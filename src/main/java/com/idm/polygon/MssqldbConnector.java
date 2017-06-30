/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idm.polygon;

import com.idm.polygon.methods.CreateUser;
import com.idm.polygon.methods.DeleteUser;
import com.idm.polygon.methods.UpdateUser;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.SearchResultsHandler;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.objects.Schema;



import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ConnectorClass(displayNameKey = "mssqldb.connector.display", configurationClass = MssqldbConfiguration.class)
public class MssqldbConnector implements Connector, TestOp, CreateOp, DeleteOp, UpdateOp, SchemaOp, SearchOp<String> {

    private static final Logger LOG = new Logger();


    private MssqldbConfiguration configuration;
    private MssqldbConnection connection;

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PASS_EXP = "passExpires";
    public static final String STATUS = "status";
    public static final String PASS = "password";

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        try {
            LOG.write("Attempting to initiate connection to resource.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.configuration = (MssqldbConfiguration)configuration;
        try {
            this.connection = new MssqldbConnection(this.configuration);
        }
        catch (Exception e) {
            LOG.write("Unable to establish connection to resource!");
            throw new ConnectorException("Error in connection process:" + e.getMessage());
        }

        LOG.write("Connection sucessful. Connection object is:" + this.connection.toString());
    }

    @Override
    public void dispose() {
        configuration = null;
        if (connection != null) {
            connection.dispose();
            connection = null;
        }
    }

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> set, OperationOptions operationOptions) {
        LOG.write("Attempting to create user.");
        Uid uidResult = null;

        if (objectClass == null) {
            throw new ConnectorException("Unable to create new user, no object class was specified.");
        }

        try {
            uidResult = new CreateUser(objectClass, connection, configuration, set).create();
        } catch (Exception e) {
            LOG.write("Error creating user." + e.toString());
        }
        return uidResult;
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        LOG.write("Attempting to DELETE user.");
        DeleteUser delete = new DeleteUser(objectClass, uid, connection, configuration);
        try {
            delete.delete();
        } catch (Exception e) {
            LOG.write(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void test() {
        LOG.write("Testing database connection...");

        try {
            connection.disconnect();
        }
        catch (Exception e) {
            LOG.write(e.toString());
        }

        try {
            connection.testConnection();
        } catch (SQLException e) {
            LOG.write("Error testing connection" + e.toString());
            try {
                throw new SQLException(e.getMessage());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> set, OperationOptions operationOptions) {
        LOG.write("Attempting to UPDATE user....");
        Uid uidResult = null;

        if (objectClass == null) {
            throw new ConnectorException("Unable to update user, no object class was specified.");
        }

        try {
            uidResult = new UpdateUser(objectClass, connection, configuration, set, uid).update();
        } catch (Exception e) {
            LOG.write("Error creating user." + e.toString());
        }
        return uidResult;

    }

    @Override
    public Schema schema() {
        final SchemaBuilder builder = new SchemaBuilder(MssqldbConnector.class);

        buildAccountObjectClass(builder);
        buildGroupObjectClass(builder);

        Schema schema = builder.build();

        return schema;
    }

    public void buildAccountObjectClass(SchemaBuilder builder) {
        ObjectClassInfoBuilder objectClassBuilder = new ObjectClassInfoBuilder();
        objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.define(FIRST_NAME).setRequired(true).build());
        objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(LAST_NAME));
        objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(PASS_EXP, Boolean.TYPE));
        objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(STATUS, Boolean.TYPE));
        objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(PASS));

        builder.defineObjectClass(objectClassBuilder.build());
    }

    public void buildGroupObjectClass(SchemaBuilder builder) {
        ObjectClassInfoBuilder objectClassInfoBuilder = new ObjectClassInfoBuilder();
        objectClassInfoBuilder.setType(ObjectClass.GROUP_NAME);

        builder.defineObjectClass(objectClassInfoBuilder.build());
    }


    @Override
    public FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        LOG.write("Inside filter translator.....");
        LOG.write("Operation options: "+operationOptions.toString());

        return new AbstractFilterTranslator<String>() {

        };
    }

    @Override
    public void executeQuery(ObjectClass objectClass, String s, ResultsHandler resultsHandler, OperationOptions operationOptions) {
        LOG.write("Attempting to execute search query....");
        LOG.write("Parameters received: s: "+s+" ResultsHandler: "+resultsHandler.toString()+" operationOptions: "+operationOptions.toString());
        if (objectClass.equals(ObjectClass.GROUP)) {
            LOG.write("Object class for search query is GROUP.");
            String getGroupsQuery = "SELECT "+configuration.getGroupKeyField()+", "+configuration.getGroupNameField()+" FROM "+configuration.groupTable+";";
            Statement stmt = null;
            ResultSet rs = null;
            List<String> header = new ArrayList<String>();
            try {
                stmt = connection.getInitializedConnection().createStatement();
                LOG.write(stmt.toString());
            }
            catch (SQLException e) {
                LOG.write("Problem obtaining open connection while attempting to execute GROUPS query.");
            }
            try {
                rs = stmt.executeQuery(getGroupsQuery);
                //take in rs and return ConnectorObject (equal to one record)

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                //Create list of headers
                for (int i = 1; i <= columnCount; i++ ) {
                    String name = rsmd.getColumnName(i);
                    header.add(name);
                }
                LOG.write("Header values: " + header.toString());

                List<String> record = new ArrayList<String>();
                while(rs.next()) {
                    for (int i = 1; i <= columnCount; i++ ) {
                        record.add(rs.getString(i));
                    }
                    ConnectorObject obj = createConnectorObject(header, record);
                    resultsHandler.handle(obj);
                    record.clear();
                }

                /*
                while (rs.next()) {
                    LOG.write((rs.getString(1) + "  " + rs.getString(2)+ "  "+rs.getString(3)));
                    record.add(rs.getString(1));
                    record.add(rs.getString(2));
                    record.add(rs.getString(3));

                    ConnectorObject obj = createConnectorObject(record);
                }
                */
            }
            catch (SQLException e) {
                LOG.write(e.toString());
            }
            catch (Exception e) {
                LOG.write((e.toString()));
            }
            /*
            SearchResult searchResult = new SearchResult(null, 0, true);
            ((SearchResultsHandler)resultsHandler).handleResult(searchResult);
            */
        }

    }

    public ConnectorObject createConnectorObject(List<String> header, List<String> record) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();

        LOG.write("Values received: "+record.toString());

        String columnName = null;

        for (int i = 0; i < record.size(); i++) {
            String name = header.get(i);
            String value = record.get(i);

            if (StringUtil.isEmpty(value)) {
                continue;
            }
            if (name.equals(configuration.getGroupKeyField())) {
                builder.setUid(value);
                columnName = "uid";
                continue;
            }
            if (name.equals(configuration.getGroupNameField())) {
                builder.setName(value);
                columnName = "name";
                continue;
            }
            builder.addAttribute(columnName, createAtrributeValues(value));
        }
        return builder.build();
    }

    private List<String> createAtrributeValues(String attributeValue) {
        List<String> values = new ArrayList<String>();
        values.add(attributeValue);

        return values;
    }
}

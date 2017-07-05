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

    public static final String FIRST_NAME = "nombre";
    public static final String LAST_NAME = "apellido";
    public static final String PASS_EXP = "passExpires";
    public static final String STATUS = "activo";
    //Attribute that receives user's ID when dealing with entitlement assignment.
    //Should receive the user's UID.
    public static final String ACCOUNT_ASSOCIATION_ID = "userAssociationID";

    public static final String GROUP_DESCRIP = "descripcion";
    public static final String GROUP_MEMBERS = "members";

    //public static final String PASS = "password";


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
            connection.disconnect();
            connection.dispose();

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
        //objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(STATUS, Boolean.TYPE));
        //objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(PASS));
        objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(ACCOUNT_ASSOCIATION_ID, Boolean.TYPE));




        builder.defineObjectClass(objectClassBuilder.build());
    }

    public void buildGroupObjectClass(SchemaBuilder builder) {
        ObjectClassInfoBuilder objectClassInfoBuilder = new ObjectClassInfoBuilder();
        objectClassInfoBuilder.setType(ObjectClass.GROUP_NAME);
        objectClassInfoBuilder.addAttributeInfo(AttributeInfoBuilder.build(GROUP_DESCRIP));
        objectClassInfoBuilder.addAttributeInfo(AttributeInfoBuilder.build(GROUP_MEMBERS));

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
        String getQuery = null;
        if (objectClass.equals(ObjectClass.GROUP)) {
            LOG.write("Object class for search query is GROUP.");
            //String getGroupsQuery = "SELECT "+configuration.getGroupKeyField()+", "+configuration.getGroupNameField()+" FROM "+configuration.getGroupTable+";";
            getQuery = "SELECT * FROM " + configuration.getGroupTable() + ";";
        }
        else if (objectClass.equals(ObjectClass.ACCOUNT)) {
            LOG.write("Object class for search query is ACCOUNT.");
            getQuery = "SELECT "+configuration.getUserNameField()+", "+configuration.getFirstNameField()+
                    ", "+configuration.getLastNameField()+" FROM "+configuration.getUserTable()+";";
        }
            Statement stmt = null;
            ResultSet rs = null;
            List<String> header = new ArrayList<String>();
            try {
                stmt = connection.getInitializedConnection().createStatement();
                LOG.write(stmt.toString());
            }
            catch (SQLException e) {
                LOG.write("Problem obtaining open connection while attempting to execute Reconciliation query.");
            }
            try {
                rs = stmt.executeQuery(getQuery);
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
                    ConnectorObject obj = createConnectorObject(objectClass, header, record, stmt);
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
            finally {

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        LOG.write(e.toString());
                    }
                }
            }
            /*
            SearchResult searchResult = new SearchResult(null, 0, true);
            ((SearchResultsHandler)resultsHandler).handleResult(searchResult);
            */

    }

    public ConnectorObject createConnectorObject(ObjectClass objectClass, List<String> header, List<String> record, Statement stmt) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();

        LOG.write("Values received: "+record.toString());

        //String columnName = null;

        for (int i = 0; i < record.size(); i++) {
            String name = header.get(i);
            String value = record.get(i);

            if (StringUtil.isEmpty(value)) {
                continue;
            }
            if (objectClass.equals(ObjectClass.GROUP)) {
                if (name.equals(configuration.getGroupKeyField())) {
                    builder.setUid(value);
                    //columnName = "uid";
                    continue;
                }
                if (name.equals(configuration.getGroupNameField())) {
                    builder.setName(value);
                    //columnName = "name";
                    continue;
                }

            }
            else if (objectClass.equals(ObjectClass.ACCOUNT)) {
                if (name.equals(configuration.getUserNameField())) {
                    builder.setUid(value);
                    builder.setName(value);
                    continue;
                }
            }
            builder.addAttribute(name, createAtrributeValues(value));
        }

        //Add to group object, the accounts assigned to the group
        try {
            AttributeBuilder memberAttributeBuilder = new AttributeBuilder();
            memberAttributeBuilder.setName(GROUP_MEMBERS);
            //Get list of members
            List<String> memberList = getGroupMembers(configuration.getGroupKeyField(), stmt);
            for (String member : memberList) {
                memberAttributeBuilder.addValue(member);
            }
            builder.addAttribute(memberAttributeBuilder.build());
        }
        catch (Exception e) {
            LOG.write("Error getting list of members assigned to group on resource.");
        }

        return builder.build();
    }

    private List<String> getGroupMembers(String groupKeyField, Statement stmt) {
        //AttributeBuilder memberAttrBuilder = new AttributeBuilder();
        //memberAttrBuilder.setName(GROUP_MEMBERS);


        String getGroupMembersQuery = "SELECT "+configuration.getUserNameField()+" FROM "+configuration.getRelationTable()+
                " WHERE "+configuration.getGroupKeyField()+" = "+"'"+groupKeyField+"';";
        ResultSet rs = null;
        List<String> members = null;
        try {
            rs = stmt.executeQuery(getGroupMembersQuery);
        } catch (SQLException e) {
            LOG.write("Error executing query to obtain group members");
            LOG.write(e.toString());
        }

        int i = 0;
        try {
            while(rs.next()) {

                //System.out.println(rs.getString(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
                System.out.println(rs.getString(i));
                members.add(rs.getString(i));
                //memberAttrBuilder.addValue(rs.getString(i));
                i ++;
            }
        } catch (SQLException e) {
            LOG.write(e.toString());
        }
        catch (Exception e) {
            LOG.write(e.toString());
        }

        return members;
    }

    private List<String> createAtrributeValues(String attributeValue) {
        List<String> values = new ArrayList<String>();
        values.add(attributeValue);

        return values;
    }
}

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

import com.idm.polygon.utilities.Logger;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.objects.Schema;
import sun.security.util.Password;

import java.sql.SQLException;
import java.util.Set;

@ConnectorClass(displayNameKey = "mssqldb.connector.display", configurationClass = MssqldbConfiguration.class)
public class MssqldbConnector implements Connector, TestOp, CreateOp, DeleteOp, UpdateOp, SchemaOp {

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
        return null;
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {

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
        return null;
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
}

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
import com.idm.polygon.utilities.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MssqldbConnection {

    private static final Logger LOG = new Logger();

    private MssqldbConfiguration configuration;
    private Connection connection;

    public MssqldbConnection(MssqldbConfiguration configuration) throws SQLException {

        this.configuration = configuration;
        connect(configuration);
    }

    public Connection connect(final MssqldbConfiguration configuration) throws SQLException {
        LOG.write("Attempting to initiate connection to database.");

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            connection= DriverManager.getConnection(
                    "jdbc:sqlserver://"+configuration.getHost()+":"+configuration.getPort()+";databaseName="+configuration.getDbName(),
                            configuration.getUser(), Utilities.getPlainPassword(configuration.getPassword()));

            LOG.write("Connected to database : "+connection.toString());
        }
        catch (SQLException e) {
            throw new SQLException("Failed to connect to database.", e.getMessage());
        }
        catch (Exception e) {
            LOG.write(e.getMessage().toString());
        }

        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && connection.isValid(10));
            connection.close();
        }
        catch (SQLException e) {
            LOG.write(e.toString());
        }
        connection = null;
        LOG.write("Disconnected from database.");
    }

    public Connection getInitializedConnection() throws SQLException {
        if (connection == null || !connection.isValid(10)) {
            connection = connect(configuration);
        }

        return connection;
    }

    public void testConnection() throws SQLException {
        Connection connection = getInitializedConnection();
        if (connection == null || !connection.isValid(10)) {
            LOG.write("No existing connection. Attempting to reconnect...");
            connection = connect(configuration);
        }

    }

    public boolean checkAlive() throws SQLException {
        System.out.println("In checkAlive method. Testing connection...");
        if (connection.equals(null) || !connection.isValid(10)) {
            return false;
        }
        System.out.println("Connection object is: " + connection.toString());
        return connection.isValid(10);
    }

    public void dispose() {
        //todo implement
    }
}
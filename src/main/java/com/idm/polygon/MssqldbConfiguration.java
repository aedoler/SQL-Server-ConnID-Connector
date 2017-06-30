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
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class MssqldbConfiguration extends AbstractConfiguration {

    private static final Logger LOG = new Logger();

    private String host;
    private String dbName;
    private String userTable;
    private String groupTable;
    private String user;
    private GuardedString password;
    private String port;

    //Table fields
    private String userNameField;
    private String firstNameField;
    private String lastNameField;
    private String passExpiresField;
    private String statusField;
    private String passwordField;

    private String groupNameField;
    private String groupKeyField;

    @Override
    public void validate() {
        //todo implement
    }

    public MssqldbConfiguration() {}

    @ConfigurationProperty(order=1, displayMessageKey = "host.display",
            helpMessageKey = "host.help", required = true)

    public String getHost() {return host;}

    public void setHost(String host) {
        this.host = host;
    }

    @ConfigurationProperty(order=2, displayMessageKey = "port.display",
            helpMessageKey = "port.help", required = true)

    public String getPort() {return port;}

    public void setPort(String port) {
        this.port = port;
    }

    @ConfigurationProperty(order=3, displayMessageKey = "dbname.display",
            helpMessageKey = "dbname.help", required = true)

    public String getDbName() {return dbName;}

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @ConfigurationProperty(order=4, displayMessageKey = "userTable.display",
            helpMessageKey = "userTable.help", required = true)

    public String getUserTable() {return userTable;}

    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }

    @ConfigurationProperty(order=5, displayMessageKey = "groupTable.display",
            helpMessageKey = "groupTable.help", required = true)

    public String getGroupTable() {return groupTable;}

    public void setGroupTable(String groupTable) {
        this.groupTable = groupTable;
    }

    @ConfigurationProperty(order=6, displayMessageKey = "user.display",
            helpMessageKey = "user.help", required = false)

    public String getUser() {return user;}

    public void setUser(String user) {
        this.user = user;
    }

    @ConfigurationProperty(order=7, displayMessageKey = "password.display",
            helpMessageKey = "password.help", required = true)

    public GuardedString getPassword() {return password;}

    public void setPassword(GuardedString password) {
        this.password = password;
    }

    @ConfigurationProperty(order=8, displayMessageKey = "userNameField.display",
            helpMessageKey = "userNameField.help", required = true)

    public String getUserNameField() {return userNameField;}

    public void setUserNameField(String userNameField) {
        this.userNameField = userNameField;
    }

    @ConfigurationProperty(order=9, displayMessageKey = "firstNameField.display",
            helpMessageKey = "firstNameField.help", required = true)

    public String getFirstNameField() {return firstNameField;}

    public void setFirstNameField(String firstNameField) {
        this.firstNameField = firstNameField;
    }

    @ConfigurationProperty(order=10, displayMessageKey = "lastNameField.display",
            helpMessageKey = "lastNameField.help", required = false)

    public String getLastNameField() {return lastNameField;}

    public void setLastNameField(String lastNameField) {
        this.lastNameField = lastNameField;
    }

    @ConfigurationProperty(order=11, displayMessageKey = "passExpiresField.display",
            helpMessageKey = "passExpiresField.help", required = false)

    public String getPassExpiresField() {return passExpiresField;}

    public void setPassExpiresField(String passExpiresField) {
        this.passExpiresField = passExpiresField;
    }

    @ConfigurationProperty(order=12, displayMessageKey = "statusField.display",
            helpMessageKey = "statusField.help", required = false)

    public String getStatusField() {return statusField;}

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }

    @ConfigurationProperty(order=13, displayMessageKey = "passwordField.display",
            helpMessageKey = "passwordField.help", required = false)

    public String getPasswordField() {return passwordField;}

    public void setPasswordField(String passwordField) {
        this.passwordField = passwordField;
    }

    @ConfigurationProperty(order=14, displayMessageKey = "groupNameField.display",
            helpMessageKey = "groupNameField.help", required = true)

    public String getGroupNameField() {return groupNameField;}

    public void setGroupNameField(String groupNameField) {
        this.groupNameField = groupNameField;
    }

    @ConfigurationProperty(order=15, displayMessageKey = "groupKeyField.display",
            helpMessageKey = "groupKeyField.help", required = true)

    public String getGroupKeyField() {return groupKeyField;}

    public void setGroupKeyField(String groupKeyField) {
        this.groupKeyField = groupKeyField;
    }


}
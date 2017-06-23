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

package com.deloitte.idm;

import com.deloitte.idm.Utilities.Logger;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class MssqldbConfiguration extends AbstractConfiguration {

    private static final Logger LOG = new Logger();

    //Variables are public currently for testing only
    public String host;
    public String dbName;
    public String user;
    public String password;
    public String port;

    @Override
    public void validate() {
        //todo implement
    }

    public MssqldbConfiguration() {}

    @ConfigurationProperty(order=3, displayMessageKey = "dbname.display",
            helpMessageKey = "dbname.help", required = true)

    public String getDbName() {return dbName;}

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @ConfigurationProperty(order=4, displayMessageKey = "user.display",
            helpMessageKey = "user.help", required = true)

    public String getUser() {return user;}

    public void setUser(String user) {
        this.user = user;
    }

    @ConfigurationProperty(order=5, displayMessageKey = "password.display",
            helpMessageKey = "password.help", required = true)

    public String getPassword() {return password;}

    public void setPassword(String password) {
        this.password = password;
    }

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

}
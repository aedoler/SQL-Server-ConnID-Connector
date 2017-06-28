package com.idm.polygon;

import org.identityconnectors.framework.common.objects.Attribute;

import java.sql.SQLException;
import java.util.Set;

/**
 * Created by adoler on 23/6/2017.
 */
public class TestConnection {


    public static void main(String[] args) throws SQLException {
        test();
    }

    public static boolean test() throws SQLException {
        MssqldbConfiguration configuration = new MssqldbConfiguration();

        //Set configuration variables. Complete only for testing purposes.
        MssqldbConnector connector = new MssqldbConnector();
        MssqldbConnection connection = new MssqldbConnection(configuration);

        connector.test();
        boolean checkConnection = connection.checkAlive();

        if (checkConnection == false) {
            System.out.println("No existing connection. Connection test failed.");
            return false;
        }

        if (checkConnection == true) {
            System.out.println("Test connection sucessfull!");
        }
        return checkConnection;
    }

    public static void testUser() {

        //Set<Attribute> attrs = ["Attribute": {"Name"="password", Value=[aedoler@gmail.com]}, Attribute: {Name=lastName, Value=[Doler]}, Attribute: {Name=__NAME__, Value=[aedoler@gmail.com]}, Attribute: {Name=firstName, Value=[Alexander]}];
    }

}

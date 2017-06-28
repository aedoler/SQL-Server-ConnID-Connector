package com.idm.polygon.utilities;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.ObjectClass;
import com.idm.polygon.utilities.Logger;

import java.util.List;

/**
 * Created by adoler on 23/6/2017.
 */
public class Utilities {

    private static final Logger LOG = new Logger();

    public static String getPlainPassword(final GuardedString password) {
        if (password == null){
            return null;
        }

        final StringBuilder builder = new StringBuilder();

        password.access(new GuardedString.Accessor() {

            @Override
            public void access(final char[] clearChars) {
                builder.append(clearChars);
            }
        });
        return builder.toString();
    }

    public static boolean checkOccurence(Integer occurence,
                                         List<Object> values) {

        if (occurence == 1){
            if (values != null && !values.isEmpty()){
                return values.size() == 1;
            }
        }

        return true;

    }

    public static String getObjectTable(ObjectClass objectClass, MssqldbConfiguration configuration) {
        try {
            if (objectClass.equals(ObjectClass.ACCOUNT)) {
                LOG.write("Got account type. Updating user account table.");
                return configuration.getUserTable();
            } else if (objectClass.equals(ObjectClass.GROUP)) {
                LOG.write("Got group type. Updating group table.");
                return configuration.getGroupTable();
            }
        }
        catch (Exception e) {
            LOG.write("Problem setting object type in order to determine table.");
        }

        return null;
    }

    public static String getUpdateField(ObjectClass objectClass, MssqldbConfiguration configuration) {
        try {
            if (objectClass.equals(ObjectClass.ACCOUNT)) {
                LOG.write("Got account type. Updating account for " + configuration.getUserNameField());
                return configuration.getUserNameField();
            } else if (objectClass.equals(ObjectClass.GROUP)) {
                LOG.write("Got group type. Updating group for " + configuration.getGroupNameField());
                return configuration.getGroupNameField();
            }
        }
        catch (Exception e) {
            LOG.write("Problem setting object type in order to determine UPDATE field for WHERE clause.");
        }

        return null;
    }

    public static boolean checkRecordExists(String objectName, MssqldbConfiguration configuration, MssqldbConnection connection) {
        String checkUserExistsQuery = "SELECT COUNT(1) from "+configuration.getUserTable()+" WHERE "+configuration.getUserNameField()+" = "+objectName;
        if (checkUserExistsQuery == "1") {
            return true;
        }
        else if (checkUserExistsQuery == "0") {
            return false;
        }
        return false;
    }

}
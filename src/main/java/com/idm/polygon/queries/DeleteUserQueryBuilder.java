package com.idm.polygon.queries;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.sql.SQLException;
import java.util.Set;

/**
 * Created by adoler on 28/6/2017.
 */
public class DeleteUserQueryBuilder {
    private static final Logger LOG = new Logger();

    Set<Attribute> attrs;
    String objectName;
    MssqldbConfiguration configuration;
    ObjectClass objectClass;

    public DeleteUserQueryBuilder(ObjectClass objectClass, String objectName, MssqldbConfiguration configuration) {
        this.objectName = objectName;
        this.configuration = configuration;
        this.objectClass = objectClass;
    }

    public String getQuery() throws Exception {
        String deleteQuery = null;
        try {
            deleteQuery = "DELETE FROM "+configuration.getUserTable()+" WHERE "+configuration.getUserNameField()+" = "+"'"+objectName+"'"+";";
        }
        catch (Exception e) {
            LOG.write("Error forming DELETE query." + e.toString());
        }
        return deleteQuery;
    }
}

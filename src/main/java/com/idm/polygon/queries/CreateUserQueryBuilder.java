package com.idm.polygon.queries;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.utilities.Logger;
import com.idm.polygon.utilities.Utilities;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;

import java.util.List;
import java.util.Set;

/**
 * Created by adoler on 26/6/2017.
 */
public class CreateUserQueryBuilder {

    private static final Logger LOG = new Logger();

    Set<Attribute> attrs;
    String objectName;
    MssqldbConfiguration configuration;

    public CreateUserQueryBuilder(String objectName, MssqldbConfiguration configuration, Set<Attribute> attrs) {
        this.attrs = attrs;
        this.objectName = objectName;
        this.configuration = configuration;
    }

    public String getQuery() throws Exception {
        StringBuilder query = new StringBuilder();
        StringBuilder values = new StringBuilder();
        query.append(getInsert());
        values.append(getValues());

        LOG.write("Attributes received:" + attrs.toString());
        LOG.write("Starting query creation.");

        int counter = 0;

        try {

            for (Attribute attr : attrs) {
                if (attr.getName().equals("nombre")) {
                    query.append(configuration.getFirstNameField());
                    values.append("'"+AttributeUtil.getAsStringValue(attr)+"'");
                } else if (attr.getName().equals("apellido")) {
                    query.append(configuration.getLastNameField());
                    values.append("'"+AttributeUtil.getAsStringValue(attr)+"'");
                } else if (attr.getName().equals("passExpires")) {
                    query.append(configuration.getPassExpiresField());
                    values.append("'"+AttributeUtil.getAsStringValue(attr)+"'");
                } else if (attr.getName().equals("__ENABLE__")) {
                    query.append(configuration.getStatusField());
                    values.append("'"+AttributeUtil.getAsStringValue(attr)+"'");
                } else if (attr.getName().equals("__PASSWORD__")) {
                    query.append(configuration.getPasswordField());
                    values.append("'"+ Utilities.getPlainPassword(AttributeUtil.getPasswordValue(attrs))+"'");
                } else if (attr.getName().equals("__NAME__")) {
                    counter ++;
                    continue;
                }
                counter++;

                if (counter == attrs.size()) {
                    query.append(") ");
                    values.append(");");
                    break;
                }

                if (counter != (attrs.size() - 1) || counter != attrs.size()) {
                    query.append(", ");
                    values.append(", ");
                }

            }

            query.append(values);
        }
        catch (Exception e) {
            LOG.write("Error thrown while creating create user query" + e.toString() + ", ");
            throw new Exception(e.getMessage());
        }

        return query.toString();
    }

    private String getInsert() {
        String insert = "INSERT INTO " + configuration.getUserTable() + " (" + configuration.getUserNameField() + ", ";
        return insert;
    }

    private String getValues() {
        String values = "VALUES (" + "'"+objectName+"'"+", ";
        return values;
    }
}

package com.idm.polygon.queries;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnector;
import com.idm.polygon.utilities.Logger;
import com.idm.polygon.utilities.Utilities;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationalAttributes;

import java.util.Set;

/**
 * Created by adoler on 27/6/2017.
 */
public class UpdateUserQueryBuilder {

    private static final Logger LOG = new Logger();

    Set<Attribute> attrs;
    String objectName;
    MssqldbConfiguration configuration;
    ObjectClass objectClass;

    public UpdateUserQueryBuilder(ObjectClass objectClass, String objectName, MssqldbConfiguration configuration, Set<Attribute> attrs) {
        this.attrs = attrs;
        this.objectName = objectName;
        this.configuration = configuration;
        this.objectClass = objectClass;
    }

    public String getQuery() throws Exception {
        LOG.write("Starting UPDATE creation.");
        LOG.write("Update Attributes received: " + attrs.toString());

        StringBuilder query = new StringBuilder();
        query.append(getUpdate());
        query.append(getSetValues());
        query.append(getWhere());

        return query.toString();
    }

    public String getUpdate() {
        String update = "UPDATE " + configuration.getUserTable();
        return update;
    }

    public String getSetValues() {
        StringBuilder setValues = new StringBuilder();
        String set = " SET ";
        setValues.append(set);

        StringBuilder values = new StringBuilder();
        int counter = 0;

        try {
            for (Attribute attr : attrs) {
                if (attr.getName().equals("__NAME__")) {
                    counter ++;
                    continue;
                }
                if (attr.getName().equals("__PASSWORD__")) {
                    values.append(configuration.getPasswordField()+" = "+"'"+Utilities.getPlainPassword(AttributeUtil.getPasswordValue(attrs))+"'");
                }
                if (attr.getName().equals("nombre")) {
                    values.append(configuration.getFirstNameField()+" = "+"'"+AttributeUtil.getAsStringValue(attr)+"'");
                }
                if (attr.getName().equals("apellido")) {
                    values.append(configuration.getLastNameField()+" = "+"'"+AttributeUtil.getAsStringValue(attr)+"'");
                }
                /*if (attr.getName().equals("__ENABLE__")) {
                    Attribute status = AttributeUtil.find(OperationalAttributes.ENABLE_NAME, attrs);
                    boolean statusValue = ((Boolean) status.getValue().get(0)).booleanValue();
                }
                */
                //values.append(attr.getName()+" = "+"'"+AttributeUtil.getAsStringValue(attr)+"'");
                counter ++;
                if (counter != attrs.size())
                        values.append(", ");
                else if (counter == attrs.size()) {
                    break;
                }
            }
            setValues.append(values);
        }
        catch (Exception e) {
            LOG.write("Error generating update values." + e.toString());
        }

        return setValues.toString();
    }

    public String getWhere() {

        String whereClause = null;
        try {
            whereClause = " WHERE " + configuration.getUserNameField() + " = " + "'" + objectName + "'" + ";";
        }

        catch (Exception e) {
            LOG.write("Error setting WHERE clause " + e.toString());
        }

        return whereClause;
    }

}

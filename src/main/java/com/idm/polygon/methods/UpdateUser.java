package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.MssqldbConnector;
import com.idm.polygon.queries.UpdateUserQueryBuilder;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Created by adoler on 26/6/2017.
 */
public class UpdateUser {

    private static final Logger LOG = new Logger();
    private Set<Attribute> attrs = null;
    private MssqldbConnection connection;
    private ObjectClass objectClass = null;
    private MssqldbConfiguration configuration;
    private Uid uid;


    public UpdateUser(final ObjectClass objectClass, final MssqldbConnection connection,
                      final MssqldbConfiguration configuration, final Set<Attribute> attributes, final Uid uid) {
        this.attrs = attributes;
        this.connection = connection;
        this.objectClass = objectClass;
        this.configuration = configuration;
        this.uid = uid;

    }

    public Uid update() throws Exception {
        try {
            return doUpdate();
        }
        /*
        catch (IOException e) {
            LOG.write(e.toString());
            throw new IOException(e.getMessage());
        }
        catch (SQLException e) {
            LOG.write(e.toString());
            throw new SQLException(e.getMessage());
        } */
        catch (Exception e) {
            LOG.write(e.toString());
            throw new Exception(e.getMessage());
        }

    }

    public Uid doUpdate() throws Exception {
        if (!objectClass.equals(ObjectClass.ACCOUNT) && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }
        LOG.write("Update Attributes received: " + attrs.toString());


            LOG.write("Update user: " + uid.getUidValue());

            final Name newUserName = AttributeUtil.getNameFromAttributes(attrs);
            String newUserNameValue = null;

            if (newUserName != null && StringUtil.isNotBlank(newUserName.getNameValue())) {
                newUserNameValue = newUserName.getNameValue();
            } else {
                newUserNameValue = uid.getUidValue();
            }
        if (objectClass.equals(ObjectClass.ACCOUNT)) {
            String updateQuery = null;

            //Check whether trying to assign entitlement to user or update user attributes
            LOG.write("Checking UPDATE operation type.");
            for (Attribute attr : attrs) {
                if (attr.getName().equals(MssqldbConnector.ACCOUNT_ASSOCIATION_ID)) {
                    LOG.write("Update type is ASSIGN ENTITLEMENT");
                    updateQuery = "INSERT INTO " + configuration.getRelationTable() + " (" + configuration.getGroupKeyField() +
                            ", " + configuration.getUserNameField() + ") VALUES ('" + newUserNameValue + "', '" +
                            AttributeUtil.getAsStringValue(attr) + "');";
                } else {
                    LOG.write("Update type is USER UPDATE.");
                    UpdateUserQueryBuilder query = new UpdateUserQueryBuilder(objectClass, newUserNameValue, configuration, attrs);
                    updateQuery = query.getQuery();
                }
            }


            LOG.write("Attempting to UPDATE user account.");
            Statement stmt = null;
            try {
                stmt = connection.getInitializedConnection().createStatement();
                LOG.write(stmt.toString());
            } catch (SQLException e) {
                LOG.write("Problem obtaining open connection while attempting to update user.");
            }

            LOG.write("Attempting to create UPDATE statement..." + updateQuery);

            //Insert user

            try {
                stmt.executeUpdate(updateQuery);
            } catch (SQLException e) {
                LOG.write(e.toString());
                throw new SQLException(e.getMessage());
            } catch (Exception e) {
                LOG.write((e.toString()));
                throw new Exception(e.getMessage());
            } finally {

                if (stmt != null) {
                    stmt.close();
                }
            }
            //Add user status
            try {
                ProcessUserStatus processUserStatus = new ProcessUserStatus(connection, configuration, attrs, newUserNameValue.toString());
                processUserStatus.processActivation();
            } catch (Exception e) {
                LOG.write("Failed to add user STATUS." + e.toString());
            }

        }

        else if (objectClass.equals(ObjectClass.GROUP)) {
            LOG.equals("Object class type is GROUP");

        }

        return new Uid(newUserNameValue);
    }
}

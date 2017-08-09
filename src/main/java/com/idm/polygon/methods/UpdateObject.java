package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.MssqldbConnector;
import com.idm.polygon.queries.ModifyUserGroupsBuilder;
import com.idm.polygon.queries.UpdateUserQueryBuilder;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Created by adoler on 26/6/2017.
 */
public class UpdateObject {

    private static final Logger LOG = new Logger();
    private Set<Attribute> attrs = null;
    private MssqldbConnection connection;
    private ObjectClass objectClass = null;
    private MssqldbConfiguration configuration;
    private Uid uid;


    public UpdateObject(final ObjectClass objectClass, final MssqldbConnection connection,
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

        final Name newObjectName = AttributeUtil.getNameFromAttributes(attrs);
        String newObjectNameValue = null;

        if (newObjectName != null && StringUtil.isNotBlank(newObjectName.getNameValue())) {
            newObjectNameValue = newObjectName.getNameValue();
        } else {
            newObjectNameValue = uid.getUidValue();
        }

        Statement stmt = null;
        try {
            stmt = connection.getInitializedConnection().createStatement();
            LOG.write(stmt.toString());
        } catch (SQLException e) {
            LOG.write("Problem obtaining open connection while attempting to update user.");
        }
        String updateQuery =  null;
        if (objectClass.equals(ObjectClass.ACCOUNT)) {

            //Check whether trying to assign entitlement to user or update user attributes
            /*
            LOG.write("Checking UPDATE operation type.");
            for (Attribute attr : attrs) {
                if (attr.getName().equals(MssqldbConnector.ACCOUNT_ASSOCIATION_ID)) {
                    LOG.write("Update type is ASSIGN ENTITLEMENT");
                    updateQuery = "INSERT INTO " + configuration.getRelationTable() + " (" + configuration.getGroupKeyField() +
                            ", " + configuration.getUserNameField() + ") VALUES ('" + newObjectNameValue + "', '" +
                            AttributeUtil.getAsStringValue(attr) + "');";
                } else {
                */
            UpdateUserQueryBuilder query = new UpdateUserQueryBuilder(objectClass, newObjectNameValue, configuration, attrs);
            updateQuery = query.getQuery();

            //Add user status
            try {
                ProcessUserStatus processUserStatus = new ProcessUserStatus(connection, configuration, attrs, newObjectNameValue.toString());
                processUserStatus.processActivation();
            } catch (Exception e) {
                LOG.write("Failed to add user STATUS." + e.toString());
            }

            try {
                LOG.write("Update query is:" + updateQuery);
                stmt.executeUpdate(updateQuery);
            } catch (SQLException e) {
                LOG.write(e.toString());
                throw new SQLException(e.getMessage());
            } catch (Exception e) {
                LOG.write((e.toString()));
                throw new Exception(e.getMessage());
            }
        }

        else if (objectClass.equals(ObjectClass.GROUP)) {
            LOG.write("Object class type is GROUP");
            //Check whether group exisits
            /*
            try {
                ResultSet rs = stmt.executeQuery("select count(1) from " + configuration.getGroupTable() +
                        " where " + configuration.getGroupKeyField() + " = " + uid.toString() + ";");
            }
            catch (SQLException e) {

            }
            */
            for (Attribute attr : attrs) {
                if (attr.getName().equals(MssqldbConnector.GROUP_MEMBERS)) {
                    LOG.write("Update type is MODIFY USER ENTITLEMENT");
                    LOG.write("Updating members for group object: "+uid.getUidValue());
                    ModifyUserGroupsBuilder query = new ModifyUserGroupsBuilder(connection, objectClass, uid.getUidValue(), configuration, attr);
                    try {
                        query.modifyUserGroups();
                    }
                    catch (Exception e) {
                        LOG.write("Error while trying to modify groups. " + e.getMessage());
                        throw e;
                    }

                } /*else {
                    LOG.write("Update type is USER UPDATE.");
                    UpdateUserQueryBuilder query = new UpdateUserQueryBuilder(objectClass, newObjectNameValue, configuration, attrs);
                    updateQuery = query.getQuery();
                }*/
            }
        }

            if (stmt != null) {
                stmt.close();
            }


        return new Uid(newObjectNameValue);
    }
}

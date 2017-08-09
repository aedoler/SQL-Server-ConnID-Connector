package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.queries.DeleteUserQueryBuilder;
import com.idm.polygon.queries.UpdateUserQueryBuilder;
import com.idm.polygon.utilities.Logger;
import com.idm.polygon.utilities.Utilities;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Created by adoler on 27/6/2017.
 */
public class DeleteUser {

    private static final Logger LOG = new Logger();
    private MssqldbConnection connection;
    private ObjectClass objectClass = null;
    private MssqldbConfiguration configuration;
    private Uid uid;


    public DeleteUser(final ObjectClass objectClass, final Uid uid, final MssqldbConnection connection,
                      final MssqldbConfiguration configuration) {

        this.connection = connection;
        this.objectClass = objectClass;
        this.configuration = configuration;
        this.uid = uid;
    }

    public void delete() throws Exception {
        try {
            doDelete();
            LOG.write("User successfully deleted from records.");
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

    public void doDelete() throws Exception {

        if (!objectClass.equals(ObjectClass.ACCOUNT) && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }
        if (objectClass.equals(ObjectClass.ACCOUNT)) {
            //final Name name = AttributeUtil.getNameFromAttributes(attrs);
            //if (name == null || StringUtil.isBlank(name.getNameValue())) {
            //    throw new IllegalArgumentException("No Name attribute provided in the attributes");
            // }
            String objectName = uid.getUidValue();
            Statement stmt = null;
            ResultSet rs = null;

            DeleteUserQueryBuilder query = new DeleteUserQueryBuilder(objectClass, objectName, configuration);

            try {
                stmt = connection.getInitializedConnection().createStatement();

            } catch (SQLException e) {
                LOG.write("Problem obtaining open connection while attempting to update user.");
            }


            try {
                rs = stmt.executeQuery(Utilities.checkUserHasAssignment(objectName, configuration));
                while (rs.next()) {
                    if (rs.getString(1) != "0") {
                        LOG.write("Record exists");
                        stmt = connection.getInitializedConnection().createStatement();
                        stmt.executeUpdate(query.deleteUserRelationships());
                        break;
                    }
                }
            } catch (SQLException e) {
                LOG.write("Error while deleting user from relationships table. " + e.getMessage());
                throw e;
            }



            //Delete user from user records
            LOG.write("Attempting to DELETE user account.");
            try {
                stmt = connection.getInitializedConnection().createStatement();
                stmt.executeUpdate(query.getQuery());
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
        }
        else if (objectClass.equals(ObjectClass.GROUP)) {
            LOG.write("Delete group objects is not supported for Database Connector.");
            throw new UnsupportedOperationException("Delete group objects is not supported for Database Connector.");
        }
    }



}

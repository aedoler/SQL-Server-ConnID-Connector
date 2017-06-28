package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.queries.DeleteUserQueryBuilder;
import com.idm.polygon.queries.UpdateUserQueryBuilder;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.*;

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
            LOG.write("User successfully delete from records.");
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
        //final Name name = AttributeUtil.getNameFromAttributes(attrs);
        //if (name == null || StringUtil.isBlank(name.getNameValue())) {
        //    throw new IllegalArgumentException("No Name attribute provided in the attributes");
       // }
        String objectName = uid.getUidValue();

        DeleteUserQueryBuilder query = new DeleteUserQueryBuilder(objectClass, objectName, configuration);

        LOG.write("Attempting to DELETE user account.");
        Statement stmt = null;
        try {
            stmt = connection.getInitializedConnection().createStatement();
            LOG.write(stmt.toString());
        } catch (SQLException e) {
            LOG.write("Problem obtaining open connection while attempting to update user.");
        }

        LOG.write("Attempting to create DELETE statement..." + query.getQuery());

        //Insert user

        try {
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


}

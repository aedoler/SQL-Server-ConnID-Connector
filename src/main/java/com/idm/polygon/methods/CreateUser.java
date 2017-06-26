package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.queries.CreateUserQueryBuilder;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Created by adoler on 23/6/2017.
 */
public class CreateUser {

    private static final Logger LOG = new Logger();
    private Set<Attribute> attrs = null;
    private MssqldbConnection connection;
    private ObjectClass objectClass = null;
    private MssqldbConfiguration configuration;


    public CreateUser(final ObjectClass objectClass, final MssqldbConnection connection,
                      final MssqldbConfiguration configuration, final Set<Attribute> attributes) {
        this.attrs = attributes;
        this.connection = connection;
        this.objectClass = objectClass;
        this.configuration = configuration;

    }

    public Uid create() throws Exception {
        try {
            return doCreate();
        }
        catch (IOException e) {
            LOG.write(e.toString());
            throw new IOException(e.getMessage());
        }
        catch (SQLException e) {
            LOG.write(e.toString());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            LOG.write(e.toString());
            throw new Exception(e.getMessage());
        }

    }

    public Uid doCreate() throws Exception {
        if (!objectClass.equals(ObjectClass.ACCOUNT) && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }
        final Name name = AttributeUtil.getNameFromAttributes(attrs);
        if (name ==  null || StringUtil.isBlank(name.getNameValue())) {
            throw new IllegalArgumentException("No Name attribute provided in the attributes");
        }
        String objectName = name.getNameValue();

        CreateUserQueryBuilder query = new CreateUserQueryBuilder(objectName, configuration, attrs);

        if (objectClass.equals(ObjectClass.ACCOUNT)) {
            LOG.write("Attempting to create user account.");
            Statement stmt = null;
            try {
                stmt = connection.getInitializedConnection().createStatement();
                LOG.write(stmt.toString());
            }
            catch (SQLException e) {
                LOG.write("Problem obtaining open connection while attempting to create user.");
            }

            LOG.write("Attempting to create query..." + query.getQuery());


            /*
            try {
                ResultSet rs = stmt.executeQuery(query.getQuery());
                while (rs.next())
                    LOG.write(rs.getString(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }
            catch (Exception e) {
                LOG.write("Error while executing create user query." + e.getMessage().toString());
                throw new Exception(e.getMessage());
            }
            */

        }


        return new Uid(objectName);
    }
}

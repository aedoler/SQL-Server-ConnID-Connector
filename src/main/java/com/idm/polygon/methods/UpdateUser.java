package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;

import java.io.IOException;
import java.sql.SQLException;
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


    public UpdateUser(final ObjectClass objectClass, final MssqldbConnection connection,
                      final MssqldbConfiguration configuration, final Set<Attribute> attributes) {
        this.attrs = attributes;
        this.connection = connection;
        this.objectClass = objectClass;
        this.configuration = configuration;

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

    public Uid doUpdate() {

        return null;
    }
}

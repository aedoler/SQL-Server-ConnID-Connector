package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;

import java.io.IOException;
import java.util.Set;

/**
 * Created by adoler on 23/6/2017.
 */
public class CreateUser {

    private static final Logger LOG = new Logger();
    private Set<Attribute> attrs = null;
    private MssqldbConnection connection;
    private ObjectClass objectClass = null;

    public CreateUser(final ObjectClass objectClass, final MssqldbConnection connection, final Set<Attribute> attributes) {
        this.attrs = attributes;
        this.connection = connection;
        this.objectClass = objectClass;

    }

    public Uid create() throws IOException {
        try {
            return doCreate();
        }
        catch (IOException e) {
            LOG.write(e.toString());
            throw new IOException(e.getMessage());
        }

    }

    public Uid doCreate() {
        return null;
    }
}

package com.idm.polygon.methods;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnection;
import com.idm.polygon.MssqldbConnector;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.OperationalAttributes;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Created by adoler on 30/6/2017.
 */
public class ProcessUserStatus {

    private static final Logger LOG = new Logger();
    private static MssqldbConfiguration configuration;
    private static Set<Attribute> attrs = null;
    private static String username = null;
    private static MssqldbConnection connection;

    public ProcessUserStatus(final MssqldbConnection connection, final MssqldbConfiguration configuration,
                             final Set<Attribute> attrs, final String username) {
        this.configuration = configuration;
        this.attrs = attrs;
        this.username = username;
        this.connection = connection;
    }


    public void processActivation() {
        Attribute status = AttributeUtil.find(OperationalAttributes.ENABLE_NAME, ProcessUserStatus.attrs);
        //LOG.write("User status is: "+status.toString());

        boolean statusValue = ((Boolean) status.getValue().get(0)).booleanValue();
        String updateStatusQuery = null;
        if (statusValue) {
            updateStatusQuery = "UPDATE "+ configuration.getUserTable()+
                    " SET "+MssqldbConnector.STATUS+" = "+1+" WHERE "+configuration.getUserNameField()+" = "+username;
        }
        else if (!statusValue) {
            updateStatusQuery = "UPDATE "+ configuration.getUserTable()+
                    " SET "+MssqldbConnector.STATUS+" = "+0+" WHERE "+configuration.getUserNameField()+" = "+username;
        }

        try {
            Statement stmt = connection.getInitializedConnection().createStatement();
            stmt.executeUpdate(updateStatusQuery);
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            LOG.write("Erorr updating user status"+e.toString());
        }

    }
}

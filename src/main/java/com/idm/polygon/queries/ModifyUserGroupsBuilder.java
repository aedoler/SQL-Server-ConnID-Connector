package com.idm.polygon.queries;

import com.idm.polygon.MssqldbConfiguration;
import com.idm.polygon.MssqldbConnector;
import com.idm.polygon.utilities.Logger;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by adoler on 7/7/2017.
 */
public class ModifyUserGroupsBuilder {

    private static final Logger LOG = new Logger();


    Attribute attr;
    String objectName;
    MssqldbConfiguration configuration;
    ObjectClass objectClass;
    Statement stmt;

    public ModifyUserGroupsBuilder(ObjectClass objectClass, String objectName, MssqldbConfiguration configuration,
                                   Attribute attr, Statement stmt) {
        this.attr = attr;
        this.objectName = objectName;
        this.configuration = configuration;
        this.objectClass = objectClass;
        this.stmt = stmt;
    }

    public void modifyUserGroups() {
        List<Object> values = attr.getValue();
        List<String> newMemberIds = new ArrayList<String>(values.size());
        List<String> membersToAdd = new ArrayList<String>();
        List<String> membersToDelete = new ArrayList<String>();
        List<String> origMembers = new MssqldbConnector().getGroupMembers(objectName);
        //Add new members to list
        for (Object attrValue : values) {
            newMemberIds.add((String) attrValue);
        }
        LOG.write("User objects received: "+newMemberIds.toString());
        LOG.write("Users already associated with group on resource: "+origMembers.toString());

        /*
        String query = null;

        query = "SELECT "+configuration.getUserNameField()+" FROM "+configuration.getRelationTable()+" WHERE "+configuration.getGroupKeyField()+" = "+
                objectName+";";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            LOG.write("Error getting current association accounts from database. "+ e.toString());

        }
        //Create list of original members associated with group on resource
        try {
            while(rs.next()) {
                    //System.out.println(rs.getString(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
                origMembers.add(rs.getString(1));
                }
            LOG.write("Original members associented with group on resource: "+origMembers.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        //Check if user should be added to group
        for (String newMemberId : newMemberIds) {
            boolean found = false;
            for (String origMember : origMembers) {
                if (origMember == newMemberId) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                membersToAdd.add(newMemberId);
            }
        }
        LOG.write("Users to add: "+membersToAdd.toString());

        //Check if user should be delted from group
        for (String origMember : origMembers) {
            if (!newMemberIds.contains(origMember)) {
                membersToDelete.add(origMember);
            }
        }
        LOG.write("Users to delete: "+membersToDelete.toString());

        if (membersToAdd.size() != 0) {
            LOG.write("Attempting to add members to group.");

            for (String member : membersToAdd) {
                try {
                    String addUserQuery = "INSERT INTO " + configuration.getRelationTable() + " (" + configuration.getGroupKeyField() +
                            ", " + configuration.getUserNameField() + ") VALUES ('" + objectName + "', '" + member + "');";
                    LOG.write("Add user query: "+addUserQuery);
                    stmt.executeUpdate(addUserQuery);
                } catch (SQLException e) {
                    LOG.write(e.toString());
                }
            }
        }
        if (membersToDelete.size() != 0) {
            LOG.write("Attempting to delete members from group");
            for (String member : membersToDelete) {
                String deleteUserQuery = "DELETE FROM "+configuration.getRelationTable()+" WHERE "+configuration.getGroupKeyField()+
                        " = '"+objectName+"' AND WHERE "+configuration.getUserNameField()+" = '"+member+"';";
                LOG.write("Delete user query: "+deleteUserQuery);
                try {
                    stmt.executeUpdate(deleteUserQuery);
                } catch (SQLException e) {
                    LOG.write(e.toString());
                }
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        /*
        query = "INSERT INTO " + configuration.getRelationTable() + " (" + configuration.getGroupKeyField() +
                ", " + configuration.getUserNameField() + ") VALUES ('" + objectName + "', '" +
                AttributeUtil.getAsStringValue(attr) + "');";
        */
    }
}

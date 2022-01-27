package pd.Database;


import pd.models.Group;
import pd.models.Msg;
import pd.models.User;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnDB {
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/bdpd";
    private final String USERNAME = "root";
    private final String PASSWORD = "ROOT";

    private Connection dbConn;


    public ConnDB() throws SQLException {
        this.dbConn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public void close() throws SQLException {
        if (this.dbConn != null)
            this.dbConn.close();
    }

    //----------------User acess to db--------------


    public Boolean loginUser(User user) throws SQLException, SQLSyntaxErrorException {

        if (user.getUsername() == null || user.getPassword() == null)
            return false;

        String SQLquery = "SELECT password, name, id FROM user WHERE (password like '" + user.getPassword() + "') AND (name like '" + user.getUsername() + "')";
        Statement statement = dbConn.createStatement();
        ResultSet rs = statement.executeQuery(SQLquery);

        if (!rs.isBeforeFirst()) {
            System.out.println(user.getUsername() + " falhou login...");
            return false;
        }

        while (rs.next()) {
            user.setUsername(rs.getString("name"));
            user.setId(Integer.parseInt(rs.getString("id")));
        }

        if (this.setUserState(user.getId(), 1)) {
            return true;
        }

        return true;
    }

    public boolean setUserState(int id, int isLogged) {
        try {
            String SQLquery = "UPDATE user SET state = + " + isLogged + " WHERE id like " + id;

            Statement statement = dbConn.createStatement();
            int rs = statement.executeUpdate(SQLquery);
            statement.close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }


    public Boolean changeUserName(int id, String name, String newName) throws SQLException {
        if (newName == null && name == null)
            return false;

        if (id < 0) {
            String tempSQL = "SELECT id from user where name = '" + name + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                id = tempRs.getInt("id");
            }
            tempStatement.close();
        }

        Statement statement = dbConn.createStatement();
        String sqlQuery;

        sqlQuery = "UPDATE user SET name='" + newName + "' WHERE id=" + id;

        int rs = statement.executeUpdate(sqlQuery);
        statement.close();

        return rs != 0;
    }


    public List<User> getAllContacts(int id, String name) throws SQLException {
        if (name == null)
            return null;

        if (id < 0) {
            String tempSQL = "SELECT id from user where name = '" + name + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                id = tempRs.getInt("id");
            }
            tempStatement.close();
        }

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT user.id, user.name, user.state " +
                "FROM user " +
                "JOIN contact ON (user.id=contact.id_send OR user.id=contact.id_receive) " +
                "WHERE user.id != " + id + " and user.name != '" + name + "'";


        ResultSet resultSet = statement.executeQuery(sqlQuery);
        List<User> contactList = new ArrayList<>();
        while (resultSet.next()) {
            User TempUser = new User();
            TempUser.setId(resultSet.getInt("id"));
            TempUser.setUsername(resultSet.getString("name"));
            TempUser.setState(resultSet.getInt("state"));

            contactList.add(TempUser);
        }

        resultSet.close();
        statement.close();
        return contactList;
    }


    public List<Group> getMyGroups(int id, String name) throws SQLException {

        if(name == null) return null;
        if (id < 0) {
            String tempSQL = "SELECT id from user where name = '" + name + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                id = tempRs.getInt("id");
            }
            tempStatement.close();
            if (id < 0) return null;
        }

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT bdpd.group.id, bdpd.group.name, bdpd.group.id_admin " +
                "FROM bdpd.group " +
                "JOIN participant ON bdpd.group.id = participant.id_group " +
                "where participant.id_user =" + id;


        ResultSet resultSet = statement.executeQuery(sqlQuery);
        List<Group> GroupList = new ArrayList<>();

        while (resultSet.next()) {
            Group TempGroup = new Group();
            TempGroup.setId(resultSet.getInt("id"));
            TempGroup.setName(resultSet.getString("name"));
            TempGroup.setId_admin(resultSet.getInt("id_admin"));

            GroupList.add(TempGroup);
        }

        resultSet.close();
        statement.close();

        return GroupList;
    }

    public List<Msg> getMsgFromContact(String myName, int myId, String hisName, int hisId) throws SQLException {
        if (myName == null || hisName == null) return null;

        Statement statement = dbConn.createStatement();

        if (myId < 0) {
            String tempSQL = "SELECT id from user where name = '" + myName + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                myId = tempRs.getInt("id");
            }
            tempStatement.close();
            if (myId < 0) return null;
        }

        System.out.println("id1-" +myId);

        if (hisId < 0) {
            String tempSQL = "SELECT id from user where name = '" + hisName + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = statement.executeQuery(tempSQL);
            while (tempRs.next()) {
                hisId = tempRs.getInt("id");
            }
            tempStatement.close();
            if (hisId < 0) return null;
        }

        String sqlQuery = "Select message.id, message.message, message.send_at, message.seen, message.id_user, message.id_receiver " +
                "from message " +
                "where id_group = -1 and ((id_user = " + myId + " and id_receiver = " + hisId + ")or(id_user = " + hisId + " and id_receiver = " + myId + "))" +
                "order by message.id";

        ResultSet resultSet = statement.executeQuery(sqlQuery);
        List<Msg> MsgList = new ArrayList<>();

        while (resultSet.next()) {
            Msg TempMsg = new Msg();
            TempMsg.setId(resultSet.getInt("id"));
            TempMsg.setMessage(resultSet.getString("message"));
            TempMsg.setSeen(resultSet.getInt("seen"));
            TempMsg.setId_receiver(resultSet.getInt("message.id_receiver"));
            TempMsg.setId_user(resultSet.getInt("message.id_user"));

            MsgList.add(TempMsg);
        }

        resultSet.close();
        statement.close();

        return MsgList;
    }

    public List<Msg> getMsgFromGroup(String myName, int myId, String groupName, int groupId) throws SQLException {
        if (myName == null || groupName == null) return null;

        Statement statement = dbConn.createStatement();

        if (groupId < 0) {
            String tempSQL = "SELECT id from bdpd.group where name = '" + groupName + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                groupId = tempRs.getInt("id");
            }
            tempStatement.close();

            if (groupId < 0) return null;
        }

        if (myId < 0) {
            String tempSQL = "SELECT id from user where name = '" + myName + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                myId = tempRs.getInt("id");
            }
            tempStatement.close();
            if (myId < 0) return null;
        }

        String sqlQuery = "Select message.id, message.id_user, user.name, message.id_group, message.message, message.send_at, message.seen " +
                "from message " +
                "JOIN user ON user.id = message.id_user " +
                "where message.id_group=" + groupId + " " +
                "order by message.id";

        ResultSet resultSet = statement.executeQuery(sqlQuery);
        List<Msg> MsgList = new ArrayList<>();

        while (resultSet.next()) {
            Msg TempMsg = new Msg();
            TempMsg.setId(resultSet.getInt("id"));
            TempMsg.setMessage(resultSet.getString("message"));
            TempMsg.setSeen(resultSet.getInt("seen"));
            //TempMsg.setId_receiver(resultSet.getInt("message.id_receiver"));
            TempMsg.setId_user(resultSet.getInt("message.id_user"));
            TempMsg.setId_group(resultSet.getInt("id_group"));

            MsgList.add(TempMsg);
        }

        resultSet.close();
        statement.close();

        return MsgList;
    }

    public Boolean removeContact(String myName, int myId, String hisName, int hisId) throws SQLException {

        if (myName == null || hisName == null) return false;

        Statement statement = dbConn.createStatement();

        if (myId < 0) {
            String tempSQL = "SELECT id from user where name = '" + myName + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                myId = tempRs.getInt("id");
            }
            tempStatement.close();
            if (myId < 0) return false;
        }

        if (hisId < 0) {
            String tempSQL = "SELECT id from user where name = '" + hisName + "'";
            Statement tempStatement = dbConn.createStatement();
            ResultSet tempRs = tempStatement.executeQuery(tempSQL);
            while (tempRs.next()) {
                hisId = tempRs.getInt("id");
            }
            tempStatement.close();
            if (hisId < 0) return false;
        }


        String sqlQuery = "DELETE FROM contact " +
                        "WHERE (id_send = " + myId + " AND id_receive=" + hisId + ") or (id_send = " + hisId + " AND id_receive=" + myId + ")";
        statement.executeUpdate(sqlQuery);
        sqlQuery = "DELETE FROM message " +
                "where id_group = -1 and ((id_user = " + myId + " and id_receiver = " + hisId + ")or(id_user = " + hisId + " and id_receiver = " + myId + "))";
        statement.executeUpdate(sqlQuery);

        statement.close();
        return true;
    }


}
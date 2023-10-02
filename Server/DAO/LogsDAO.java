package Server.DAO;

import java.sql.*;

public class LogsDAO {
    //Données pour la connection SQL
    private static String DB_URL = "jdbc:mysql://localhost/chat?logs=root&password=";
    private static String USERNAME = "root";
    private static String PASSWORD = "";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Statement stmt;

    static {
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int type;
    private String pseudo;

    public LogsDAO() {
    }

    public void initDAO(int type, String pseudo){
        this.type = type;
        this.pseudo = pseudo;
    }
    public void insertLog() {
        //Type de Logs
        String type_str ="";
        switch (type){
            case 1:
                type_str="New User";
                break;
            case 2:
                type_str="New Message";
                break;
            case 4:
                type_str="Update Statement";//User, Admin, Moderator
                break;
            case 5:
                type_str="Update Permission";//Online,Offline,Away,Ban
                break;
            default:
                break;
        }
        try {
            int user_id=0;

            String sqlGetUser = "SELECT * FROM users WHERE NAME=?";
            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlGetUser);
            preparedStatementUser.setString(1,pseudo);
            ResultSet resultSetUser = preparedStatementUser.executeQuery();

            if(!isID(pseudo)) { //Cas où le pseudo est passé sous forme de String
                if (resultSetUser.next()) {
                    user_id = resultSetUser.getInt("id");
                }
            }else{
                user_id=Integer.parseInt(pseudo);
            }
            if(type_str.equals("Perm Change")){
                String permType = resultSetUser.getString("permission");
                type_str=permType;
            }
            if(type_str.equals("Status Change")){
                String statusType = resultSetUser.getString("statut");
                type_str=statusType;
            }

            String sql = "INSERT INTO `logs` (`user_id`, `time_stamp`, `type`) VALUES (?, current_timestamp(), ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //Insert Log dans la base de donnée
            preparedStatement.setInt(1, user_id);
            preparedStatement.setString(2, type_str);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isID(String pseudo) {
        try {
            Integer.parseInt(pseudo);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void end() throws SQLException {
        //Fermeture des statements
        stmt.close();
        connection.close();
    }

}

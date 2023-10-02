package Server.DAO;

import Server.ClientSocket;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

public class UserDAO {
    //Données de connection a la base SQL
    private final String DB_URL = "jdbc:mysql://localhost/chat?user=root&password=";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private final Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    private final Statement stmt = connection.createStatement();

    private final PrintWriter out;
    private ArrayList<ClientSocket> listClient;

    private String msg;
    private String pseudo;

    public UserDAO(PrintWriter out, ArrayList<ClientSocket> listClient) throws SQLException {
        this.out = out;
        this.listClient=listClient;
    }

    public void initDAO(String msg, String pseudo){
        this.msg = msg;
        this.pseudo = pseudo;
    }

    public void sendAll(String Socket){
        //Envoyer a tous les users
        for (ClientSocket cl:listClient){
            cl.send(Socket);
        }
    }

    public void getUser() {
        //Recuperer les données pour se connecter
        String email=pseudo;
        String password=msg;
        try {
            String sql = "SELECT * FROM users WHERE email=? AND PASSWORD=?AND statut!='ban'";

            String sqlUpdate ="UPDATE users SET statut = 'online' WHERE statut = 'offline'  AND NAME =?";

           PreparedStatement preparedStatementUpdate = connection.prepareStatement(sqlUpdate);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatementUpdate.setString(1,resultSet.getString("name"));
                preparedStatementUpdate.executeUpdate();
                if (resultSet.getString("statut").equals("ban")){
                    //Le user est Ban
                    out.println("0&ban&ban");
                    out.flush();
                }else {
                    //Les informations du User
                    out.println("0&" + resultSet.getString("name") + "&" + resultSet.getString("id") + "~" + resultSet.getString("name") + "~" + resultSet.getString("email") + "~" + resultSet.getString("password") + "~" + resultSet.getString("permission") + "~" + "online");
                    out.flush();
                }
            }else{
                //Le user n'est pas dans la base de donnée
                out.println("0&Error&Error");
                out.flush();
            }
            resultSet.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser() {
        //Nouvel utilisateur
        String [] differentLogin = msg.split("~");
        String username=differentLogin[0];
        String phone=differentLogin[1];
        String email=differentLogin[2];
        String password=differentLogin[3];
        try {
            String insertQuery = "INSERT INTO users(name,phone,email,password,statut,permission) VALUES('" + username + "', '" + phone + "', '" + email + "', '" + password + "', 'offline', 'user')";
            stmt.executeUpdate(insertQuery);
            out.println("1&New User&New User");
            out.flush();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStatement() throws SQLException {
        //Chagement d'etat
        String sqlUpdateStatut = "UPDATE users SET statut =? WHERE NAME = ?";
        PreparedStatement preparedStatementUpdate = connection.prepareStatement(sqlUpdateStatut);
        preparedStatementUpdate.setString(1, msg);
        preparedStatementUpdate.setString(2, pseudo);
        preparedStatementUpdate.executeUpdate();

    }

    public void updatePermission() throws SQLException {
        //Changement des droits
        String sqlUpdatePerm = "UPDATE users SET permission =? WHERE NAME = ?";
        PreparedStatement preparedStatementUpdate = connection.prepareStatement(sqlUpdatePerm);
        preparedStatementUpdate.setString(1, msg);
        preparedStatementUpdate.setString(2, pseudo);
        preparedStatementUpdate.executeUpdate();
    }

    public void getStatus(String s) throws SQLException {
        //Avoir la liste des utilisateurs et de leurs status
        String sqlStatus = "SELECT * FROM users WHERE statut=?";
        PreparedStatement preparedStatementStatus = connection.prepareStatement(sqlStatus);

        preparedStatementStatus.setString(1, s);
        ResultSet resultSetStatus = preparedStatementStatus.executeQuery();
        StringBuilder stringBuilder = new StringBuilder();
        while (resultSetStatus.next()){
            stringBuilder.append(resultSetStatus.getString("NAME")).append("¤");
        }
        if (stringBuilder.toString().endsWith("¤")) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        sendAll("6&"+s+"&"+stringBuilder);
        resultSetStatus.close();

    }
    public void getPermission() throws SQLException {
        //Avoir la liste des utilisateurs et de leur droit
        String sqlPerm = "SELECT * FROM users";
        PreparedStatement preparedStatementPerm = connection.prepareStatement(sqlPerm);
        ResultSet resultSetperm = preparedStatementPerm.executeQuery();
        StringBuilder stringBuilder = new StringBuilder();
        while (resultSetperm.next()){
            stringBuilder.append(resultSetperm.getString("NAME")).append("¤").append(resultSetperm.getString("permission")).append("~");
        }
        sendAll("6&comboBox&"+stringBuilder);

    }

    public void end() throws SQLException {
        //femeture des statements
        stmt.close();
        connection.close();
    }



}

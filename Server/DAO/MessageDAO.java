package Server.DAO;

import Server.ClientSocket;

import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class MessageDAO {
    //Donnée de connection au serveur SQL
    private final String DB_URL = "jdbc:mysql://localhost/chat?messages=root&password=";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private final Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    private final Statement stmt = connection.createStatement();
    private ArrayList<ClientSocket> listClient;
    private final PrintWriter out;

    private String msg;
    private String pseudo;

    public MessageDAO(PrintWriter out, ArrayList<ClientSocket> listClient) throws SQLException {
        this.out = out;
        this.listClient=listClient;
    }

    public void initDAO(String msg, String pseudo){
        this.msg = msg;
        this.pseudo = pseudo;
    }
    private void sendAll(String Socket){
        //Envoyer a tous les clients connectés
        for (ClientSocket cl:listClient){
            cl.send(Socket);
        }
    }
    public void getMessage() throws SQLException {
        //Recuperer les messages de la base de données
        String displayChat = "SELECT * FROM messages";
        String username = "SELECT * FROM users WHERE id = ?";

        PreparedStatement preparedStatementChat = connection.prepareStatement(displayChat);
        PreparedStatement preparedStatementUsername = connection.prepareStatement(username);

        ResultSet resultSetChat = preparedStatementChat.executeQuery(displayChat);
        StringBuilder stringBuilder = new StringBuilder();
        while (resultSetChat.next()) {
            preparedStatementUsername.setInt(1, resultSetChat.getInt("user_id"));
            ResultSet resultSetUsername = preparedStatementUsername.executeQuery();
            resultSetUsername.next();
            String usernameStr = resultSetUsername.getString("NAME");
            Date timestamp = resultSetChat.getTimestamp("time_stamp");
            String formattedTimestamp;
            if (LocalDate.now().equals(timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                // Afficher l'heure et les minutes pour les messages d'aujourd'hui
                formattedTimestamp = new SimpleDateFormat("HH:mm").format(timestamp);
            } else {
                // Afficher la date "jour/mois" pour les autres messages
                formattedTimestamp = new SimpleDateFormat("dd/MM").format(timestamp);
            }
            stringBuilder.append(usernameStr + "~" + formattedTimestamp + "~" + resultSetChat.getString("content")).append("¤");

        }
        sendAll("3&pseudo&"+ stringBuilder);
    }
    public void addMessage(int id) throws SQLException {
        //Ajouter un message à la base de donnée
        String sqlMessage = "INSERT INTO `messages` (`user_id`, `time_stamp`, `content`) VALUES (?, current_timestamp(),?)";
        PreparedStatement preparedStatement4 = connection.prepareStatement(sqlMessage);

        try {
            preparedStatement4.setInt(1, id);
            String content =msg;
            preparedStatement4.setString(2, content);
            preparedStatement4.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void end() throws SQLException {
        stmt.close();  // close the statement object
        connection.close();
    }

    //---------------List des requetes SQL pour avoir les differentes stats-----------
    public void getNumberOfConnectionByDay() throws SQLException{
        String sqlMessage = "SELECT DATE(time_stamp) date, type, COUNT(1) as nbConnection FROM `logs` WHERE type=\"Log in\" GROUP BY date ORDER BY date DESC";
        PreparedStatement preparedStatement5 = connection.prepareStatement(sqlMessage);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ResultSet rs = preparedStatement5.executeQuery();
            while (rs.next()){
                stringBuilder.append(rs.getTimestamp("date")+"~"+rs.getInt("nbConnection")+"¤");
            }
            out.println("11&AffichageStat&"+stringBuilder);
            out.flush();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    public void getNumberOfMessageByDay()throws SQLException{
        String sqlMessage = "SELECT DATE(time_stamp) date, COUNT(1) as nbMessage FROM messages GROUP BY date ORDER BY date DESC";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                stringBuilder.append(rs.getString("date")+"~"+rs.getInt("nbMessage")+"¤");
            }
            out.println("12&AffichageStat&"+stringBuilder);
            out.flush();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    public void getNumberOfUserByGroup()throws SQLException{
        String sqlMessage = "SELECT permission FROM users";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                stringBuilder.append(rs.getString("permission")+"¤");
            }
            out.println("13&AffichageStat&"+stringBuilder);
            out.flush();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    public void getNumberOfUserByStatut()throws SQLException{
        String sqlMessage = "SELECT statut FROM users";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlMessage);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                stringBuilder.append(rs.getString("statut")+"¤");
            }
            out.println("14&AffichageStat&"+stringBuilder);
            out.flush();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
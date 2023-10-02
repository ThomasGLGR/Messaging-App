package Server;


import Server.DAO.*;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;


public class ClientSocket implements Runnable {
    private final BufferedReader in;
    private final PrintWriter out;

    private int type; //Differencier Type de socket:Connection, Message,Action
    private String msg;
    private String pseudo;

    ArrayList<ClientSocket> listClient;

    private MessageDAO messageDAO;
    private LogsDAO logsDAO;
    private UserDAO userDAO;


    public ClientSocket(Socket s,ArrayList<ClientSocket> listClient) throws IOException, SQLException {
        //Initialisation general
        this.listClient=listClient;
        out = new PrintWriter(s.getOutputStream());
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        messageDAO=new MessageDAO(out,listClient);
        userDAO=new UserDAO(out,listClient);
        logsDAO=new LogsDAO();
    }

    public void send(String Socket){
        out.println(Socket);
        out.flush();
    }

    @Override
    public void run() {
        Thread recevoir= new Thread(new Runnable() {
            String receive ;
            @Override
            public void run() {
                try {
                    //Recevoir les sockets des clients
                    receive = in.readLine();
                    while(receive!=null){
                        System.out.println(receive);
                        String[] parties = receive.split("&");
                        SwitchMsg(parties);
                        receive = in.readLine();
                    }
                    //Fermeture des statements
                    out.close();
                } catch (IOException | SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        recevoir.start();
    }

    private void SwitchMsg(String[] parties) throws SQLException, InterruptedException {
        type=Integer.parseInt(parties[0]);
        pseudo=parties[1];
        msg=parties[2];
        messageDAO.initDAO(msg,pseudo);
        userDAO.initDAO(msg,pseudo);
        logsDAO.initDAO(type,pseudo);
        logsDAO.insertLog();
        switch (type){
            case 0://Demande de Connection
                userDAO.getUser();
                break;
            case 1://Nouvel utilisateur
                userDAO.addUser();
                break;
            case 2://Ajout Message
                messageDAO.addMessage(Integer.parseInt(pseudo));
                break;
            case 3://Avoir liste Message
                messageDAO.getMessage();
                break;
            case 4://Changement des états
                userDAO.updateStatement();
                break;
            case 5://Changement des droits
                userDAO.updatePermission();
                break;
            case 6://Avoir la liste des permissions et des status:
                userDAO.getStatus("online");
                userDAO.getStatus("offline");
                userDAO.getStatus("ban");
                userDAO.getStatus("away");
                userDAO.getPermission();
                userDAO.sendAll("6&go&go");
                break;
            case 7:
                break;
            case 8:
                //Envoie des données pour l'appel vidéo
                for (ClientSocket cl:listClient){
                    cl.send("8&" + pseudo + "&" + msg);
                }
                break;
            case 10:
                //fermeture des statements
                messageDAO.end();
                userDAO.end();
                logsDAO.end();
                break;

                //----Ensemble des stats------
            case 11:
                messageDAO.getNumberOfConnectionByDay();
                break;
            case 12:
                messageDAO.getNumberOfMessageByDay();
                break;
            case 13:
                messageDAO.getNumberOfUserByGroup();
                break;
            case 14:
                messageDAO.getNumberOfUserByStatut();
                break;

       }

    }

}



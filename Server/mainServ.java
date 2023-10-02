package Server;

import Server.ClientSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;


public class mainServ {
    // Liste statique de ClientSocket pour conserver les clients connectés
    private static ArrayList<ClientSocket> listClients = new ArrayList<>();
    public static void main(String[] test) throws IOException, SQLException {

        int port = 5000;
        InetAddress adresse = InetAddress.getLocalHost();
        ServerSocket serveurSocket = new ServerSocket(port, 0, InetAddress.getByName(adresse.getHostAddress()));
        //ServerSocket serveurSocket =new ServerSocket(port);
        while (true) {
            try {
                Socket s = serveurSocket.accept();
                ClientSocket currentClient = new ClientSocket(s, listClients);
                // Ajouter le nouveau ClientSocket à la liste
                listClients.add(currentClient);
                currentClient.run();
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }

}
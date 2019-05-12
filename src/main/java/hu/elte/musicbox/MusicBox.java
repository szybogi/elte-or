package hu.elte.musicbox;

import java.net.ServerSocket;

public class MusicBox {

    public static void main(String[] args) {


        ConnectionManager mgr = new ConnectionManager();

        try (
                ServerSocket serverSocket = new ServerSocket(40000);
        ) {
            while(true) {
                ClientConnection newConnection = new ClientConnection(serverSocket.accept(), mgr);
                newConnection.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

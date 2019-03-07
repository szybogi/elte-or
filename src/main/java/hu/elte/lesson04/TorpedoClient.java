package hu.elte.lesson04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

class TorpedoClient {

    private String hostname;
    private int serverPort;

    public TorpedoClient(String hostname, int serverPort) {
        this.serverPort = serverPort;
        this.hostname = hostname;
    }

    void run() {
        try (
                Socket echoSocket = new Socket(hostname, serverPort);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            boolean running = true;
            boolean endOfGame;
            while (running) {
                String status = in.readLine();
                endOfGame = Boolean.getBoolean(status);
                System.out.println(endOfGame);
                if (endOfGame) {
                    System.out.println(in.readLine());
                    running = false;
                } else {
                    String cmd = stdIn.readLine();
                    if (cmd.equals("exit")) {
                        running = false;
                    }
                    out.println(cmd);
                    System.out.println(in.readLine());
                    String message = in.readLine();
                    System.out.println(message);
                    if(message.equals("You Win!") || message.equals("You lose!")) {
                        running = false;
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new TorpedoClient("localhost", 2019).run();
    }

}
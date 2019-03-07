package hu.elte.lesson03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String args[]) {
        new Server().run(2019);
    }

    public void run(Integer port) {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Scanner stdIn = new Scanner(System.in);
        ) {
            System.out.println("Server start.");
            String message = in.readLine();
            System.out.println("Server get a message: " + message);
            System.out.println("Server message to client:");
            out.println(stdIn.nextLine());
            System.out.println("Server send a message to client.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

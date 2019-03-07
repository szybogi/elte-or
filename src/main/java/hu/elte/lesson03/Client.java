package hu.elte.lesson03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        new Client().run("localhost", 2019);
    }

    public void run(String host, int port) {
        try (
                Socket clientSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Scanner sc = new Scanner(System.in);
        ) {
            System.out.println("Client start. Client message:");
            String message = sc.nextLine();
            out.println(message);
            System.out.println("Client send a message: " + message);
            System.out.println("Client get a message: " + in.readLine());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

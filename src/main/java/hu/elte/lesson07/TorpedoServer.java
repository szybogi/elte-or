package hu.elte.lesson07;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

class TorpedoServer {

    public static void main(String[] args) {
        final int serverPort = 1337;
        int nextId = 1;
        final Random rand = new Random(System.currentTimeMillis());

        //B feladatrész
        LinkedList<Player> players = new LinkedList<>();
        try (ServerSocket serverSocket = new ServerSocket(serverPort);
        ) {
            while (true) {
                Socket socket = serverSocket.accept();
                Player p = new Player(socket);
                players.add(p);
                while(players.size() >=2) {
                    final boolean player1Starts = rand.nextBoolean();
                    Game game = new Game(nextId++, players.removeFirst(), players.removeFirst(), player1Starts);
                    game.start();
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
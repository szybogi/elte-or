package hu.elte.lesson04;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class TorpedoServer {

    enum Type {
        WATER,
        SHIP_INTACT,
        SHIP_HIT,
        MISS
    }

    private final int boardSize;
    private Type[][] map;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: <ships_file> <server_port>");
            return;
        }

        final int serverPort = Integer.parseInt(args[1]);

        TorpedoServer game;
        try (Scanner ships = new Scanner(new File(args[0]))) {
            game = new TorpedoServer(10, ships);
        } catch (IOException e) {
            System.err.println("Error while reading ships!");
            e.printStackTrace();
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(serverPort);
             Socket clientSocket = serverSocket.accept();
             PrintWriter out =
                     new PrintWriter(clientSocket.getOutputStream(), true);
             Scanner tips = new Scanner(
                     new InputStreamReader(clientSocket.getInputStream()))
        ) {
            game.play(tips);
        } catch (IOException e) {
            System.err.println("Error while reading tips!");
            e.printStackTrace();
        }

    }

    public TorpedoServer(int boardSize, Scanner ships) {
        this.boardSize = boardSize;
        map = new Type[boardSize][boardSize];
        for (int iy = 0; iy < boardSize; ++iy) {
            for (int ix = 0; ix < boardSize; ++ix) {
                set(ix, iy, Type.WATER);
            }
        }

        while (ships.hasNextLine()) {
            String line = ships.nextLine();
            Scanner lineScanner = new Scanner(line);
            String shipType = lineScanner.next();
            int x = lineScanner.nextInt();
            int y = lineScanner.nextInt();
            switch (shipType) {
                case "X":
                    placeShip(x, y);
                    break;
                case "I":
                    placeShip(x, y - 1);
                    placeShip(x, y);
                    placeShip(x, y + 1);
                    break;
                case "-":
                    placeShip(x - 1, y);
                    placeShip(x, y);
                    placeShip(x + 1, y);
                    break;
            }
        }
    }

    private void play(Scanner tips) {
        while (tips.hasNextInt()) {
            int x = tips.nextInt();
            int y = tips.nextInt();

            hit(x, y);

            print(System.out);
        }
    }

    private void print(PrintStream out) {
        for (int iy = 0; iy < boardSize; ++iy) {
            for (int ix = 0; ix < boardSize; ++ix) {
                out.print(visualise(get(ix, iy)));
            }
            out.println();
        }
    }

    private char visualise(Type type) {
        switch (type) {
            case WATER:
                return '.';
            case MISS:
                return '*';
            case SHIP_INTACT:
                return 'X';
            case SHIP_HIT:
                return '!';
        }
        return '?';
    }

    boolean onMap(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    Type get(int x, int y) {
        return map[y][x];
    }

    void set(int x, int y, Type t) {
        map[y][x] = t;
    }

    private void placeShip(int x, int y) {
        if (onMap(x, y)) {
            set(x, y, Type.SHIP_INTACT);
        }
    }

    private void hit(int x, int y) {
        if (onMap(x, y)) {
            switch (get(x, y)) {
                case SHIP_INTACT:
                    set(x, y, Type.SHIP_HIT);
                    break;
                case WATER:
                    set(x, y, Type.MISS);
                    break;
            }
        }
    }

}
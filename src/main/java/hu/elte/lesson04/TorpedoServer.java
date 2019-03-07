package hu.elte.lesson04;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
    protected static boolean endOfGame = false;
    private int intactShips = 0;


    public static void main(String[] args) {

        final int serverPort = 2019;

        TorpedoServer game, game2;
        try (Scanner ships = new Scanner(new File("src/main/resources/hu/elte/lesson04/ships.txt"));
             Scanner ships2 = new Scanner(new File("src/main/resources/hu/elte/lesson04/ships2.txt"))
        ) {
            game = new TorpedoServer(10, ships);
            game2 = new TorpedoServer(10, ships2);
        } catch (IOException e) {
            System.err.println("Error while reading ships!");
            e.printStackTrace();
            return;
        }
        try (ServerSocket serverSocket = new ServerSocket(serverPort);
             Socket clientSocket = serverSocket.accept();
             Socket clientSocket2 = serverSocket.accept();
             PrintWriter outPlayer1 =
                     new PrintWriter(clientSocket.getOutputStream(), true);
             PrintWriter outPlayer2 =
                     new PrintWriter(clientSocket2.getOutputStream(), true);
             Scanner tipsPlayer1 = new Scanner(
                     new InputStreamReader(clientSocket.getInputStream()));
             Scanner tipsPlayer2 = new Scanner(
                     new InputStreamReader(clientSocket2.getInputStream()));) {
            while (!endOfGame) {
                game.play(tipsPlayer1, outPlayer1);
                game2.play(tipsPlayer2, outPlayer2);
                if (game.intactShips == 0 || game2.intactShips == 0) {
                    if(game.intactShips == 0) {
                        outPlayer1.println("You Win!");
                        outPlayer2.println("You lose!");
                    } else {
                        outPlayer2.println("You Win!");
                        outPlayer1.println("You lose!");
                    }
                    endOfGame = true;
                }
            }
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
                    intactShips += 1;
                    break;
                case "I":
                    placeShip(x, y - 1);
                    placeShip(x, y);
                    placeShip(x, y + 1);
                    intactShips += 3;
                    break;
                case "-":
                    placeShip(x - 1, y);
                    placeShip(x, y);
                    placeShip(x + 1, y);
                    intactShips += 3;
                    break;
            }
        }
    }

    private void play(Scanner tips, PrintWriter out) {
        if (tips.hasNextInt() && !endOfGame) {
            int x = tips.nextInt();
            int y = tips.nextInt();

            String message = hit(x, y);
            out.println(message + ". IntactShips:");
            out.println(intactShips);

            print(System.out);
            System.out.println("\n");
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

    private String hit(int x, int y) {
        String message = "Out of map";
        if (onMap(x, y)) {
            switch (get(x, y)) {
                case SHIP_INTACT:
                    set(x, y, Type.SHIP_HIT);
                    intactShips--;
                    message = "Hit";
                    break;
                case WATER:
                    set(x, y, Type.MISS);
                    message = "Miss";
                    break;
            }
        }
        return message;

    }

}
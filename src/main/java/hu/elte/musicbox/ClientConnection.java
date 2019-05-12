package hu.elte.musicbox;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientConnection implements AutoCloseable {

    private Socket clientSocket;
    private PrintWriter out;
    private Scanner in;
    private Thread incomingThread;
    private Thread outgoingThread;
    private ConnectionManager mgr;
    private final LinkedList<String> outgoingMessages = new LinkedList<>();
    boolean active = true;
    private Map<String, Song> songs= new HashMap <>();
    private Map<Integer, Song> playedSongs = new HashMap<>();
    private final AtomicInteger tempo = new AtomicInteger(200);
    private final AtomicInteger transpose = new AtomicInteger(0);
    private  final AtomicBoolean stop = new AtomicBoolean(false);

    public ClientConnection(Socket clientSocket, ConnectionManager mgr) throws IOException {
        this.clientSocket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new Scanner(clientSocket.getInputStream());
        this.mgr = mgr;
        songs.put("asd", new Song("asd"));
        songs.get("asd").setSheetMusic("C 4 E 4 C 4 E 4 G 8 G 8 REP 6;1 C/1 4 B 4 A 4 G 4 F 8 A 8 G 4 F 4 E 4 D 4 C 8 C 8");
        songs.get("asd").setLyrics("bo ci bo ci tar ka se fü le se far ka o da me gyünk lak ni a hol te jet kap ni");

    }


    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        clientSocket.close();
    }

    public void run() {

        incomingThread = new Thread(() -> {
            System.out.println("Starting client connection");
            mgr.add(this);
            while (in.hasNextLine()) {
                String input = in.nextLine();
                System.out.println("Got input from client:" + input);
                try {
                    String[] action = input.split(" ");
                    String title;
                    switch (action[0]) {
                        case "stop":
                            synchronized (this.stop) {
                                stop.set(true);
                            }
                            break;
                        case "change":
                            synchronized (this.tempo) {
                                tempo.set(Integer.parseInt(action[1]));
                            }
                            synchronized (this.transpose) {
                                transpose.set(Integer.parseInt(action[2]));
                            }

                            break;
                        case "add":
                            title = action[1];
                            Song newSong = new Song(title);
                            input = in.nextLine();
                            newSong.setSheetMusic(input);
                            songs.put(title, newSong);
                            break;
                        case "addlyrics":
                            title = action[1];
                            input = in.nextLine();
                            songs.get(title).setLyrics(input);
                            break;
                        case "play":
                            synchronized (this.tempo) {
                                tempo.set(Integer.parseInt(action[1]));
                            }
                            synchronized (this.transpose) {
                                transpose.set(Integer.parseInt(action[2]));
                            }
                            title = action[3];
                            Song song = songs.get(title);
                            //addPlayedSong(song);
                            List<String> playableSong = makePlayableSong(song);
                            synchronized (outgoingMessages) {
                                outgoingMessages.clear();
                                outgoingMessages.addAll(playableSong);
                                outgoingMessages.notify();
                            }
                            break;
                        default: break;
                    }


                    //Task t = new Task(input);

                    /*mgr.forEachConnection((ClientConnection c) -> {
                        synchronized (c.outgoingMessages) {
                            c.outgoingMessages.add(t.getId() + " " + t.getResult());
                            c.outgoingMessages.notifyAll();
                        }
                    });

                    System.out.println("Queued answer to all clients: " + t.getResult());*/


                } catch (Exception e) {
                    // survive invalid input
                    e.printStackTrace();
                }
            }
            System.out.println("Client disconnected");

            // Make sure that the outgoing thread exits nicely

            synchronized (outgoingMessages) {
                active = false;
                outgoingMessages.clear();
                outgoingMessages.notify();
            }

            try {
                outgoingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                mgr.remove(this);
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Client threads done.");

        });
        incomingThread.start();

        outgoingThread = new Thread(() -> {
            while(active) {
                synchronized (outgoingMessages) {
                    try {
                        outgoingMessages.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {

                            while (!outgoingMessages.isEmpty() && clientSocket.isConnected()) {
                                synchronized (this.stop) {
                                    if (this.stop.get()) {
                                        break;
                                    }
                                }
                                String actualElement = outgoingMessages.getFirst();
                                System.out.println(actualElement);
                                if(!actualElement.equals("HOLD")) {
                                    synchronized (this.transpose) {
                                        out.println(actualElement +this.transpose.get());
                                        out.flush();
                                    }

                                } else {
                                    synchronized (this.tempo) {
                                        Thread.sleep(this.tempo.get());
                                    }




                                }

                                System.out.println("Sent answer to client: " + clientSocket + " - " + outgoingMessages.getFirst());
                                outgoingMessages.removeFirst();
                            }


                        out.println("FIN");
                        out.flush();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        outgoingThread.start();
    }

    private void addPlayedSong(Song song) {
        int i = 1;
        while (!playedSongs.get(i).equals(null)) {
            i++;
        }
        playedSongs.put(i, song);
    }

    private List<String> makePlayableSong(Song song) {
        List<String> pSong = new ArrayList<>();
        String[] sheetMusic = song.getSheetMusic().split(" ");
        String[] lyrics;
        if(song.getLyrics() !=null) {
            lyrics = song.getLyrics().split(" ");
        } else {
            lyrics = null;
        }
        int actual = 0;
        int noteCount = 0;
        while (actual < sheetMusic.length) {
            if(sheetMusic[actual].equals("REP")) {
                if(Integer.parseInt(sheetMusic[actual + 1].split(";")[1]) != 0) {
                    String[] replay = sheetMusic[actual + 1].split(";");
                    int start = Integer.parseInt(replay[0]);
                    int rest = Integer.parseInt(replay[1])-1;
                    sheetMusic[actual+1] = start + ";" + rest;
                    actual = actual - (start * 2) -2;
                }

            } else {
                String syllable = "???";
                if(lyrics != null && lyrics.length > noteCount) {
                    syllable = lyrics[noteCount];
                }

                pSong.add(sheetMusic[actual] + " " + syllable + " " );

                noteCount++;
                System.out.println(sheetMusic[actual]);
                for(int i = 0; i < Integer.parseInt(sheetMusic[actual + 1])-1; i++) {
                    pSong.add("HOLD");
                    System.out.println("HOLD");
                }

            }
            actual += 2;

        }
        return  pSong;
    }

}
package hu.elte.musicbox;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Scanner;

public class MusicBoxClient {
    private static int sound;

    private static boolean running = true;
    public static void main(String[] args) throws InterruptedException {

        try (
                Socket socket = new Socket("localhost", 40000);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream());
                Scanner in =
                        new Scanner(
                                new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            Thread tToServer = new Thread(() -> {
                try {
                    while (running) {
                        String read =stdIn.readLine();
                        String msg = read;
                        out.println(msg);
                        System.out.println("Message sent: " + msg);
                        String[] action = read.split(" ");
                        if(read.equals("exit")) {
                            running = false;
                            break;
                        } else if(action[0].equals("add") || action[0].equals("addlyrics")) {
                            read =stdIn.readLine();
                            msg = read;
                            out.println(msg);
                            System.out.println("Message sent: " + msg);
                        }
                        out.flush();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });

            Thread tFromServer = new Thread(() -> {
                while (running && in.hasNextLine()) {
                    String tune = in.nextLine();
                    String[] soundAndSyllableAndTranspose = tune.split(" ");
                    if(tune.equals("FIN")) {
                        System.out.println(tune);
                    }
                    //System.out.println("Message received; " + soundAndSyllableAndTranspose[0] + " " + soundAndSyllableAndTranspose[1]);
                    if(soundAndSyllableAndTranspose.length == 3){
                        try {
                            Synthesizer synthesizer = MidiSystem.getSynthesizer();
                            synthesizer.open();
                            MidiChannel channel = synthesizer.getChannels()[1];
                            channel.allNotesOff();
                            sound = getSound(soundAndSyllableAndTranspose[0]);
                            if(sound != 0) {
                                sound += Integer.valueOf(soundAndSyllableAndTranspose[2]);
                                channel.noteOn(sound, 100);
                            }



                        } catch (MidiUnavailableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            tFromServer.start();
            tToServer.start();

            tFromServer.join();
            tToServer.join();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int getSound(String s) {
        int basic = 60;
        int sound;
        int octave = 0;
        int halfSound = 0;
        String[] soundInformation = s.split("/");
        if(soundInformation.length == 2) {
            octave = 12 * Integer.valueOf(soundInformation[1]);
        }
        switch (soundInformation[0].charAt(0)){
            case 'C': sound = 1; break;
            case 'D': sound = 3; break;
            case 'E': sound = 5; break;
            case 'F': sound = 6; break;
            case 'G': sound = 8; break;
            case 'A': sound = 10; break;
            case 'B': sound = 12; break;
            default: sound = 0; return sound;
        }
        if(soundInformation[0].length() == 2) {
            if(soundInformation[0].charAt(1) == 'b') {
                halfSound = -1;
            } else if(soundInformation[0].charAt(1) == '#') {
                halfSound = 1;
            } else {
                halfSound = 0;
            }
        }
        int result = basic + sound +octave +halfSound;
        return result;
    }
}

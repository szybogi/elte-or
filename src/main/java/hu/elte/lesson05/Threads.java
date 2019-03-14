package hu.elte.lesson05;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Threads {

    public static void main(String[] args) {
        List list = new LinkedList();
        Runnable r1 = new Runnable(new File("src/main/resources/hu/elte/lesson05/input.txt"));
        Runnable r2 = new Runnable(new File("src/main/resources/hu/elte/lesson05/input.txt"));
    }

    private static class Runnable extends Threads {
        File file;

        public Runnable(File file) {
            this.file = file;
        }

        public void print(List list) {
            try (Scanner sc = new Scanner(new FileReader(file));) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
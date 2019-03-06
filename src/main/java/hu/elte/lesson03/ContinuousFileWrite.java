package hu.elte.lesson03;


import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class ContinuousFileWrite {
    public static void main(String[] args) {
        File file = new File("./src/main/resources/hu/elte/lesson03/output.txt");
        try (
                PrintWriter pw = new PrintWriter(file);
                Scanner scanner = new Scanner(System.in);
        ) {
            String line = scanner.nextLine();
            while (!line.equals("end")) {
                pw.println(line);
                pw.flush();
                line = scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

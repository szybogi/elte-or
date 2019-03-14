package hu.elte.lesson05;

public class Threads {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> characterPrinter("Hello "));
        t1.start();
        new Thread(() -> characterPrinter("vilag")).start();

        try {
            t1.join();
        } catch (InterruptedException e) {
        }
        characterPrinter("!");
    }

    private static synchronized void characterPrinter(String hello) {
        for (char c : hello.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }
}
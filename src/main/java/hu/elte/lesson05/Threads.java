package hu.elte.lesson05;

public class Threads {

    public static void main(String[] args) {
        try {
            Thread t1 = new Thread(() -> characterPrinter("Hello "));
            t1.start();
            t1.join();
            Thread t2 = new Thread(() -> characterPrinter("vilag"));
            t2.start();
            t2.join();
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
package thread.hungry;

public class hungry {
    public static void main(String[] args) {
        Thread t1 = new Thread(new target());
        Thread t2 = new Thread(new target());
        Thread t3 = new Thread(new target());
        Thread t4 = new Thread(new target());

        t1.setPriority(Thread.MIN_PRIORITY);
        t2.setPriority(3);
        t3.setPriority(7);
        t4.setPriority(Thread.MAX_PRIORITY);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}

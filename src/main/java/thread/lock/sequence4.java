package thread.lock;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class sequence4 {
    private int value;

    //    公用的锁
    Lock lock = new ReentrantLock();

    public int getValue() {
//        此处仍会出现线程安全问题，加锁不正确，每个线程独立维护一把锁，应当所有线程公用一把锁
//        Lock lock = new ReentrantLock();
        lock.lock();
        value += 2;
        lock.unlock();

        return value;
    }

    public static void main(String[] args) {
        sequence4 s = new sequence4();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " " + s.getValue());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " " + s.getValue());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

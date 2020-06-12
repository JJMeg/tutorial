package thread.atomic;

import scala.xml.Atom;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class sequence3 {
    private AtomicInteger value = new AtomicInteger(0);

    private int[] s = {1, 2, 3};
    AtomicIntegerArray a = new AtomicIntegerArray(s);

    public int getNext() {
        return value.getAndAdd(2);
    }

    public int getValue() {
        return a.addAndGet(2,2);
    }

    public static void main(String[] args) {
        sequence3 s = new sequence3();

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

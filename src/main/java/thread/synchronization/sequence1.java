package thread.synchronization;

public class sequence1 {
    private volatile int value;

    public int getNext() {
//        既读又写操作，出现线程安全性问题
        return value++;
    }

    public int getValue() {
        return value += 2;
    }

    public static void main(String[] args) {
        sequence1 s = new sequence1();

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

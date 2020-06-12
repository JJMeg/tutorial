package thread.volatile_demo;

public class sequence2 {
    //不保证原子性
    public synchronized int getNext() {
        return value += 2;
    }

    private volatile int value;

    public  int getValue() {
        return value+=2;
    }

    public static void main(String[] args) {
        sequence2 s = new sequence2();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " " + s.getValue() );
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
                    System.out.println(Thread.currentThread().getName() + " " + s.getValue() );
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

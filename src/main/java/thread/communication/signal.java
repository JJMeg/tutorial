package thread.communication;

public class signal {
    private volatile int signal;

    public void setSignal(int signal) {
        this.signal = signal;
    }

    private int getSignal() {
        return signal;
    }

    public static void main(String[] args) {
        thread.communication.signal s = new signal();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (s) {
                    System.out.println("修改状态的线程执行....");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.setSignal(1);
                    System.out.println("修改状态成功....");
                    s.notify();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
//                等待signal为1时开始执行，否则不能执行
//                有性能消耗，会常占用CPU，使用sleep，但仍占用CPU，加上synchronized
                synchronized (s) {
                    while (s.getSignal() != 1) {
                        try {
                            s.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

//                signal=1，开始执行
                    System.out.println("执行....");

                }
            }
        }).start();
    }
}


package thread.re_enter;

public class re_enter {
    public synchronized void a() {
        System.out.println("a");
        b();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void b() {
        System.out.println("b");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        re_enter r = new re_enter();

        new Thread(new Runnable() {
            @Override
            public void run() {
                r.a();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                r.b();
            }
        }).start();

    }
}

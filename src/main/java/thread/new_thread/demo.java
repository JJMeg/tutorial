package thread.new_thread;

public class demo {

    public static void main(String[] args) {
//        匿名内部类创建线程
        new Thread() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        }.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable");
            }
        }) {
            @Override
            public void run() {
                System.out.println("sub");
            }
        }.start();
    }


}

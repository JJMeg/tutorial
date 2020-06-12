package thread.new_thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 线程池创建线程
public class pool {
    public static void main(String[] args) {
        ExecutorService p = Executors.newFixedThreadPool(5);
        ExecutorService p2 = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 50; i++) {
            p.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                }
            });
            p2.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                }
            });

        }

        p.shutdown();
        p2.shutdown();
    }
}

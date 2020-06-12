package thread.synchronization;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Singleton_lazy {
    //    构造私有方法
    private Singleton_lazy() {
    }

    // 创建唯一的singleton对象
    private static Singleton_lazy instance;

    // 获取唯一对象的方法
    public static Singleton_lazy getInstance() {
//        懒汉式，在需要时才创建实例
        synchronized (Singleton_lazy.class) {
            if (instance == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instance = new Singleton_lazy();
            }
        }

        return instance;
    }

    public static void main(String[] args) {
        ExecutorService p = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 50; i++) {
            p.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " " + Singleton_lazy.getInstance().hashCode());
                }
            });
        }

        p.shutdown();
    }
}

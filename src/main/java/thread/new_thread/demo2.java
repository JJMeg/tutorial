package thread.new_thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

// 带返回值的线程
public class demo2 implements Callable<Integer> {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        demo2 d = new demo2();

        FutureTask<Integer> task = new FutureTask<>(d);

        Thread t = new Thread(task);
        t.start();

        System.out.println("I am waiting");

        Integer result = task.get();
        System.out.printf("result: %d", result);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("calculating now...");
        Thread.sleep(3000);
        return 1;
    }
}

package thread.new_thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// 定时任务
public class demo_timer {
    public static void main(String[] args) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.printf("Hi,%s\n", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            }
        }, 1000, 2000);
    }
}

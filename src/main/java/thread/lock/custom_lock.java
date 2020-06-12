package thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class custom_lock implements Lock {
    private boolean isLocked = false;

//    不可重入的实现
//    @Override
//    public synchronized void lock() {
////        加synchronized，让所有进来的线程都等待
////        自旋
//        while (isLocked) {
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        isLocked = true;
//    }
//
//    @Override
//    public synchronized void unlock() {
//        isLocked = false;
//        notify();
//    }


    //    可重入的实现
    Thread lockBy = null;
    int lockCount = 0;

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        while (isLocked && currentThread != lockBy) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;
        lockBy = currentThread;
        lockCount++;
    }

    @Override
    public void unlock() {
        if (lockBy == Thread.currentThread()) {
            lockCount--;

            if (lockCount == 0) {
                isLocked = false;
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

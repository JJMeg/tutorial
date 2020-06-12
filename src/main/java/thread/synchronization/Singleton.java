package thread.synchronization;

public class Singleton {
    //    构造私有方法
    private Singleton() {
    }

    // 创建唯一的singleton对象
    private static Singleton instance = new Singleton();

    // 获取唯一对象的方法
    public static Singleton getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Singleton s = Singleton.getInstance();
//        无线程安全问题
    }
}

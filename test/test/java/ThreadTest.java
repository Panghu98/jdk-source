package test.java;

public class ThreadTest {

    static {
        Thread thread = Thread.currentThread();
        System.err.println(thread.getName() + "正在执行静态代码块打印方法");
        try {
            Thread.sleep(1000*2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void staticSyncPrint() {

        Thread thread = Thread.currentThread();
        System.err.println(thread.getName() + "正在执行静态同步打印方法");
        try {
            Thread.sleep(1000*2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private synchronized void syncPrint() {
        Thread thread = Thread.currentThread();
        System.err.println(thread.getName() + "正在执行同步打印方法");
        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void notSyncPrint() {
        Thread thread = Thread.currentThread();
        System.err.println(thread.getName() + "正在执行非同步打印方法");
        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }






    public static void main(String[] args) {


        ThreadTest test = new ThreadTest();
        ThreadTest test2 = new ThreadTest();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                staticSyncPrint();
                test.syncPrint();
            }
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                staticSyncPrint();
                test2.notSyncPrint();
            }
        };



        Thread t1 = new Thread(runnable);
        t1.setName("线程1");
        Thread t2 = new Thread(runnable2);
        t2.setName("线程2");


        t1.start();
        t2.start();

    }

}

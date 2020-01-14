package test.java;

public class ThreadTest {

    private synchronized void threadPrint() {

        Thread thread = Thread.currentThread();
        System.err.println(thread.getName() + "正在执行print方法");
        try {
            Thread.sleep(1000*5);
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
                test.threadPrint();
            }
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                test2.threadPrint();
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

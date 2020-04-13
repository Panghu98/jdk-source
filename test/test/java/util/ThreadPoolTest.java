package test.java.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 11:06 2020/4/13
 * @Modified By:
 */
public class ThreadPoolTest {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,4,6000,
                TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(6));
        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            System.out.println("线程" +name+ "开始执行任务......");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程" +name+ "执行任务完毕......");
        };
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
        // 7
        executor.execute(runnable);
        // 8
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);

    }

}

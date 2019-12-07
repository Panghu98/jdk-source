一、基本知识
（1）线程特性

- 每个线程均有优先级
- 线程能被标记为守护线程
- 每个线程均分配一个name
（2）创建线程的方法

继承Thread类，并重写run方法
// 继承Thread类
class PrimeThread extends Thread {
    long minPrime;
    PrimeThread(long minPrime) {
    this.minPrime = minPrime;
    }

    public void run() {
        // compute primes larger than minPrime
    }
}
// 调用
PrimeThread p = new PrimeThread(143);
p.start();
创建Thread类，并传入构造参数runnable
class PrimeRun implements Runnable {
    long minPrime;
    PrimeRun(long minPrime) {
        this.minPrime = minPrime;
    }
    public void run() {
        // compute primes larger than minPrime
    }
}
// 调用
PrimeRun p = new PrimeRun(143);
new Thread(p).start();
二、线程状态
    public enum State {
        NEW,
        RUNNABLE,
        BLOCKED,
        WAITING,
        TIMED_WAITING,
        TERMINATED;
    }
NEW 状态是指线程刚创建, 尚未启动
RUNNABLE 状态是线程正在正常运行中, 当然可能会有某种耗时计算/IO等待的操作/CPU时间片切换等, 这个状态下发生的等待一般是其他系统资源, 而不是锁, Sleep等
BLOCKED 这个状态下, 是在多个线程有同步操作的场景, 这个事件将在另一个线程放弃了这个锁的时候发生,也就是这里是线程在等待进入临界区
WAITING（无线等待） 这个状态下是指线程拥有了某个锁之后, 调用了他的wait方法, 等待其他线程/锁拥有者调用 notify / notifyAll 一遍该线程可以继续下一步操作, 这里要区分 BLOCKED 和 WATING 的区别, 一个是在临界点外面等待进入, 一个是在临界点里面wait等待别人notify, 线程调用了join方法 join了另外的线程的时候, 也会进入WAITING状态, 等待被他join的线程执行结束
TIMED_WAITING 这个状态就是有限的(时间限制)的WAITING, 一般出现在调用wait(long), join(long)等情况下, 另外一个线程sleep后, 也会进入TIMED_WAITING状态
TERMINATED 这个状态下表示 该线程的run方法已经执行完毕了, 基本上就等于死亡了(当时如果线程被持久持有, 可能不会被回收)
java层次的状态转换图

 


 

 

操作系统层次的状态转换图

 


 

 

三、基本属性
// Thread本身也是继承了Runnable接口
public class Thread implements Runnable {

    private volatile char  name[];
    private int            priority;
    private Thread         threadQ;
    private long           eetop;

    private boolean     single_step;
    private boolean     daemon = false;

    // 虚拟机状态
    private boolean     stillborn = false;

    // 实际的线程任务
    private Runnable target;

    private ThreadGroup group;
    private ClassLoader contextClassLoader;
    private AccessControlContext inheritedAccessControlContext;

    // 所有初始化线程的数目
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    // 这是为ThreadLocal类维护的一些变量
    ThreadLocal.ThreadLocalMap threadLocals = null;
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

    private long stackSize;
    private long nativeParkEventPointer;

    // 线程id相关
    private long tid;
    private static long threadSeqNumber;
    private static synchronized long nextThreadID() {
        return ++threadSeqNumber;
    }

    // 线程状态
    private volatile int threadStatus = 0;


    volatile Object parkBlocker;
    private volatile Interruptible blocker;
    private final Object blockerLock = new Object();
    void blockedOn(Interruptible b) {
        synchronized (blockerLock) {
            blocker = b;
        }
    }

    // java中的线程总共分了10个优先级
    // 最小优先级为1，最大为10，默认为5
    public final static int MIN_PRIORITY = 1;
    public final static int NORM_PRIORITY = 5;
    public final static int MAX_PRIORITY = 10;
}
四、构造函数
    // 最主要的辅助构造函数，所有的构造函数均调用init函数
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name.toCharArray();

        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();

        this.group = g;
        // 子线程继承父线程的优先级和守护属性
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        if (parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        // 获取唯一的线程id，此函数为synchronize
        tid = nextThreadID();
    }

    // 所有的构造函数本质上都是调用init方法
    public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
    }
    public Thread(Runnable target) {
        init(null, target, "Thread-" + nextThreadNum(), 0);
    }
    Thread(Runnable target, AccessControlContext acc) {
        init(null, target, "Thread-" + nextThreadNum(), 0, acc);
    // 省略许多构造函数
五、主要方法
    // 启动一个线程
    public synchronized void start() {
        // 线程不能重复start
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        group.add(this);

        boolean started = false;
        try {
            // 未native方法
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {

            }
        }
    }

    private native void start0();

    // Thread也实现了Runnable接口
    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }

    // 由系统调用，可以使Thread在销毁前释放资源
    private void exit() {
        if (group != null) {
            group.threadTerminated(this);
            group = null;
        }
        target = null;
        threadLocals = null;
        inheritableThreadLocals = null;
        inheritedAccessControlContext = null;
        blocker = null;
        uncaughtExceptionHandler = null;
    }

    // 中断
    public void interrupt() {
        if (this != Thread.currentThread())
            checkAccess();

        synchronized (blockerLock) {
            Interruptible b = blocker;
            if (b != null) {
                // 只是设置了中断标志位
                interrupt0();
                b.interrupt(this);
                return;
            }
        }
        interrupt0();
    }

    public final synchronized void join(long millis)
    throws InterruptedException {
        long base = System.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
            while (isAlive()) {
                wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
    }

    // 一些静态native方法，由jvm实现
    public static native Thread currentThread();
    public static native void yield();
    public static native void sleep(long millis) throws InterruptedException;

    // 还有一些已经不推荐使用的方法
    @Deprecated
    public final void stop() { }
    @Deprecated
    public final void stop() { }
    @Deprecated
    public void destroy() {
        throw new NoSuchMethodError();
    }
    @Deprecated
    public final void suspend() {
        checkAccess();
        suspend0();
    }
    @Deprecated
    public final void resume() {
        checkAccess();
        resume0();
    }
 
六、总结
（1）线程复用

像线程池类高效的原因在于，线程池中的线程在完成任务后，不会销毁，而且缓存起来，每当用户请求一个线程处理任务时，线程池可以利用缓存的空闲线程来处理用户任务，这样避免了线程创建销毁带来的开销。

在Thread类中有一个Runnable target的域，只需将target替换成新的Runnable即可。

（2）wait()和notify/notifyAll()方法

wait()方法

线程进入WAITING状态，并且释放掉它所占有的“锁标志”，从而使别的线程有机会抢占该锁，等待其他线程调用“锁标志“对象的notify或notifyAll方法恢复
wait方法是一个本地方法，其底层是通过一个叫做监视器锁的对象来完成的，所以调用wait方式时必须获取到monitor对象的所有权即通过Synchronized关键字，否则抛出IllegalMonitorStateException异常
notify/notifyAll()方法

在同一对象上去调用notify/notifyAll方法，就可以唤醒对应对象monitor上等待的线程了。notify和notifyAll的区别在于前者只能唤醒monitor上的一个线程，对其他线程没有影响，而notifyAll则唤醒所有的线程
（3）sleep/yield/join方法解析

sleep

sleep方法的作用是让当前线程暂停指定的时间（毫秒）
wait方法依赖于同步，而sleep方法可以直接调用
sleep方法只是暂时让出CPU的执行权，并不释放锁。而wait方法则需要释放锁
yield

yield方法的作用是暂停当前线程，以便其他线程有机会执行，不过不能指定暂停的时间，并且也不能保证当前线程马上停止
yield只能使同优先级或更高优先级的线程有执行的机会
join

等待调用join方法的线程结束，再继续执行。如：t.join()，主要用于等待t线程运行结束
作用是父线程等待子线程执行完成后再执行，换句话说就是将异步执行的线程合并为同步的线程
（4）不推荐使用方法解释

参考：Why Are Thread.stop, Thread.suspend, Thread.resume and Runtime.runFinalizersOnExit Deprecated?

suspend()和resume()

这两个方法是配套使用的，suspend()是暂停线程，但并不释放资源，容易造成死锁情况
stop()

因为调用stop会使线程释放所有的锁，导致不安全情况，在调用stop时候，由锁保护的临界区可能处于状态不一致的情况，这不一致状态将暴露给其他线程
推荐的做法是，维护一个状态变量，当线程需要停止时更改这一状态变量，该线程应检查这一状态变量，看该线程是否应该终止了
（5）关于interrupt()中断函数

其实调用这个函数并不是真的中断线程，这个函数只是将Thread中的interrupt标志设置为true，用户需自行检测这一变量，停止线程，这种做法避免了stop带来的问题
（6）更深入学习

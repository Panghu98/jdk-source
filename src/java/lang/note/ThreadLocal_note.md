## 关于ThreadLocal

https://www.cnblogs.com/aspirant/p/8991010.html

该类提供了线程局部 (thread-local) 变量。这些变量不同于它们的普通对应物，因为访问某个变量（通过其`get `或 `set `方法）的每个线程都有自己的局部变量，它独立于变量的初始化副本。ThreadLocal实例通常是类中的` private static `字段，它们希望将状态与某一个线程（例如，用户 ID 或事务 ID）相关联。

**ThreadLocal与线程同步机制不同**

- 线程同步机制是多个线程共享同一个变量
- ThreadLocal是为每一个线程创建一个单独的变量副本，所以每一个线程都可以单独地改变自己所拥有的变量副本，而不会影响其他线程所对应的副本

>可以说ThreadLocal为多线程环境下变量的人访问提供了另一种解决思路。

<br>
**ThreadLocal定义了四个方法：**

- get()：返回此线程局部变量的当前线程副本中的值。
- initialValue()：返回此线程局部变量的当前线程的“初始值”。
- remove()：移除此线程局部变量当前线程的值。
- set(T value)：将此线程局部变量的当前线程副本中的值设置为指定值。

**ThreadLocalMap是实现ThreadLocal的关键,ThreadLocal**
- Thread,ThreadLocal,ThreadLocalMap的之间的关系
![QNtS8x.png](https://s2.ax1x.com/2019/12/07/QNtS8x.png)
图片来自 https://www.cnblogs.com/aspirant/p/8991010.html
>1. ThreadLocal实例本身是不存储值，它只是提供了一个在当前线程中找到副本值得key。
2. 是ThreadLocal包含在Thread中，而不是Thread包含在ThreadLocal中

## Java内存模型中工作内存和ThreadLocal之间的关系
![](https://images2015.cnblogs.com/blog/1066658/201706/1066658-20170628211941414-1622377523.png)
 在虚拟机中，堆内存用于存储共享数据（实例对象），堆内存也就是这里说的主内存。
     每个线程将会在堆内存中开辟一块空间叫做线程的工作内存，附带一块缓存区用于存储共享数据副本。那么，共享数据在堆内存当中，线程通信就是通过主内存为中介，线程在本地内存读并且操作完共享变量操作完毕以后，把值写入主内存。
ThreadLocal被称为线程局部变量，说白了，他就是线程工作内存的一小块内存，用于存储数据。

## 常见的关于ThreadLocal的面试题

    ThreadLocal 定义，以及是否可能引起的内存泄露(threadlocalMap的Key是弱引用，用线程池有可能泄露)
每个thread中都存在一个map, map的类型是ThreadLocal.ThreadLocalMap. Map中的key为一个threadlocal实例. 这个Map的确使用了弱引用,不过弱引用只是针对key. 每个key都弱引用指向threadlocal. 当把threadlocal实例置为null以后,没有任何强引用指向threadlocal实例,所以threadlocal将会被gc回收. 但是,我们的value却不能回收,因为存在一条从current thread连接过来的强引用. 只有当前thread结束以后, current thread就不会存在栈中,强引用断开, Current Thread, Map, value将全部被GC回收.

　　所以得出一个结论就是只要这个线程对象被gc回收，就不会出现内存泄露，但在threadLocal设为null和线程结束这段时间不会被回收的，就发生了我们认为的内存泄露。其实这是一个对概念理解的不一致，也没什么好争论的。最要命的是线程对象不被回收的情况，这就发生了真正意义上的内存泄露。比如使用线程池的时候，线程结束是不会销毁的，会再次使用的。就可能出现内存泄露。
　　
Java为了最小化减少内存泄露的可能性和影响，在ThreadLocal的get,set的时候都会清除线程Map里所有key为null的value。所以最怕的情况就是，threadLocal对象设null了，开始发生“内存泄露”，然后使用线程池，这个线程结束，线程放回线程池中不销毁，这个线程一直不被使用，或者分配使用了又不再调用get,set方法，那么这个期间就会发生真正的内存泄露。 　　
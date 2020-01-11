## 关于fail-fast 和 fail-safe的区别
### 快速失败:fail-fast
  在用迭代器遍历一个集合对象时,如果遍历过程中对集合对象的内容进行了修改,则会抛出Concurrent Modification Exception。则会抛出Concurrent Modification Exception。

  原理:迭代器在遍历时直接访问集合中的内容，并且在遍历过程中使用一个 modCount 变量。集合在被遍历期间如果内容发生变化，就会改变modCount的值。每当迭代器使用hashNext()/next()遍历下一个元素之前，都会检测modCount变量是否为expectedmodCount值，是的话就返回遍历；否则抛出异常，终止遍历。

  注意：这里异常的抛出条件是检测到 modCount！=expectedmodCount 这个条件。如果集合发生变化时修改modCount值刚好又设置为了expectedmodCount值，则异常不会抛出。因此，不能依赖于这个异常是否抛出而进行并发操作的编程，这个异常只建议用于检测并发修改的bug。

场景：java.util包下的集合类都是快速失败的，不能在多线程下发生并发修改（迭代过程中被修改）。**在多线程的情况下快速失败的处理机制是不适用的**

### 安全失败（fail—safe）
采用安全失败机制的集合容器，在遍历时不是直接在集合内容上访问的，而是先复制原有集合内容，在拷贝的集合上进行遍历。

  原理：由于迭代时是对原集合的拷贝进行遍历，所以在遍历过程中对原集合所作的修改并不能被迭代器检测到，所以不会触发Concurrent Modification Exception。

  缺点：基于拷贝内容的优点是避免了Concurrent Modification Exception，但同样地，迭代器并不能访问到修改后的内容，即：迭代器遍历的是开始遍历那一刻拿到的集合拷贝，在遍历期间原集合发生的修改迭代器是不知道的。
    
  场景：java.util.concurrent包下的容器都是安全失败，可以在多线程下并发使用，并发修改。**适用于多线程模式**

## ArrayList
[ArrayList源码分析](http://cmsblogs.com/?p=4727)<br>
ArrayList是一种以数组实现的List，与数组相比，它具有动态扩展的能力，因此也可称之为动态数组。

![QUzcvD.png](https://s2.ax1x.com/2019/12/08/QUzcvD.png)

>ArrayList实现了List, RandomAccess, Cloneable,
>java.io.Serializable等接口。
- ArrayList实现了List，提供了基础的添加、删除、遍历等操作。
- ArrayList实现了RandomAccess，提供了随机访问的能力。
- ArrayList实现了Cloneable，可以被克隆。
- ArrayList实现了Serializable，可以被序列化。

（1）DEFAULT_CAPACITY 默认容量为10，也就是通过newArrayList()创建时的默认容量。
 （2）EMPTY_ELEMENTDATA空的数组，这种是通过new ArrayList(0)创建时用的是这个空数组。

## 属性
```
   /**
     * 初始化默认容量。
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * 指定该ArrayList容量为0时，返回该空数组。
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * 当调用无参构造方法，返回的是该数组。刚创建一个ArrayList 时，其内数据量为0。
     * 它与EMPTY_ELEMENTDATA的区别就是：该数组是默认返回的，而后者是在用户指定容量为0时返回。
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * 保存添加到ArrayList中的元素。
     * ArrayList的容量就是该数组的长度。
     * 该值为DEFAULTCAPACITY_EMPTY_ELEMENTDATA 时，当第一次添加元素进入ArrayList中时，数组将扩容值DEFAULT_CAPACITY。
     * 被标记为transient，在对象被序列化的时候不会被序列化。
     */
    transient Object[] elementData; // non-private to simplify nested class access

    /**
     * ArrayList的实际大小（数组包含的元素个数）。
     * @serial
     */
    private int size;
    /**
     * 分派给arrays的最大容量
     * 为什么要减去8呢？
     * 因为某些VM会在数组中保留一些头字，尝试分配这个最大存储容量，可能会导致array容量大于VM的limit，最终导致OutOfMemoryError。
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
```

## 方法说明

**扩容**

1. 进行空间检查，决定是否进行扩容，以及确定最少需要的容量
2. 如果确定扩容，就执行grow(int minCapacity)，minCapacity为最少需要的容量
3. 第一次扩容，逻辑为newCapacity = oldCapacity + (oldCapacity >> 1);即在原有的容量基础上增加一半。
4. 第一次扩容后，如果容量还是小于minCapacity，就将容量扩充为minCapacity。
5. 对扩容后的容量进行判断，如果大于允许的最大容量MAX_ARRAY_SIZE，则将容量再次调整为MAX_ARRAY_SIZE。至此扩容操作结束。

## ArrayList小结
（1）ArrayList内部使用数组存储元素，当数组长度不够时进行扩容，每次加一半的空间，ArrayList不会进行缩容；

（2）ArrayList支持随机访问，通过索引访问元素极快，时间复杂度为O(1)；

（3）ArrayList添加元素到尾部极快，平均时间复杂度为O(1)；

（4）ArrayList添加元素到中间比较慢，因为要搬移元素，平均时间复杂度为O(n)；

（5）ArrayList从尾部删除元素极快，时间复杂度为O(1)；

（6）ArrayList从中间删除元素比较慢，因为要搬移元素，平均时间复杂度为O(n)；

（7）ArrayList支持求并集，调用addAll(Collection<? extends E> c)方法即可；

（8）ArrayList支持求交集，调用retainAll(Collection<? extends E> c)方法即可；

（9）ArrayList支持求单向差集，调用removeAll(Collection<? extends E>
c)方法即可；


>elementData定义为transient的优势，自己根据size序列化真实的元素，而不是根据数组的长度序列化元素，减少了空间占用。

----

## LinkedList
![LinkedList的继承体系图](https://s2.ax1x.com/2019/12/08/QaSkqJ.png)
>LinkedLIst是一个双向链表实现的List,它除了作为LIst使用,还可以作为队列或者栈来使用

内部类节点结构
```
private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```
>从源码上来看就是一个典型的双端队列的实现,和ArrayList一样,LinkedList=使用的一样的是快速失败的策略


### 属性

```
// 元素个数
transient int size = 0;
// 链表首节点
transient Node<E> first;
// 链表尾节点
transient Node<E> last;
```

### 常见方法
在双向队列中使用指定索引位置进行插入的时候,因为是双向队列,通过判断index的位置是在前半段还是后半段来使用对应的尾插和头插

- 增加节点的方式
![Qandq1.png](https://s2.ax1x.com/2019/12/08/Qandq1.png)

- 删除节点的方式
![QanyGD.png](https://s2.ax1x.com/2019/12/08/QanyGD.png)

>不论是对于头结点和尾节点进行删除或者是增加,效率都是`O(1)`

### LinkedList小结
（1）LinkedList是一个以双链表实现的List；

（2）LinkedList还是一个双端队列，具有队列、双端队列、栈的特性；

（3）LinkedList在队列首尾添加、删除元素非常高效，时间复杂度为O(1)；

（4）LinkedList在中间添加、删除元素比较低效，时间复杂度为O(n)；

（5）LinkedList不支持随机访问，所以访问非队列首尾的元素比较低效；

（6）LinkedList在功能上等于ArrayList + ArrayDeque；

---
## HashMap
**HashMap结构示意图**
![QdR1sO.png](https://s2.ax1x.com/2019/12/09/QdR1sO.png)
![QaKens.png](https://s2.ax1x.com/2019/12/08/QaKens.png)
>在Java中，HashMap的实现采用了（数组 + 链表 + 红黑树）的复杂结构，数组的一个元素又称作桶。
在添加元素时，会根据hash值算出元素在数组中的位置，如果该位置没有元素，则直接把元素放置在此处，如果该位置有元素了，则把元素以链表的形式放置在链表的尾部。
当一个链表的元素个数达到一定的数量（且数组的长度达到一定的长度）后，则把链表转化为红黑树，从而提高效率。
数组的查询效率为O(1)，链表的查询效率是O(k)，红黑树的查询效率是O(log k)，k为桶中的元素个数，所以当元素数量非常多的时候，转化为红黑树能极大地提高效率。

### 属性

- **Node内部类**
Node是一个典型的单链表节点，其中，hash用来存储key计算得来的hash值。

	static class Node<K,V> implements Map.Entry<K,V> {
	    final int hash;
	    final K key;
	    V value;
	    Node<K,V> next;
	}

- **TreeNode内部类**
这是一个神奇的类，它继承自LinkedHashMap中的Entry类，关于LInkedHashMap.Entry这个类我们后面再讲。

TreeNode是一个典型的树型节点，其中，prev是链表中的节点，用于在删除元素的时候可以快速找到它的前置节点。

	// 位于HashMap中
		static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
		    TreeNode<K,V> parent;  // red-black tree links
		    TreeNode<K,V> left;
		    TreeNode<K,V> right;
		    TreeNode<K,V> prev;    // needed to unlink next upon deletion
		    boolean red;
		}
	
	// 位于LinkedHashMap中，典型的双向链表节点
	static class Entry<K,V> extends HashMap.Node<K,V> {
	    Entry<K,V> before, after;
	    Entry(int hash, K key, V value, Node<K,V> next) {
	        super(hash, key, value, next);
	    }
	}


### 方法
**put()方法示意图**
![Q0Z9wq.png](https://s2.ax1x.com/2019/12/09/Q0Z9wq.png)

**HashMap的resize**
- 为什么要进行扩容
当HashMap中的元素在越来越多的时候,碰撞的几率也就会越来越高（因为数组的长度是固定的），所以为了提高查询的效率，就要对hashmap的数组进行扩容，数组扩容这个操作也会出现在ArrayList中，所以这是一个通用的操作，很多人对它的性能表示过怀疑，不过想想我们的“均摊”原理，就释然了，而在hashmap数组扩容之后，最消耗性能的点就出现了：原数组中的数据必须重新计算其在新数组中的位置，并放进去，这就是resize。 

- 何时进行扩容
当hashMap中的元素超过数组大小(**capacity**) *  loadFactor(**threshold = capacity * loadFactor**)时，就会进行数组扩容，loadFactor的默认值为0.75，也就是说，默认情况下，数组大小为16，那么当hashmap中元素个数超过16*0.75=12的时候，就把数组的大小扩展为2*16=32，即扩大一倍，然后重新计算每个元素在数组中的位置，而这是一个非常消耗性能的操作，所以如果我们已经预知hashmap中元素的个数，那么预设元素的个数能够有效的提高hashmap的性能。比如说，我们有1000个元素new HashMap(1000), 但是理论上来讲new HashMap(1024)更合适，即使是1000，hashmap也自动会将其设置为1024。 但是new HashMap(1024)还不是更合适的，因为0.75*1000 < 1000, 也就是说为了让0.75 * size > 1000, 我们必须这样new HashMap(2048)才最合适，既考虑了&的问题，也避免了resize的问题。
	


### 总结

- [关于HashMap在JDK1.7 和 1.8的区别](https://blog.csdn.net/qq_36520235/article/details/82417949) 

 ![Q0VHTP.png](https://s2.ax1x.com/2019/12/09/Q0VHTP.png)

- [关于reHash出现死循环](https://www.jianshu.com/p/1e9cf0ac07f4) 

（1）HashMap是一种散列表，采用（数组 + 链表 + 红黑树）的存储结构；

（2）HashMap的默认初始容量为16（1<<4），默认装载因子为0.75f，容量总是2的n次方；

>这里提一下为什么要求是2的n次方,在进行Hash运算的时候
算key得hashcode值，然后跟数组的长度-1做一次“与”运算（&）。

>当HashMap使用put方法的时候需要寻找桶的位置`(n - 1) & hash`--n代表数组的长度,当数组为2^n^时,出现哈希冲突的概率更小


（3）HashMap扩容时每次容量变为原来的两倍；

（4）**当桶的数量小于64时不会进行树化，只会扩容；**

（5）**当桶的数量大于64且单个桶中元素的数量大于8时，进行树化；**

（6）**当单个桶中元素数量小于6时，进行反树化；**
>树化是针对于单桶而言

（7）HashMap是非线程安全的容器；

（8）HashMap查找添加元素的时间复杂度都为O(1)；

---
## HashSet
HashSet是Set的一种实现方式，底层主要使用HashMap来确保元素不重复。只对键进行存储

### 属性
``` 内部使用HashMap
    private transient HashMap<E,Object> map;

    // 虚拟对象，用来作为value放到map中
    private static final Object PRESENT = new Object();
    
```

> 注意这里使用的Value是类静态变量，所以不需要重复的创建对象，只需要在init方法中初始化该对象即可



### 常见方法

**构造方法**

```
public HashSet() {
    map = new HashMap<>();
}

public HashSet(Collection<? extends E> c) {
    map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
    addAll(c);
}

public HashSet(int initialCapacity, float loadFactor) {
    map = new HashMap<>(initialCapacity, loadFactor);
}

public HashSet(int initialCapacity) {
    map = new HashMap<>(initialCapacity);
}

// 非public，主要是给LinkedHashSet使用的
HashSet(int initialCapacity, float loadFactor, boolean dummy) {
    map = new LinkedHashMap<>(initialCapacity, loadFactor);
}
```

>构造方法都是调用HashMap对应的构造方法。

最后一个构造方法有点特殊，它不是public的，意味着它只能被同一个包或者子类调用，这是LinkedHashSet专属的方法。





**添加元素**

直接调用HashMap的put()方法，把元素本身作为key，把PRESENT作为value，也就是这个map中所有的value都是一样的。



```
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}
```

> 使用静态



**删除元素**

直接调用HashMap的remove()方法，注意map的remove返回是删除元素的value，而Set的remov返回的是boolean类型。

这里要检查一下，如果是null的话说明没有该元素，如果不是null肯定等于PRESENT。

```
public boolean remove(Object o) {
    return map.remove(o)==PRESENT;
}
```





### 查询元素

Set没有get()方法哦，因为get似乎没有意义，不像List那样可以按index获取元素。

这里只要一个检查元素是否存在的方法contains()，直接调用map的containsKey()方法。

```
public boolean contains(Object o) {
    return map.containsKey(o);
}
```



### 遍历元素

直接调用map的keySet的迭代器。

```
public Iterator<E> iterator() {
    return map.keySet().iterator();
}
```



### 常见面试题

（1）阿里手册上有说，使用java中的集合时要自己指定集合的大小，通过这篇源码的分析，你知道初始化HashMap的时候初始容量怎么传吗？

我们发现有下面这个构造方法，很清楚明白地告诉了我们怎么指定容量。

假如，我们预估HashMap要存储n个元素，那么，它的容量就应该指定为((n/0.75f) + 1)，如果这个值小于16，那就直接使用16得了。

初始化时指定容量是为了减少扩容的次数，提高效率。

```
public HashSet(Collection<? extends E> c) {
    map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
    addAll(c);
}
```

（2）什么是fail-fast？

fail-fast机制是java集合中的一种错误机制。

当使用迭代器迭代时，如果发现集合有修改，则快速失败做出响应，抛出ConcurrentModificationException异常。

这种修改有可能是其它线程的修改，也有可能是当前线程自己的修改导致的，比如迭代的过程中直接调用remove()删除元素等。

另外，并不是java中所有的集合都有fail-fast的机制。比如，像最终一致性的ConcurrentHashMap、CopyOnWriterArrayList等都是没有fast-fail的。

那么，fail-fast是怎么实现的呢？

细心的同学可能会发现，像ArrayList、HashMap中都有一个属性叫`modCount`，每次对集合的修改这个值都会加1，在遍历前记录这个值到`expectedModCount`中，遍历中检查两者是否一致，如果出现不一致就说明有修改，则抛出ConcurrentModificationException异常。



### 总结

（1）HashSet内部使用HashMap的key存储元素，以此来保证元素不重复；

（2）HashSet是无序的，因为HashMap的key是无序的；

（3）HashSet中允许有一个null元素，因为HashMap允许key为null；

（4）HashSet是非线程安全的；

（5）HashSet是没有get()方法的；



### LinkedHashMap
[LinkedHashMap源码解析](https://www.imooc.com/article/22931)







## ConcurrentHashMap



### 简介

ConcurrentHashMap是HashMap的线程安全版本，内部也是使用（数组 + 链表 + 红黑树）的结构来存储元素。

相比于同样线程安全的HashTable来说，效率等各方面都有极大地提高。

![l5SOTs.png](https://s2.ax1x.com/2020/01/11/l5SOTs.png)

## 常用方法

#### 构造方法

```
public ConcurrentHashMap() {
}

public ConcurrentHashMap(int initialCapacity) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException();
    int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ?
            MAXIMUM_CAPACITY :
            tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
    this.sizeCtl = cap;
}

public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
    this.sizeCtl = DEFAULT_CAPACITY;
    putAll(m);
}

public ConcurrentHashMap(int initialCapacity, float loadFactor) {
    this(initialCapacity, loadFactor, 1);
}

public ConcurrentHashMap(int initialCapacity,
                         float loadFactor, int concurrencyLevel) {
    if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
        throw new IllegalArgumentException();
    if (initialCapacity < concurrencyLevel)   // Use at least as many bins
        initialCapacity = concurrencyLevel;   // as estimated threads
    long size = (long)(1.0 + (long)initialCapacity / loadFactor);
    int cap = (size >= (long)MAXIMUM_CAPACITY) ?
            MAXIMUM_CAPACITY : tableSizeFor((int)size);
    this.sizeCtl = cap;
}
```

> 构造方法与HashMap的构造方法对比而言 没有了HashMap中的threshold和loadFactor，而是改用了sizeCtl来控制，而且只存储了容量在里面，那么它是怎么用的呢？官方给出的解释如下： 
>
> （1）-1，表示有线程正在进行初始化操作
>
> （2）-(1 + nThreads)，表示有n个线程正在一起扩容
>
> （3）0，默认值，后续在真正初始化的时候使用默认容量
>
> （4）> 0，初始化或扩容完成后下一次的扩容门槛



### 添加元素

```
public V put(K key, V value) {
    return putVal(key, value, false);
}

final V putVal(K key, V value, boolean onlyIfAbsent) {
    // key和value都不能为null
    if (key == null || value == null) throw new NullPointerException();
    // 计算hash值
    int hash = spread(key.hashCode());
    // 要插入的元素所在桶的元素个数
    int binCount = 0;
    // 死循环，结合CAS使用（如果CAS失败，则会重新取整个桶进行下面的流程）
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            // 如果桶未初始化或者桶个数为0，则初始化桶
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            // 如果要插入的元素所在的桶还没有元素，则把这个元素插入到这个桶中
            if (casTabAt(tab, i, null,
            
            
            	
            	//这里使用CAS的意义在于多个线程进行访问的时候进行死循环判断，判定结束的条件是
            	//该桶元素不为null,如果满足该条件则进行更新，否则退出
            	
       
            	
                    new Node<K,V>(hash, key, value, null)))
                // 如果使用CAS插入元素时，发现已经有元素了，则进入下一次循环，重新操作
                // 如果使用CAS插入元素成功，则break跳出循环，流程结束
                break;                   // no lock when adding to empty bin
        }
        else if ((fh = f.hash) == MOVED)
            // 如果要插入的元素所在的桶的第一个元素的hash是MOVED，则当前线程帮忙一起迁移元素
            tab = helpTransfer(tab, f);
        else {
            // 如果这个桶不为空且不在迁移元素，则锁住这个桶（分段锁）
            // 并查找要插入的元素是否在这个桶中
            // 存在，则替换值（onlyIfAbsent=false）
            // 不存在，则插入到链表结尾或插入树中
            V oldVal = null;
            synchronized (f) {
                // 再次检测第一个元素是否有变化，如果有变化则进入下一次循环，从头来过
                if (tabAt(tab, i) == f) {
                    // 如果第一个元素的hash值大于等于0（说明不是在迁移，也不是树）
                    // 那就是桶中的元素使用的是链表方式存储
                    if (fh >= 0) {
                        // 桶中元素个数赋值为1
                        binCount = 1;
                        // 遍历整个桶，每次结束binCount加1
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                            (ek != null && key.equals(ek)))) {
                                // 如果找到了这个元素，则赋值了新值（onlyIfAbsent=false）
                                // 并退出循环
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                // 如果到链表尾部还没有找到元素
                                // 就把它插入到链表结尾并退出循环
                                pred.next = new Node<K,V>(hash, key,
                                        value, null);
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {
                        // 如果第一个元素是树节点
                        Node<K,V> p;
                        // 桶中元素个数赋值为2
                        binCount = 2;
                        // 调用红黑树的插入方法插入元素
                        // 如果成功插入则返回null
                        // 否则返回寻找到的节点
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                value)) != null) {
                            // 如果找到了这个元素，则赋值了新值（onlyIfAbsent=false）
                            // 并退出循环
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            // 如果binCount不为0，说明成功插入了元素或者寻找到了元素
            if (binCount != 0) {
                // 如果链表元素个数达到了8，则尝试树化
                // 因为上面把元素插入到树中时，binCount只赋值了2，并没有计算整个树中元素的个数
                // 所以不会重复树化
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                // 如果要插入的元素已经存在，则返回旧值
                if (oldVal != null)
                    return oldVal;
                // 退出外层大循环，流程结束
                break;
            }
        }
        }
        // 成功插入元素，元素个数加1（是否要扩容在这个里面）
        addCount(1L, binCount);
        // 成功插入元素返回null
        return null;
    }
```

> [关于CAS实现的原理]( https://juejin.im/post/5a73cbbff265da4e807783f5 )

----


## [集合框架底层数据结构总结](https://snailclimb.gitee.io/javaguide/#/docs/java/collection/Java集合框架常见面试题?id=集合框架底层数据结构总结)

### [Collection](https://snailclimb.gitee.io/javaguide/#/docs/java/collection/Java集合框架常见面试题?id=collection)

#### [1. List](https://snailclimb.gitee.io/javaguide/#/docs/java/collection/Java集合框架常见面试题?id=_1-list)

- **Arraylist：** Object 数组
- **Vector：** Object 数组
- **LinkedList：** 双向链表 (JDK1.6 之前为循环链表，JDK1.7 取消了循环)

#### [2. Set](https://snailclimb.gitee.io/javaguide/#/docs/java/collection/Java集合框架常见面试题?id=_2-set)

- **HashSet（无序，唯一）:** 基于 HashMap 实现的，底层采用 HashMap 来保存元素
- **LinkedHashSet：** LinkedHashSet 继承于 HashSet，并且其内部是通过 LinkedHashMap 来实现的。有点类似于我们之前说的 LinkedHashMap 其内部是基于 HashMap 实现一样，不过还是有一点点区别的
- **TreeSet（有序，唯一）：** 红黑树 (自平衡的排序二叉树)

### [Map](https://snailclimb.gitee.io/javaguide/#/docs/java/collection/Java集合框架常见面试题?id=map)

- **HashMap：** JDK1.8 之前 HashMap 由数组 + 链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的（“拉链法” 解决冲突）。JDK1.8 以后在解决哈希冲突时有了较大的变化，当链表长度大于阈值（默认为 8）时，将链表转化为红黑树，以减少搜索时间
- **LinkedHashMap：** LinkedHashMap 继承自 HashMap，所以它的底层仍然是基于拉链式散列结构即由数组和链表或红黑树组成。另外，LinkedHashMap 在上面结构的基础上，增加了一条双向链表，使得上面的结构可以保持键值对的插入顺序。同时通过对链表进行相应的操作，实现了访问顺序相关逻辑。详细可以查看：[《LinkedHashMap 源码详细分析（JDK1.8）》](https://www.imooc.com/article/22931)
- **Hashtable：** 数组 + 链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的
- **TreeMap：** 红黑树（自平衡的排序二叉树）

## [如何选用集合?](https://snailclimb.gitee.io/javaguide/#/docs/java/collection/Java集合框架常见面试题?id=如何选用集合)

主要根据集合的特点来选用，比如我们需要根据键值获取到元素值时就选用 Map 接口下的集合，需要排序时选择 TreeMap, 不需要排序时就选择 HashMap, 需要保证线程安全就选用 ConcurrentHashMap. 当我们只需要存放元素值时，就选择实现 Collection 接口的集合，需要保证元素唯一时选择实现 Set 接口的集合比如 TreeSet 或 HashSet，不需要就选择实现 List 接口的比如 ArrayList 或 LinkedList，然后再根据实现这些接口的集合的特点来选用。





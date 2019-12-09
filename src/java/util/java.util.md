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

（1）DEFAULT_CAPACITY 默认容量为10，也就是通过new
ArrayList()创建时的默认容量。 （2）EMPTY_ELEMENTDATA
空的数组，这种是通过new ArrayList(0)创建时用的是这个空数组。

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


在双向队列中使用指定索引位置进行插入的时候,因为是双向队列,通过判断index的位置是在前半段还是后半段来使用对应的尾插和头插

- 增加节点的方式
![Qandq1.png](https://s2.ax1x.com/2019/12/08/Qandq1.png)

- 删除节点的方式
![QanyGD.png](https://s2.ax1x.com/2019/12/08/QanyGD.png)

>不论是对于头结点和尾节点进行删除或者是增加,效率都是`O(1)`

## LinkedList小结
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
	
	
	

**总结**

（1）HashMap是一种散列表，采用（数组 + 链表 + 红黑树）的存储结构；

（2）HashMap的默认初始容量为16（1<<4），默认装载因子为0.75f，容量总是2的n次方；

（3）HashMap扩容时每次容量变为原来的两倍；

（4）当桶的数量小于64时不会进行树化，只会扩容；

（5）当桶的数量大于64且单个桶中元素的数量大于8时，进行树化；

（6）当单个桶中元素数量小于6时，进行反树化；

（7）HashMap是非线程安全的容器；

（8）HashMap查找添加元素的时间复杂度都为O(1)；

## Object篇
常用方法 <br> `getClass、hashCode、equals、clone、toString、notify、wait`
<br>这些方法大多数都是native方法
- finalize方法
  该方法用于当对象被回收时调用，这个由JVM支持，Object的finalize方法默认是什么都没有做，如果子类需要在对象被回收时执行一些逻辑处理，则可以重写finalize方法。
  > 任何一个对象的finalize()方法都只会被虚拟机自动调用一次，不建议使用finalize（）
  > 方法来拯救对象。运行代价大，不确定性大。相对来说try-finally可以做的更好
  
  ---
## String
 java
 语言使用String类代表字符串，实际上String对象的值是一个常量，一旦创建后就不可变，
 所以它是线程安全的，可以被多个线程共享。 <br>
###  String的底层实现
 ```
 private final char value[];
 ```
> 使用不可变字符串，说明String对象是一个常量，是不可变的，线程安全的
###  String.intern()方法 <br> 
**字符串的对象创建**

- 直接使用双引号创建字符串
>判断这个常量是否存在于常量池，
如果存在，判断这个常量是存在的引用还是常量，
如果是引用，返回引用地址指向的堆空间对象，
如果是常量，则直接返回常量池常量，
如果不存在，在常量池中创建该常量，并返回此常量

- 使用String(String str)的构造方法进行创建
>首先在堆上创建对象(无论堆上是否存在相同字面量的对象),然后判断常量池上是否存在字符串的字面量，
如果不存在，在常量池上创建常量
如果存在,不做任何操作
- 双引号相加
>判断这两个常量、相加后的常量在常量池上是否存在
如果不存在,则在常量池上创建相应的常量
如果存在,判断这个常量是存在的引用还是常量，如果是引用，返回引用地址指向的堆空间对象，如果是常量，则直接返回常量池常量，
- 两个new String相加
>首先会创建这两个对象以及相加后的对象然后判断常量池中是否存在这两个对象的字面量常量
如果存在,不做任何操作
如果不存在,则在常量池上创建对应常量
- 双引号字符串与new String字符串
>首先创建两个对象，一个是new String的对象，一个是相加后的对象
 然后判断双引号常量与new String的字面量在常量池是否存在
  如果存在,不做操作
  如果不存在,则在常量池上创建对象的常量


- intern()方法分析
>判断这个常量是否存在于常量池。
如果存在,判断存在内容是引用还是常量，
    如果是引用，返回引用地址指向堆空间对象，
    如果是常量，直接返回常量池常量
如果不存在，
  将当前对象引用复制到常量池,并且返回的是当前对象的引用

---
## StringBuffer和StringBuilder
>StringBuilder和StringBuffer的内部实现跟String类一样，都是通过一个char数组存储字符串的，不同的是String类里面的char数组是final修饰的，是不可变的，而StringBuilder和StringBuffer的char数组是可变的。

- StringBuiler为什么线程不安全

>ensureCapacityInternal()方法是检查StringBuilder对象的原char数组的容量能不能盛下新的字符串，如果盛不下就调用expandCapacity()方法对char数组进行扩容。



	public void ensureCapacity(int minimumCapacity) {
	        if (minimumCapacity > 0)
	            ensureCapacityInternal(minimumCapacity);
	    }

 

扩容的逻辑就是new一个新的char数组，新的char数组的容量是原来char数组的两倍再加2，再通过System.arryCopy()函数将原数组的内容复制到新数组，最后将指针指向新的char数组。


	 private void ensureCapacityInternal(int minimumCapacity) {
	        // overflow-conscious code
	        if (minimumCapacity - value.length > 0) {
	            //创建一个新的字符数组
	            value = Arrays.copyOf(value,
	                    newCapacity(minimumCapacity));
	        }
	    }


​    
![单线程扩容](https://s2.ax1x.com/2019/12/05/QGDIVe.png)
![多线程扩容](https://s2.ax1x.com/2019/12/05/QGDb8I.png)

>扩容的时候两个线程同时读取到一个不需要扩容的边界值，直接进行插入操作，但是同时另外一个线程已经插入（数组没有及时进行扩容，导致数组溢出，抛出异常）
## 整型
>整型中大多数关于数值的操作的方法都是使用的位移进行操作，使用的比较巧妙。
`Byte,Short,Integer,Long`等整型类其内部都存在一个Cache数组，默认值是-127到128，可配置虚拟机参数设置该大小
（只能是-127~128+，如果小于128还是会选择128）

整型创建对象的时候会使用valueOf方法，该方法会判断在整型的数值是否在数组当中，如果在直接返回，不在的话在堆中创建一个对象。

---
## Boolean类型

```
 public static final Boolean TRUE = new Boolean(true);
  public static final Boolean FALSE = new Boolean(false);
```
>Boolean只有两种形式的对象，在Boolean类在进行类加载的过程中，就已经初始化了两种类型的Boolean变量。因此推荐使用
`Boolean b = TRUE` 或者 `Boolean b = FALSE`的方式进行类的初始化。

## Double,FLoat
在某些方法中的位运算运用的很巧妙

## 基本类型封装类小结
- 在基本类型使用valueOf获取对象的时候，会先去寻找这个对象是否已经存在于常量池，例如整型调用valueOf方法去寻找内部类所拥有的中是否有这个值，如果存在则直接返回，不存在则创建
- 基本类中有一个`TYPE`属性，该属性用于获取该封装类所对应的基本类型

## Thread
[Thread类阅读](https://github.com/Panghu98/jdk-source/blob/master/src/java/lang/note/java.lang.md)

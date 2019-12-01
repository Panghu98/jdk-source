public class Test{

    public static void main(String[] args) {
        Integer a = new Integer(3); Integer b = 3; // 将3自动装箱成Integer类型
        System.err.println(a.hashCode());
        System.out.println(b.hashCode());
        int c = 3;
        System.err.println(1>>3);
        Integer d = new Integer(3);
        System.out.println(d == a);
        System.out.println(a == b); // false 两个引用没有引用同一对象,a是创建了一个新的对象
        System.out.println(a == c); // true a自动拆箱成int类型再和c比较
    }

    /**
     * 如果是换成Integer b = new Integer.java(3)的话使用==符号进行判断的话，就是true
     *
     * 当我们给一个Integer对象赋一个int值的时候，会调用Integer类的静态方法valueOf
     */

}
package test.java.lang;

public class StringTest {

    public static void main(String[] args) {

        String str1 = "a";
        String str2 = "b";
        String str3 = "ab"; //常量池
        String str4 = str1 + str2; //与str1+str2不等
        String str5 = new String("ab");

        System.out.println(str5.equals(str3));//true
        System.out.println(str5 == str3);//false
        System.out.println(str5.intern() == str3);//true
        System.out.println(str5.intern() == str4);//false
    }

}

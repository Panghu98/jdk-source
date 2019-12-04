package test.java.lang;

public class IntegerTest {

    public static int countBit(int number) {
        int count = 0;

        while (number != 0) {
            ++count;
            number = (number-1) & number;
        }

        return count;
    }

    public static void main(String[] args) {
        int number = 12331274;
        long start = System.currentTimeMillis();
        int result = Integer.bitCount(number);
        long end = System.currentTimeMillis();
        System.err.println(result);
        System.err.println(end-start);
        start = System.currentTimeMillis();
        result = countBit(number);
        end = System.currentTimeMillis();
        System.err.println(result);
        System.err.println(end-start);
        Integer.highestOneBit(128);

    }

}

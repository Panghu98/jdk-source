package test.java;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ConcurrentHashMapTest {

    public static void main(String[] args) {
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>();
        map.put(1,1);
    }

}

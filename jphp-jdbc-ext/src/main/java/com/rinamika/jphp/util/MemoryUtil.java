package com.rinamika.jphp.util;

import php.runtime.memory.ArrayMemory;

import java.util.Properties;

public class MemoryUtil {

    public static Properties toProperties(ArrayMemory memory) {
        Properties props = new Properties();
        for (Object key : memory.keySet()) {
            props.put(key.toString(), memory.getByScalar(key));
        }
        return props;
    }

    public static int[] toIntArray(ArrayMemory memory) {
        int arr[] = new int[memory.size()];
        int index = 0;
        for (Object key : memory.keySet()) {
            arr[index] = memory.getByScalar(key).toInteger();
        }
        return arr;
    }

    public static String[] toStringArray(ArrayMemory memory) {
        String arr[] = new String[memory.size()];
        int index = 0;
        for (Object key : memory.keySet()) {
            arr[index] = memory.getByScalar(key).toString();
        }
        return arr;
    }
}

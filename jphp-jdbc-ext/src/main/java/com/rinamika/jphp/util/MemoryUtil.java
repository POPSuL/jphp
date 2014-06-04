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
}

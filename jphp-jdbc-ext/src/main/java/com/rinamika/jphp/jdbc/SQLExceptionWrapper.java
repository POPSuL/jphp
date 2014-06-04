package com.rinamika.jphp.jdbc;

import php.runtime.annotation.Reflection;
import php.runtime.env.Environment;
import php.runtime.ext.java.JavaException;
import php.runtime.reflection.ClassEntity;

@Reflection.Name("java\\sql\\SQLException")
public class SQLExceptionWrapper extends JavaException {

    public SQLExceptionWrapper(Environment env, Throwable throwable) {
        super(env, throwable);
    }

    public SQLExceptionWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}

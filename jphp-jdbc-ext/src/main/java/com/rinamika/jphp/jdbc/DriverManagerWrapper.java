package com.rinamika.jphp.jdbc;

import com.rinamika.jphp.util.MemoryUtil;
import php.runtime.Memory;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.ArrayMemory;
import php.runtime.memory.LongMemory;
import php.runtime.memory.ObjectMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import static php.runtime.annotation.Reflection.*;

@Name("java\\sql\\DriverManager")
public class DriverManagerWrapper extends BaseObject {

    private DriverManager mManager;

    public DriverManagerWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public DriverManagerWrapper(Environment env, DriverManager m) {
        super(env);
        mManager = m;
    }

    public static void exception(Environment env, String message, Object... args){
        SQLExceptionWrapper exception = new SQLExceptionWrapper(env, env.fetchClass("java\\sql\\SQLException"));
        exception.__construct(env, new StringMemory(String.format(message, args)));
        env.__throwException(exception);
    }

    @Signature({@Arg(value = "driver", typeClass = "java\\sql\\Driver")})
    public static Memory deregisterDriver(Environment env, Memory... args) {
        try {
            DriverWrapper wrapper = (DriverWrapper) ((ObjectMemory) args[0]).value;
            DriverManager.deregisterDriver(wrapper.getDriver());
        } catch (SQLException e) {
            exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "url"),
        @Arg(value = "info", optional = @Optional("NULL")),
        @Arg(value = "password", optional = @Optional("NULL"))})
    public static Memory getConnection(Environment env, Memory... args) {
        try {
            String url = args[0].toString();
            Connection c;
            if (args[1].isArray()) {
                c = DriverManager.getConnection(url, MemoryUtil.toProperties((ArrayMemory) args[1]));
            } else if (args[1].isString() && args[2].isString()) {
                c = DriverManager.getConnection(url, args[1].toString(), args[2].toString());
            } else {
                c = DriverManager.getConnection(args[0].toString());
            }
            return new ConnectionWrapper(env, c).toMemory();
        } catch (SQLException e) {
            exception(env, e.getMessage());
            return Memory.NULL;
        }
    }

    @Signature({@Arg(value = "url")})
    public static Memory getDriver(Environment env, Memory... args) {
        try {
            Driver driver = DriverManager.getDriver(args[0].toString());
            return new DriverWrapper(env, driver).toMemory();
        } catch (SQLException e) {
            exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public static Memory getDrivers(Environment env, Memory... args) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        ArrayMemory driversMemory = new ArrayMemory();
        while(drivers.hasMoreElements()) {
            driversMemory.add(new ObjectMemory(new DriverWrapper(env, drivers.nextElement())));
        }
        return driversMemory;
    }

    @Signature
    public static Memory getLoginTimeout(Environment env, Memory... args) {
        return LongMemory.valueOf(DriverManager.getLoginTimeout());
    }

    @Signature({@Arg(value = "message")})
    public static Memory println(Environment env, Memory... args) {
        DriverManager.println(args[0].toString());
        return Memory.NULL;
    }

    @Signature({@Arg(value = "seconds")})
    public static Memory setLoginTimeout(Environment env, Memory... args) {
        DriverManager.setLoginTimeout(args[0].toInteger());
        return Memory.NULL;
    }
}

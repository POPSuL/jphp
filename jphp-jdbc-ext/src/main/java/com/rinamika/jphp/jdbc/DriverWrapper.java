package com.rinamika.jphp.jdbc;

import com.rinamika.jphp.util.MemoryUtil;
import php.runtime.Memory;
import php.runtime.annotation.Reflection;
import php.runtime.common.HintType;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.ArrayMemory;
import php.runtime.memory.LongMemory;
import php.runtime.memory.ObjectMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

@Reflection.Name("java\\sql\\Driver")
public class DriverWrapper extends BaseObject {

    private Driver mDriver;

    public DriverWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public DriverWrapper(Environment env, Driver driver) {
        super(env);
        mDriver = driver;
    }

    @Reflection.Signature({@Reflection.Arg(value = "url", type = HintType.STRING)})
    public Memory acceptsURL(Environment env, Memory... args) {
        try {
            return mDriver.acceptsURL(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Reflection.Signature({@Reflection.Arg(value = "url", type = HintType.STRING),
        @Reflection.Arg(value = "info", type = HintType.ARRAY)})
    public Memory connect(Environment env, Memory... args) {
        try {
            Connection conn = mDriver.connect(args[0].toString(), MemoryUtil.toProperties((ArrayMemory) args[1]));
            return new ObjectMemory(new ConnectionWrapper(env, conn));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Reflection.Signature
    public Memory getMajorVersion(Environment env, Memory... args) {
        return LongMemory.valueOf(mDriver.getMajorVersion());
    }

    @Reflection.Signature
    public Memory getMinorVersion(Environment env, Memory... args) {
        return LongMemory.valueOf(mDriver.getMinorVersion());
    }

    @Reflection.Signature
    public Memory jdbcCompliant(Environment env, Memory... args) {
        return mDriver.jdbcCompliant() ? Memory.TRUE : Memory.FALSE;
    }

    @Reflection.Signature
    public Memory getNativeClassName(Environment env, Memory... args) {
        return StringMemory.valueOf(mDriver.getClass().getName());
    }

    public Driver getDriver() {
        return mDriver;
    }
}

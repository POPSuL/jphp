package com.rinamika.jphp.jdbc;

import php.runtime.Memory;
import php.runtime.annotation.Reflection;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.LongMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.SQLException;
import java.sql.Savepoint;

@Reflection.Name("java\\sql\\Savepoint")
public class SavepointWrapper extends BaseObject {

    private Savepoint mSavepoint;

    public SavepointWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public SavepointWrapper(Environment env, Savepoint sp) {
        super(env);
        mSavepoint = sp;
    }

    @Reflection.Signature
    public Memory getSavepointId(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mSavepoint.getSavepointId());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Reflection.Signature
    public Memory getSavepointName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mSavepoint.getSavepointName());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return StringMemory.NULL;
    }

    public Savepoint getSavepoint() {
        return mSavepoint;
    }
}

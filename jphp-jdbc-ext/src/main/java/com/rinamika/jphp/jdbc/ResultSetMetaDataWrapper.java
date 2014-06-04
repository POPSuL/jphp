package com.rinamika.jphp.jdbc;

import php.runtime.Memory;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.LongMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static php.runtime.Memory.*;
import static php.runtime.Memory.FALSE;
import static php.runtime.annotation.Reflection.*;

@Name("java\\sql\\ResultSetMetaData")
public class ResultSetMetaDataWrapper extends BaseObject {

    public static final int columnNoNulls = ResultSetMetaData.columnNoNulls;
    public static final int columnNullable = ResultSetMetaData.columnNullable;
    public static final int columnNullableUnknown = ResultSetMetaData.columnNullableUnknown;

    private ResultSetMetaData mData;

    public ResultSetMetaDataWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public ResultSetMetaDataWrapper(Environment env, ResultSetMetaData data) {
        super(env);
        mData = data;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return mData;
    }

    @Signature({@Arg(value = "column")})
    public Memory getCatalogName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getCatalogName(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getColumnClassName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getColumnClassName(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getColumnCount(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mData.getColumnCount());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getColumnDisplaySize(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mData.getColumnDisplaySize(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getColumnLabel(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getColumnLabel(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getColumnName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getColumnName(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getColumnType(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mData.getColumnType(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getColumnTypeName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getColumnTypeName(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getPrecision(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mData.getPrecision(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getScale(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mData.getScale(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getSchemaName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getSchemaName(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory getTableName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mData.getTableName(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isAutoIncrement(Environment env, Memory... args) {
        try {
            return mData.isAutoIncrement(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isCaseSensitive(Environment env, Memory... args) {
        try {
            return mData.isCaseSensitive(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isCurrency(Environment env, Memory... args) {
        try {
            return mData.isCurrency(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isDefinitelyWritable(Environment env, Memory... args) {
        try {
            return mData.isDefinitelyWritable(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isNullable(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mData.isNullable(args[0].toInteger()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isReadOnly(Environment env, Memory... args) {
        try {
            return mData.isReadOnly(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isSearchable(Environment env, Memory... args) {
        try {
            return mData.isSearchable(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isSigned(Environment env, Memory... args) {
        try {
            return mData.isSigned(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg("column")})
    public Memory isWritable(Environment env, Memory... args) {
        try {
            return mData.isWritable(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }
}

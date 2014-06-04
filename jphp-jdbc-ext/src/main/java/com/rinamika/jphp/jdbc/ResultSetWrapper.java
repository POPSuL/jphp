package com.rinamika.jphp.jdbc;

import php.runtime.Memory;
import php.runtime.common.HintType;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.DoubleMemory;
import php.runtime.memory.LongMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static php.runtime.Memory.*;
import static php.runtime.annotation.Reflection.*;
import static php.runtime.common.HintType.*;

@Name("java\\sql\\ResultSet")
public class ResultSetWrapper extends BaseObject {

    public static final int CONCUR_READ_ONLY = ResultSet.CONCUR_READ_ONLY;
    public static final int CONCUR_UPDATABLE = ResultSet.CONCUR_UPDATABLE;

    public static final int HOLD_CURSORS_OVER_COMMIT = ResultSet.HOLD_CURSORS_OVER_COMMIT;
    public static final int CLOSE_CURSORS_AT_COMMIT = ResultSet.CLOSE_CURSORS_AT_COMMIT;

    public static final int FETCH_FORWARD = ResultSet.FETCH_FORWARD;
    public static final int FETCH_REVERSE = ResultSet.FETCH_REVERSE;
    public static final int FETCH_UNKNOWN = ResultSet.FETCH_UNKNOWN;

    public static final int TYPE_FORWARD_ONLY = ResultSet.TYPE_FORWARD_ONLY;
    public static final int TYPE_SCROLL_INSENSITIVE = ResultSet.TYPE_SCROLL_INSENSITIVE;
    public static final int TYPE_SCROLL_SENSITIVE = ResultSet.TYPE_SCROLL_SENSITIVE;

    private ResultSet mResultSet;

    public ResultSetWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public ResultSetWrapper(Environment env, ResultSet rs) {
        super(env);
        mResultSet = rs;
    }

    public ResultSet getResultSet() {
        return mResultSet;
    }

    @Signature({@Arg(value = "row", type = INT)})
    public Memory absolute(Environment env, Memory... args) {
        try {
            return mResultSet.absolute(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory afterLast(Environment env, Memory... args) {
        try {
            mResultSet.afterLast();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory beforeFirst(Environment env, Memory... args) {
        try {
            mResultSet.beforeFirst();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory cancelRowUpdates(Environment env, Memory... args) {
        try {
            mResultSet.cancelRowUpdates();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory close(Environment env, Memory... args) {
        try {
            mResultSet.close();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory deleteRow(Environment env, Memory... args) {
        try {
            mResultSet.deleteRow();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "columnLabel", type = HintType.STRING)})
    public Memory findColumn(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.findColumn(args[0].toString()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory first(Environment env, Memory... args) {
        try {
            return mResultSet.first() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getBoolean(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return mResultSet.getBoolean(args[0].toInteger()) ? TRUE : FALSE;
            } else {
                return mResultSet.getBoolean(args[0].toString()) ? TRUE : FALSE;
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getConcurrency(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.getConcurrency());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getCursorName(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mResultSet.getCursorName());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getDouble(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return DoubleMemory.valueOf(mResultSet.getDouble(args[0].toInteger()));
            } else {
                return DoubleMemory.valueOf(mResultSet.getDouble(args[0].toString()));
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getFetchDirection(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.getFetchDirection());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getFetchSize(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.getFetchSize());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getFloat(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return DoubleMemory.valueOf(mResultSet.getFloat(args[0].toInteger()));
            } else {
                return DoubleMemory.valueOf(mResultSet.getFloat(args[0].toString()));
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getHoldability(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.getHoldability());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getInt(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return LongMemory.valueOf(mResultSet.getInt(args[0].toInteger()));
            } else {
                return LongMemory.valueOf(mResultSet.getInt(args[0].toString()));
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getLong(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return LongMemory.valueOf(mResultSet.getLong(args[0].toInteger()));
            } else {
                return LongMemory.valueOf(mResultSet.getLong(args[0].toString()));
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getMetaData(Environment env, Memory... args) {
        try {
            ResultSetMetaData rsmd = mResultSet.getMetaData();
            return new ResultSetMetaDataWrapper(env, rsmd).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getRow(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.getRow());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getShort(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return LongMemory.valueOf(mResultSet.getShort(args[0].toInteger()));
            } else {
                return LongMemory.valueOf(mResultSet.getShort(args[0].toString()));
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getStatement(Environment env, Memory... args) {
        try {
            return new StatementWrapper(env, mResultSet.getStatement()).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory getString(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                return StringMemory.valueOf(mResultSet.getString(args[0].toInteger()));
            } else {
                return StringMemory.valueOf(mResultSet.getString(args[0].toString()));
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getType(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mResultSet.getType());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory insertRow(Environment env, Memory... args) {
        try {
            mResultSet.insertRow();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory isAfterLast(Environment env, Memory... args) {
        try {
            return mResultSet.isAfterLast() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory isBeforeFirst(Environment env, Memory... args) {
        try {
            return mResultSet.isBeforeFirst() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory isClosed(Environment env, Memory... args) {
        try {
            return mResultSet.isClosed() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory isFirst(Environment env, Memory... args) {
        try {
            return mResultSet.isFirst() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory isLast(Environment env, Memory... args) {
        try {
            return mResultSet.isLast() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory last(Environment env, Memory... args) {
        try {
            return mResultSet.last() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory moveToCurrentRow(Environment env, Memory... args) {
        try {
            mResultSet.moveToCurrentRow();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory moveToInsertRow(Environment env, Memory... args) {
        try {
            mResultSet.moveToInsertRow();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory next(Environment env, Memory... args) {
        try {
            return mResultSet.next() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory previous(Environment env, Memory... args) {
        try {
            return mResultSet.previous() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory refreshRow(Environment env, Memory... args) {
        try {
            mResultSet.refreshRow();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "rows", type = INT)})
    public Memory relative(Environment env, Memory... args) {
        try {
            return mResultSet.relative(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory rowDeleted(Environment env, Memory... args) {
        try {
            return mResultSet.rowDeleted() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory rowInserted(Environment env, Memory... args) {
        try {
            return mResultSet.rowInserted() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory rowUpdated(Environment env, Memory... args) {
        try {
            return mResultSet.rowUpdated() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "direction", type = INT)})
    public Memory setFetchDirection(Environment env, Memory... args) {
        try {
            mResultSet.setFetchDirection(args[0].toInteger());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "rows", type = INT)})
    public Memory setFetchSize(Environment env, Memory... args) {
        try {
            mResultSet.setFetchSize(args[0].toInteger());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = BOOLEAN)})
    public Memory updateBoolean(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateBoolean(args[0].toInteger(), args[1].toBoolean());
            } else {
                mResultSet.updateBoolean(args[0].toString(), args[1].toBoolean());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = DOUBLE)})
    public Memory updateDouble(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateDouble(args[0].toInteger(), args[1].toDouble());
            } else {
                mResultSet.updateDouble(args[0].toString(), args[1].toDouble());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = DOUBLE)})
    public Memory updateFloat(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateFloat(args[0].toInteger(), args[1].toFloat());
            } else {
                mResultSet.updateFloat(args[0].toString(), args[1].toFloat());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = INT)})
    public Memory updateInt(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateInt(args[0].toInteger(), args[1].toInteger());
            } else {
                mResultSet.updateInt(args[0].toString(), args[1].toInteger());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = INT)})
    public Memory updateLong(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateLong(args[0].toInteger(), args[1].toInteger());
            } else {
                mResultSet.updateLong(args[0].toString(), args[1].toInteger());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column")})
    public Memory updateNull(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateNull(args[0].toInteger());
            } else {
                mResultSet.updateNull(args[0].toString());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory updateRow(Environment env, Memory... args) {
        try {
            mResultSet.updateRow();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = INT)})
    public Memory updateShort(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateShort(args[0].toInteger(), (short) args[1].toInteger());
            } else {
                mResultSet.updateShort(args[0].toString(), (short) args[1].toInteger());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "column"), @Arg(value = "x", type = STRING)})
    public Memory updateString(Environment env, Memory... args) {
        try {
            if (args[0].isNumber()) {
                mResultSet.updateString(args[0].toInteger(), args[1].toString());
            } else {
                mResultSet.updateString(args[0].toString(), args[1].toString());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory wasNull(Environment env, Memory... args) {
        try {
            return mResultSet.wasNull() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

}

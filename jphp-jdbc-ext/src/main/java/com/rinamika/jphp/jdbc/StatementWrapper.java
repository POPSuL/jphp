package com.rinamika.jphp.jdbc;

import php.runtime.Memory;
import php.runtime.annotation.Reflection;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.ArrayMemory;
import php.runtime.memory.LongMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static php.runtime.Memory.*;
import static php.runtime.annotation.Reflection.*;
import static php.runtime.common.HintType.INT;
import static php.runtime.common.HintType.STRING;

@Reflection.Name("java\\sql\\Statement")
public class StatementWrapper extends BaseObject {

    public static int CLOSE_CURRENT_RESULT = Statement.CLOSE_CURRENT_RESULT;
    public static int KEEP_CURRENT_RESULT = Statement.KEEP_CURRENT_RESULT;
    public static int SUCCESS_NO_INFO = Statement.SUCCESS_NO_INFO;
    public static int EXECUTE_FAILED = Statement.EXECUTE_FAILED;
    public static int RETURN_GENERATED_KEYS = Statement.RETURN_GENERATED_KEYS;
    public static int NO_GENERATED_KEYS = Statement.NO_GENERATED_KEYS;

    private Statement mStatement;

    public StatementWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public StatementWrapper(Environment env, Statement stmt) {
        super(env);
        mStatement = stmt;
    }

    public Statement getStatement() {
        return mStatement;
    }

    @Signature({@Arg(value = "sql", type = STRING)})
    public Memory addBatch(Environment env, Memory... args) {
        try {
            mStatement.addBatch(args[0].toString());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory cancel(Environment env, Memory... args) {
        try {
            mStatement.cancel();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory clearBatch(Environment env, Memory... args) {
        try {
            mStatement.clearBatch();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory clearWarnings(Environment env, Memory... args) {
        try {
            mStatement.clearWarnings();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory close(Environment env, Memory... args) {
        try {
            mStatement.close();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory closeOnCompletion(Environment env, Memory... args) {
        try {
            mStatement.closeOnCompletion();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory execute(Environment env, Memory... args) {
        try {
            mStatement.execute(args[0].toString());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory executeBatch(Environment env, Memory... args) {
        try {
            int updated[] = mStatement.executeBatch();
            ArrayMemory mem = new ArrayMemory();
            for (int c : updated) {
                mem.add(c);
            }
            return mem;

        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "sql", type = STRING)})
    public Memory executeQuery(Environment env, Memory... args) {
        try {
            ResultSet rs = mStatement.executeQuery(args[0].toString());
            return new ResultSetWrapper(env, rs).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "sql", type = STRING)})
    public Memory executeUpdate(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.executeUpdate(args[0].toString()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getConnection(Environment env, Memory... args) {
        try {
            Connection conn = mStatement.getConnection();
            return new ConnectionWrapper(env, conn).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getFetchDirection(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getFetchDirection());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getFetchSize(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getFetchSize());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getGeneratedKeys(Environment env, Memory... args) {
        try {
            ResultSet rs = mStatement.getGeneratedKeys();
            return new ResultSetWrapper(env, rs).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getMaxFieldSize(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getMaxFieldSize());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getMaxRows(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getMaxRows());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature({@Arg(value = "current", type = INT, optional = @Optional("NULL"))})
    public Memory getMoreResults(Environment env, Memory... args) {
        try {
            if (!args[0].isNull()) {
                return mStatement.getMoreResults(args[0].toInteger()) ? TRUE : FALSE;
            }
            return mStatement.getMoreResults() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getQueryTimeout(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getQueryTimeout());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getResultSet(Environment env, Memory... args) {
        try {
            ResultSet rs = mStatement.getResultSet();
            return new ResultSetWrapper(env, rs).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getResultSetConcurrency(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getResultSetConcurrency());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getResultSetHoldability(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getResultSetHoldability());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getResultSetType(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getResultSetType());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getUpdateCount(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mStatement.getUpdateCount());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory isClosed(Environment env, Memory... args) {
        try {
            return mStatement.isClosed() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory isCloseOnCompletion(Environment env, Memory... args) {
        try {
            return mStatement.isCloseOnCompletion() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }

    @Signature
    public Memory isPoolable(Environment env, Memory... args) {
        try {
            return mStatement.isPoolable() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return Memory.NULL;
    }
}

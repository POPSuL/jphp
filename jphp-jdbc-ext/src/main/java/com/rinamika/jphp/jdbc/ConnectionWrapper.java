package com.rinamika.jphp.jdbc;

import com.rinamika.jphp.util.MemoryUtil;
import php.runtime.Memory;
import php.runtime.common.HintType;
import php.runtime.env.Environment;
import php.runtime.lang.BaseObject;
import php.runtime.memory.ArrayMemory;
import php.runtime.memory.LongMemory;
import php.runtime.memory.ObjectMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Properties;

import static php.runtime.Memory.*;
import static php.runtime.annotation.Reflection.*;

@Name("java\\sql\\Connection")
public class ConnectionWrapper extends BaseObject {

    public static final int TRANSACTION_NONE = Connection.TRANSACTION_NONE;
    public static final int TRANSACTION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
    public static final int TRANSACTION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;
    public static final int TRANSACTION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;
    public static final int TRANSACTION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;

    private Connection mConnection;

    public ConnectionWrapper(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public ConnectionWrapper(Environment env, Connection conn) {
        super(env);
        mConnection = conn;
    }

    @Signature
    public Memory clearWarnings(Environment env, Memory... args) {
        try {
            mConnection.clearWarnings();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory close(Environment env, Memory... args) {
        try {
            mConnection.close();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory commit(Environment env, Memory... args) {
        try {
            mConnection.close();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({
            @Arg(value = "resultSetType", optional = @Optional("NULL")),
            @Arg(value = "resultSetConcurrency", optional = @Optional("NULL")),
            @Arg(value = "resultSetHoldability", optional = @Optional("NULL"))})
    public Memory createStatement(Environment env, Memory... args) {
        try {
            Statement stmt;
            if (!args[0].isNull() && !args[1].isNull() && args[2].isNull()) {
                stmt = mConnection.createStatement(args[0].toInteger(), args[1].toInteger());
            } else if (!args[0].isNull() && !args[1].isNull() && !args[2].isNull()) {
                stmt = mConnection.createStatement(args[0].toInteger(), args[1].toInteger(), args[2].toInteger());
            } else {
                stmt = mConnection.createStatement();
            }
            return new StatementWrapper(env, stmt).toMemory();
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getAutoCommit(Environment env, Memory... args) {
        try {
            return mConnection.getAutoCommit() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory getCatalog(Environment env, Memory... args) {
        try {
            String catalog = mConnection.getCatalog();
            if (catalog == null) {
                return NULL;
            }
            return StringMemory.valueOf(catalog);
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
            return NULL;
        }
    }

    @Signature({@Arg(value = "name", optional = @Optional("NULL"))})
    public Memory getClientInfo(Environment env, Memory... args) {
        try {
            if (!args[0].isNull()) {
                String name = args[0].toString();
                String value = mConnection.getClientInfo(name);
                if (value == null) {
                    return NULL;
                }
                return StringMemory.valueOf(value);
            } else {
                Properties props = mConnection.getClientInfo();
                return new ArrayMemory(props);
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
            return NULL;
        }
    }

    @Signature
    public Memory getHoldability(Environment env, Memory... args) {
        try {
            int holdability = mConnection.getHoldability();
            return LongMemory.valueOf(holdability);
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
            return NULL;
        }
    }

    @Signature
    public Memory getNetworkTimeout(Environment env, Memory... args) {
        try {
            int timeout = mConnection.getNetworkTimeout();
            return LongMemory.valueOf(timeout);
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
            return NULL;
        }
    }

    @Signature
    public Memory getSchema(Environment env, Memory... args) {
        try {
            String schema = mConnection.getSchema();
            if (schema == null) {
                return NULL;
            }
            return StringMemory.valueOf(schema);
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
            return NULL;
        }
    }

    @Signature
    public Memory getTransactionIsolation(Environment env, Memory... args) {
        try {
            return LongMemory.valueOf(mConnection.getTransactionIsolation());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory isClosed(Environment env, Memory... args) {
        try {
            return mConnection.isClosed() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory isReadOnly(Environment env, Memory... args) {
        try {
            return mConnection.isReadOnly() ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "timeout", type = HintType.INT)})
    public Memory isValid(Environment env, Memory... args) {
        try {
            return mConnection.isValid(args[0].toInteger()) ? TRUE : FALSE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "sql", type = HintType.STRING)})
    public Memory nativeSQL(Environment env, Memory... args) {
        try {
            return StringMemory.valueOf(mConnection.nativeSQL(args[0].toString()));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "savepoint", typeClass = "java\\sql\\Savepoint", optional = @Optional("NULL"))})
    public Memory rollback(Environment env, Memory... args) {
        try {
            if (args[0].isNull()) {
                mConnection.rollback();
            } else {
                SavepointWrapper sp = (SavepointWrapper) ((ObjectMemory) args[0]).value;
                mConnection.rollback(sp.getSavepoint());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "autoCommit", type = HintType.BOOLEAN)})
    public Memory setAutoCommit(Environment env, Memory... args) {
        try {
            mConnection.setAutoCommit(args[0].toBoolean());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "catalog", type = HintType.STRING)})
    public Memory setCatalog(Environment env, Memory... args) {
        try {
            mConnection.setCatalog(args[0].toString());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature
    public Memory setClientInfo(Environment env, Memory... args) {
        try {
            if (args[0].isArray()) {
                ArrayMemory arr = (ArrayMemory) args[0];
                mConnection.setClientInfo(MemoryUtil.toProperties(arr));
            } else {
                mConnection.setClientInfo(args[0].toString(), args[1].toString());
            }
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "holdability", type = HintType.INT)})
    public Memory setHoldability(Environment env, Memory... args) {
        try {
            mConnection.setHoldability(args[0].toInteger());
            return TRUE;
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
            return NULL;
        }
    }

    @Signature({@Arg(value = "readOnly", type = HintType.BOOLEAN)})
    public Memory setReadOnly(Environment env, Memory... args) {
        try {
            mConnection.setReadOnly(args[0].toBoolean());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "name", type = HintType.STRING, optional = @Optional("NULL"))})
    public Memory setSavepoint(Environment env, Memory... args) {
        try {
            Savepoint sp;
            if (args[0].isNull()) {
                sp = mConnection.setSavepoint();
            } else {
                sp = mConnection.setSavepoint(args[0].toString());
            }
            return new ObjectMemory(new SavepointWrapper(env, sp));
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "schema", type = HintType.STRING)})
    public Memory setSchema(Environment env, Memory... args) {
        try {
            mConnection.setSchema(args[0].toString());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }

    @Signature({@Arg(value = "level", type = HintType.INT)})
    public Memory setTransactionIsolation(Environment env, Memory... args) {
        try {
            mConnection.setTransactionIsolation(args[0].toInteger());
        } catch (SQLException e) {
            DriverManagerWrapper.exception(env, e.getMessage());
        }
        return NULL;
    }
}

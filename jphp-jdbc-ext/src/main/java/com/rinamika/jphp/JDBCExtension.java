package com.rinamika.jphp;

import com.rinamika.jphp.jdbc.*;
import php.runtime.env.CompileScope;
import php.runtime.ext.support.Extension;

import java.sql.SQLException;

public class JDBCExtension extends Extension {

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getName() {
        return "JDBC Layer";
    }

    @Override
    public void onRegister(CompileScope scope) {
        super.onRegister(scope);
        registerNativeClass(scope, ConnectionWrapper.class);
        registerNativeClass(scope, DriverManagerWrapper.class);
        registerNativeClass(scope, DriverWrapper.class);
        registerNativeClass(scope, ResultSetMetaDataWrapper.class);
        registerNativeClass(scope, ResultSetWrapper.class);
        registerNativeClass(scope, SavepointWrapper.class);
        registerNativeClass(scope, StatementWrapper.class);
        registerJavaException(scope, SQLExceptionWrapper.class, SQLException.class);
    }
}

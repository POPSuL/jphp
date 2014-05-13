package org.develnext.jphp.core.compiler.jvm.statement;

import php.runtime.common.Modifier;
import org.develnext.jphp.core.compiler.jvm.JvmCompiler;
import php.runtime.reflection.*;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.token.expr.value.NameToken;
import org.develnext.jphp.core.tokenizer.token.stmt.ClassStmtToken;
import org.develnext.jphp.core.tokenizer.token.stmt.FunctionStmtToken;
import org.develnext.jphp.core.tokenizer.token.stmt.MethodStmtToken;
import org.develnext.jphp.core.tokenizer.token.stmt.NamespaceStmtToken;

import java.util.Arrays;


public class FunctionStmtCompiler extends StmtCompiler<FunctionEntity> {

    protected FunctionStmtToken statement;

    public FunctionStmtCompiler(JvmCompiler compiler, FunctionStmtToken statement) {
        super(compiler);
        this.statement = statement;
    }

    @Override
    public FunctionEntity compile() {
        //
        ModuleEntity module = compiler.getModule();
        FunctionEntity entity = new FunctionEntity(compiler.getContext());
        entity.setModule(module);
        entity.setName(statement.getFulledName());
        entity.setReturnReference(statement.isReturnReference());
        entity.setReturnType(statement.getReturnTypehint());
        entity.setInternalName(compiler.getModule().getInternalName() + "_func" + statement.getId());
        entity.setTrace(statement.toTraceInfo(compiler.getContext()));

        if (statement.getDocComment() != null)
            entity.setDocComment(new DocumentComment(statement.getDocComment().getComment()));

        NamespaceStmtToken namespace = NamespaceStmtToken.getDefault();
        ClassStmtToken token = new ClassStmtToken(statement.getMeta());
        token.setFinal(true);
        token.setNamespace(namespace);
        token.setName(new NameToken(TokenMeta.of(entity.getInternalName())));

        MethodStmtToken methodToken = new MethodStmtToken(statement);
        methodToken.setClazz(token);
        methodToken.setFinal(true);
        methodToken.setStatic(true);
        methodToken.setReturnReference(entity.isReturnReference());
        methodToken.setReturnType(entity.getReturnType());
        methodToken.setModifier(Modifier.PUBLIC);
        methodToken.setName(new NameToken(TokenMeta.of("__invoke")));
        token.setMethods(Arrays.asList(methodToken));

        ClassStmtCompiler classStmtCompiler = new ClassStmtCompiler(compiler, token);
        classStmtCompiler.setSystem(true);
        classStmtCompiler.setFunctionName(entity.getName());
        ClassEntity clazzEntity = classStmtCompiler.compile();
        entity.setData(clazzEntity.getData());

        MethodEntity methodEntity = clazzEntity.findMethod("__invoke");
        entity.setParameters(methodEntity.parameters);
        entity.setEmpty(methodEntity.isEmpty());
        entity.setUsesStackTrace(methodEntity.isUsesStackTrace());
        entity.setImmutable(methodEntity.isImmutable());
        entity.setResult(methodEntity.getResult());

        return entity;
    }
}

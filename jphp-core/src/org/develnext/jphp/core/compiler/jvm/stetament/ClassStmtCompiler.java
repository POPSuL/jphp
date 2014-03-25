package org.develnext.jphp.core.compiler.jvm.stetament;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import php.runtime.common.Messages;
import org.develnext.jphp.core.compiler.jvm.Constants;
import org.develnext.jphp.core.compiler.jvm.JvmCompiler;
import org.develnext.jphp.core.compiler.jvm.misc.LocalVariable;
import org.develnext.jphp.core.compiler.jvm.node.ClassNodeImpl;
import org.develnext.jphp.core.compiler.jvm.node.MethodNodeImpl;
import php.runtime.exceptions.FatalException;
import php.runtime.exceptions.support.ErrorType;
import php.runtime.env.Environment;
import php.runtime.env.TraceInfo;
import php.runtime.lang.BaseObject;
import php.runtime.Memory;
import php.runtime.reflection.ClassEntity;
import php.runtime.reflection.ConstantEntity;
import php.runtime.reflection.MethodEntity;
import php.runtime.reflection.PropertyEntity;
import org.develnext.jphp.core.tokenizer.token.Token;
import org.develnext.jphp.core.tokenizer.token.expr.ValueExprToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.FulledNameToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.NameToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.SelfExprToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.StaticAccessExprToken;
import org.develnext.jphp.core.tokenizer.token.stmt.ClassStmtToken;
import org.develnext.jphp.core.tokenizer.token.stmt.ClassVarStmtToken;
import org.develnext.jphp.core.tokenizer.token.stmt.ConstStmtToken;
import org.develnext.jphp.core.tokenizer.token.stmt.MethodStmtToken;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class ClassStmtCompiler extends StmtCompiler<ClassEntity> {
    protected ClassWriter cw;
    public final ClassNode node;
    public final ClassStmtToken statement;
    public final List<TraceInfo> traceList = new ArrayList<TraceInfo>();
    private boolean external = false;
    private boolean isSystem = false;
    private boolean isInterfaceCheck = true;
    private String functionName = "";

    private boolean initDynamicExists = false;

    protected List<ConstStmtToken.Item> dynamicConstants = new ArrayList<ConstStmtToken.Item>();
    protected List<ClassVarStmtToken> dynamicProperties = new ArrayList<ClassVarStmtToken>();

    public ClassStmtCompiler(JvmCompiler compiler, ClassStmtToken statement) {
        super(compiler);
        this.statement = statement;
        this.node = new ClassNodeImpl();
    }

    public boolean isInitDynamicExists() {
        return initDynamicExists;
    }

    public boolean isClosure(){
        return functionName == null;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public void setInterfaceCheck(boolean check){
        isInterfaceCheck = check;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    int addTraceInfo(int line, int position){
        traceList.add(new TraceInfo(compiler.getContext(), line, 0, position, 0));
        return traceList.size() - 1;
    }

    int addTraceInfo(Token token){
        traceList.add(token.toTraceInfo(compiler.getContext()));
        return traceList.size() - 1;
    }

    @SuppressWarnings("unchecked")
    protected void writeDestructor(){
        if (entity.methodDestruct != null){
            MethodNode destructor = new MethodNodeImpl();
            destructor.name = "finalize";
            destructor.access = ACC_PUBLIC;
            destructor.desc = Type.getMethodDescriptor(Type.getType(void.class));

            MethodStmtCompiler methodCompiler = new MethodStmtCompiler(this, destructor);
            ExpressionStmtCompiler expressionCompiler = new ExpressionStmtCompiler(methodCompiler, null);
            methodCompiler.writeHeader();

            LabelNode end = new LabelNode();
            LabelNode l0 = writeLabel(destructor, statement.getMeta().getStartLine());
            methodCompiler.addLocalVariable("~this", l0);

            expressionCompiler.writeVarLoad("~this");
            expressionCompiler.writeSysDynamicCall(null, "isFinalized", Boolean.TYPE);
            destructor.instructions.add(new JumpInsnNode(IFEQ, end));

            // --- if (!__finalized__) {
            expressionCompiler.writeVarLoad("~this");
            expressionCompiler.writePushDup();
            expressionCompiler.writeSysDynamicCall(null, "doFinalize", void.class);

            expressionCompiler.writePushEnvFromSelf();
            expressionCompiler.writePushConstNull();

            expressionCompiler.writeSysDynamicCall(
                    null, entity.methodDestruct.getInternalName(), Memory.class, Environment.class, Memory[].class
            );
            expressionCompiler.writePopAll(1);
            // ---- }
            destructor.instructions.add(end);

            // call parent
            // WARNING!!! It's commented for better performance, super.finalize empty in JDK 1.6, 1.7, 1.8
            /*expressionCompiler.writeVarLoad("~this");
            destructor.instructions.add(new MethodInsnNode(
                    INVOKEVIRTUAL,
                    Type.getInternalName(Object.class),
                    destructor.name,
                    destructor.desc
            ));*/

            destructor.instructions.add(new InsnNode(Opcodes.RETURN));
            methodCompiler.writeFooter();
            node.methods.add(destructor);
        }
    }

    @SuppressWarnings("unchecked")
    protected void writeConstructor(){
        MethodNode constructor = new MethodNodeImpl();
        constructor.name = Constants.INIT_METHOD;
        constructor.access = ACC_PUBLIC;
        constructor.exceptions = new ArrayList();

        MethodStmtCompiler methodCompiler = new MethodStmtCompiler(this, constructor);
        ExpressionStmtCompiler expressionCompiler = new ExpressionStmtCompiler(methodCompiler, null);
        methodCompiler.writeHeader();

        LabelNode l0 = writeLabel(constructor, statement.getMeta().getStartLine());
        methodCompiler.addLocalVariable("~this", l0);

        if (isClosure()){
            constructor.desc = Type.getMethodDescriptor(
                    Type.getType(void.class),
                    Type.getType(ClassEntity.class),
                    Type.getType(Memory.class),
                    Type.getType(Memory[].class)
            );
            methodCompiler.addLocalVariable("~class", l0, ClassEntity.class);
            methodCompiler.addLocalVariable("~self", l0, Memory.class);
            methodCompiler.addLocalVariable("~uses", l0, Memory[].class);

            methodCompiler.writeHeader();
            expressionCompiler.writeVarLoad("~this");
            expressionCompiler.writeVarLoad("~class");
            expressionCompiler.writeVarLoad("~self");
            expressionCompiler.writeVarLoad("~uses");
            constructor.instructions.add(new MethodInsnNode(
                    INVOKESPECIAL,
                    node.superName,
                    Constants.INIT_METHOD,
                    constructor.desc
            ));
        } else {
            constructor.desc = Type.getMethodDescriptor(
                    Type.getType(void.class), Type.getType(Environment.class), Type.getType(ClassEntity.class)
            );
            methodCompiler.addLocalVariable("~env", l0, Environment.class);
            methodCompiler.addLocalVariable("~class", l0, String.class);

            expressionCompiler.writeVarLoad("~this");
            expressionCompiler.writeVarLoad("~env");
            expressionCompiler.writeVarLoad("~class");
            constructor.instructions.add(new MethodInsnNode(
                    INVOKESPECIAL,
                    node.superName,
                    Constants.INIT_METHOD,
                    constructor.desc
            ));

            // PROPERTIES
            for(ClassVarStmtToken property : statement.getProperties()){
                ExpressionStmtCompiler expressionStmtCompiler = new ExpressionStmtCompiler(methodCompiler, null);
                Memory value = Memory.NULL;
                if (property.getValue() != null)
                    value = expressionStmtCompiler.writeExpression(property.getValue(), true, true, false);

                PropertyEntity prop = new PropertyEntity(compiler.getContext());
                prop.setName(property.getVariable().getName());
                prop.setModifier(property.getModifier());
                prop.setStatic(property.isStatic());
                prop.setDefaultValue(value);
                prop.setDefault(property.getValue() != null);
                prop.setTrace(property.toTraceInfo(compiler.getContext()));

                ClassEntity.PropertyResult result = entity.addProperty(prop);
                result.check(compiler.getEnvironment());

                if (value == null && property.getValue() != null) {
                    if (property.getValue().isSingle() && ValueExprToken.isConstable(property.getValue().getSingle(), true))
                        dynamicProperties.add(property);
                    else
                        compiler.getEnvironment().error(
                                property.getVariable().toTraceInfo(compiler.getContext()),
                                ErrorType.E_COMPILE_ERROR,
                                Messages.ERR_EXPECTED_CONST_VALUE.fetch(entity.getName() + "::$" + property.getVariable().getName())
                        );
                }
            }
        }

        methodCompiler.writeFooter();
        constructor.instructions.add(new InsnNode(RETURN));
        node.methods.add(constructor);
    }

    protected void writeConstant(ConstStmtToken constant){
        MethodStmtCompiler methodStmtCompiler = new MethodStmtCompiler(this, (MethodStmtToken)null);
        ExpressionStmtCompiler expressionStmtCompiler = new ExpressionStmtCompiler(methodStmtCompiler, null);

        for(ConstStmtToken.Item el : constant.items){
            Memory value = expressionStmtCompiler.writeExpression(el.value, true, true, false);
            ConstantEntity constantEntity = new ConstantEntity(el.getFulledName(), value, true);
            constantEntity.setTrace(el.name.toTraceInfo(compiler.getContext()));

            if (value != null && !value.isArray()) {
                ConstantEntity c = entity.findConstant(el.getFulledName());
                if (c != null && c.getClazz().getId() == entity.getId()){
                    compiler.getEnvironment().error(
                            constant.toTraceInfo(compiler.getContext()),
                            ErrorType.E_ERROR,
                            Messages.ERR_CANNOT_REDEFINE_CLASS_CONSTANT,
                            entity.getName() + "::" + el.getFulledName()
                    );
                    return;
                }
                entity.addConstant(constantEntity);
            } else {
                if (ValueExprToken.isConstable(el.value.getSingle(), false)){
                    dynamicConstants.add(el);
                    entity.addConstant(constantEntity);
                } else
                    compiler.getEnvironment().error(
                        constant.toTraceInfo(compiler.getContext()),
                        Messages.ERR_EXPECTED_CONST_VALUE.fetch(entity.getName() + "::" + el.getFulledName())
                    );
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void writeSystemInfo(){
        node.fields.add(new FieldNode(
                ACC_PROTECTED + ACC_FINAL + ACC_STATIC, "$FN",
                Type.getDescriptor(String.class),
                null,
                compiler.getSourceFile()
        ));

        node.fields.add(new FieldNode(
                ACC_PROTECTED + ACC_STATIC, "$TRC",
                Type.getDescriptor(TraceInfo[].class),
                null,
                null
        ));

        if (functionName != null){
            node.fields.add(new FieldNode(
                    ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "$CL",
                    Type.getDescriptor(String.class),
                    null,
                    !functionName.isEmpty() ? functionName : entity.getName()
            ));
        }
    }

    @SuppressWarnings("unchecked")
    protected void writeInitEnvironment(){
        if (!dynamicConstants.isEmpty() || !dynamicProperties.isEmpty()){
            initDynamicExists = true;
            MethodNode node = new MethodNodeImpl();
            node.access = ACC_STATIC + ACC_PUBLIC;
            node.name = "__$initEnvironment";
            node.desc = Type.getMethodDescriptor(
                Type.getType(void.class), Type.getType(Environment.class)
            );

            MethodStmtCompiler methodCompiler = new MethodStmtCompiler(this, node);
            ExpressionStmtCompiler expressionCompiler = new ExpressionStmtCompiler(methodCompiler, null);
            methodCompiler.writeHeader();

            LabelNode l0 = expressionCompiler.makeLabel();
            methodCompiler.addLocalVariable("~env", l0, Environment.class);

            LocalVariable l_class = methodCompiler.addLocalVariable("~class", l0, ClassEntity.class);

            expressionCompiler.writePushEnv();
            expressionCompiler.writePushConstString(entity.getName());
            expressionCompiler.writePushConstString(entity.getLowerName());
            expressionCompiler.writePushConstBoolean(true);
            expressionCompiler.writeSysDynamicCall(
                Environment.class, "fetchClass", ClassEntity.class, String.class, String.class, Boolean.TYPE
            );
            expressionCompiler.writeVarStore(l_class, false, false);

            // corrects defination of constants
            final List<ConstStmtToken.Item> first = new ArrayList<ConstStmtToken.Item>();
            final Set<String> usedNames = new HashSet<String>();
            final List<ConstStmtToken.Item> other = new ArrayList<ConstStmtToken.Item>();
            for (ConstStmtToken.Item el : dynamicConstants){
                Token tk = el.value.getSingle();
                if (tk instanceof StaticAccessExprToken){
                    StaticAccessExprToken access = (StaticAccessExprToken)tk;
                    boolean self = false;
                    if (access.getClazz() instanceof SelfExprToken)
                        self = true;
                    else if (access.getClazz() instanceof FulledNameToken
                            && ((FulledNameToken) access.getClazz()).getName().equalsIgnoreCase(entity.getName())){
                        self = true;
                    }
                    if (self) {
                        String name = ((NameToken)access.getField()).getName();
                        if (usedNames.contains(el.getFulledName()))
                            first.add(0, el);
                        else
                            first.add(el);
                        usedNames.add(name);
                        continue;
                    }
                }

                if (usedNames.contains(el.getFulledName()))
                    first.add(0, el);
                else
                    other.add(el);
            }

            other.addAll(0, first);

            for(ConstStmtToken.Item el : other){
                expressionCompiler.writeVarLoad(l_class);
                expressionCompiler.writePushEnv();
                expressionCompiler.writePushConstString(el.getFulledName());
                expressionCompiler.writeExpression(el.value, true, false, true);
                expressionCompiler.writePopBoxing(true);

                expressionCompiler.writeSysDynamicCall(
                    ClassEntity.class, "addDynamicConstant", void.class,
                    Environment.class, String.class, Memory.class
                );
            }

            for (ClassVarStmtToken el : dynamicProperties){
                expressionCompiler.writeVarLoad(l_class);
                expressionCompiler.writePushEnv();
                expressionCompiler.writePushConstString(el.getVariable().getName());
                expressionCompiler.writeExpression(el.getValue(), true, false, true);
                expressionCompiler.writePopBoxing(true);

                expressionCompiler.writeSysDynamicCall(
                        ClassEntity.class,
                        el.isStatic() ? "addDynamicStaticProperty" : "addDynamicProperty",
                        void.class,
                        Environment.class, String.class, Memory.class
                );
            }

            node.instructions.add(new InsnNode(RETURN));
            methodCompiler.writeFooter();

            this.node.methods.add(node);
        }
    }

    @SuppressWarnings("unchecked")
    protected void writeInitStatic(){
        MethodNode node = new MethodNodeImpl();
        node.access = ACC_STATIC;
        node.name = Constants.STATIC_INIT_METHOD;
        node.desc = Type.getMethodDescriptor(Type.getType(void.class));

        MethodStmtCompiler methodCompiler = new MethodStmtCompiler(this, node);
        ExpressionStmtCompiler expressionCompiler = new ExpressionStmtCompiler(methodCompiler, null);
        methodCompiler.writeHeader();

        expressionCompiler.writePushSmallInt(traceList.size());
        node.instructions.add(new TypeInsnNode(ANEWARRAY, Type.getInternalName(TraceInfo.class)));
        expressionCompiler.stackPush(Memory.Type.REFERENCE);

        int i = 0;
        for(TraceInfo traceInfo : traceList){
            expressionCompiler.writePushDup();
            expressionCompiler.writePushSmallInt(i);
            expressionCompiler.writePushCreateTraceInfo(traceInfo.getStartLine(), traceInfo.getStartPosition());

            node.instructions.add(new InsnNode(AASTORE));
            expressionCompiler.stackPop();
            expressionCompiler.stackPop();
            i++;
        }
        expressionCompiler.writePutStatic("$TRC", TraceInfo[].class);

        node.instructions.add(new InsnNode(RETURN));
        methodCompiler.writeFooter();

        this.node.methods.add(node);
    }

    protected void writeInterfaceMethod(MethodEntity method){
        MethodNode node = new MethodNodeImpl();
        node.access = ACC_PUBLIC;
        node.name = method.getName();
        node.desc = Type.getMethodDescriptor(
                Type.getType(Memory.class),
                Type.getType(Environment.class),
                Type.getType(Memory[].class)
        );

        MethodStmtCompiler methodCompiler = new MethodStmtCompiler(this, node);
        ExpressionStmtCompiler expressionCompiler = new ExpressionStmtCompiler(methodCompiler, null);
        methodCompiler.writeHeader();

        LabelNode l0 = writeLabel(node, statement.getMeta().getStartLine());
        methodCompiler.addLocalVariable("~this", l0);
        methodCompiler.addLocalVariable("~env", l0);
        methodCompiler.addLocalVariable("~args", l0);

        expressionCompiler.writeVarLoad("~this");
        expressionCompiler.writeVarLoad("~env");
        expressionCompiler.writeVarLoad("~args");

        String internalName = entity.findMethod(method.getLowerName()).getInternalName();
        expressionCompiler.writeSysDynamicCall(null, internalName, Memory.class, Environment.class, Memory[].class);

        node.instructions.add(new InsnNode(ARETURN));
        methodCompiler.writeFooter();
        this.node.methods.add(node);
    }

    protected void writeInterfaceMethods(Collection<MethodEntity> methods){
        for(MethodEntity method : methods){
            writeInterfaceMethod(method);
        }
    }

    protected Set<ClassEntity> writeInterfaces(ClassEntity _interface){
        Set<ClassEntity> result = new HashSet<ClassEntity>();
        writeInterfaces(_interface, result);
        return result;
    }

    protected void writeInterfaces(ClassEntity _interface, Set<ClassEntity> used) {
        if (used.add(_interface)){
            if (_interface != null && _interface.isInternal())
                node.interfaces.add(_interface.getInternalName());

            if (_interface != null){
                for(ClassEntity el : _interface.getInterfaces().values()){
                    writeInterfaces(el, used);
                }
            }
        }
    }

    protected ClassEntity fetchClass(String name){
        ClassEntity result = compiler.getModule().findClass(name);
        if (result == null)
            result = getCompiler().getEnvironment().fetchClass(name, true);

        return result;
    }

    @SuppressWarnings("unchecked")
    protected void writeImplements() {
        if (statement.getImplement() != null){
            Environment env = compiler.getEnvironment();
            for(FulledNameToken name : statement.getImplement()){
                ClassEntity implement = fetchClass(name.getName());
                Set<ClassEntity> needWriteInterfaceMethods = new HashSet<ClassEntity>();
                if (implement == null) {
                    env.error(
                            name.toTraceInfo(compiler.getContext()),
                            Messages.ERR_INTERFACE_NOT_FOUND.fetch(name.toName())
                    );
                } else {
                    if (implement.getType() != ClassEntity.Type.INTERFACE){
                        env.error(
                                name.toTraceInfo(compiler.getContext()),
                                Messages.ERR_CANNOT_IMPLEMENT.fetch(entity.getName(), implement.getName())
                        );
                    }
                    if (!statement.isInterface())
                        needWriteInterfaceMethods = writeInterfaces(implement);
                }
                ClassEntity.ImplementsResult addResult = entity.addInterface(implement);
                addResult.check(env);

                for(ClassEntity el : needWriteInterfaceMethods){
                    if (el.isInternal()) {
                        writeInterfaceMethods(el.getMethods().values());
                    }
                }
            }
        }
    }

    protected void writeCopiedMethod(MethodEntity methodEntity, ClassEntity trait, NameToken nameToken) {
        MethodEntity origin = entity.findMethod(methodEntity.getName());
        if (origin != null){
            if (origin.getClazz() == entity) {
                if (origin.getTrait() != null) {
                    compiler.getEnvironment().error(
                            nameToken.toTraceInfo(compiler.getContext()),
                            Messages.ERR_TRAIT_METHOD_COLLISION.fetch(
                                    methodEntity.getName(), trait.getName(), origin.getTrait().getName(), entity.getName()
                            )
                    );
                }
                return;
            }
        }

        MethodEntity dup = methodEntity.duplicateForInject();
        dup.setClazz(entity);
        dup.setTrait(trait);

        MethodNode methodNode = methodEntity.getMethodNode();
        if (origin != null) {
            dup.setPrototype(origin);
            dup.setInternalName(dup.getName() + "$" + entity.nextMethodIndex());
            methodNode.name = dup.getInternalName();
        }

        ClassEntity.SignatureResult result = entity.addMethod(dup, null);
        result.check(compiler.getEnvironment());

        node.methods.add(methodNode);
    }

    protected void writeTrait(ClassEntity trait, NameToken nameToken) {
        for(MethodEntity methodEntity : trait.getMethods().values()) {
            writeCopiedMethod(methodEntity, trait, nameToken);
        }
    }

    protected void writeTraits() {
        for(NameToken one : statement.getUses()) {
            ClassEntity trait = fetchClass(one.getName());
            if (trait == null) {
                compiler.getEnvironment().error(
                        one.toTraceInfo(compiler.getContext()),
                        Messages.ERR_TRAIT_NOT_FOUND.fetch(one.getName())
                );
                return;
            }

            if (!trait.isTrait()) {
                compiler.getEnvironment().error(
                        one.toTraceInfo(compiler.getContext()),
                        Messages.ERR_CANNOT_USE_NON_TRAIT.fetch(
                                entity.getName(), one.getName()
                        )
                );
            } else
                writeTrait(trait, one);
        }
    }

    @Override
    public ClassEntity compile() {
        entity = new ClassEntity(compiler.getContext());
        entity.setId(compiler.getScope().nextClassIndex());

        entity.setFinal(statement.isFinal());
        entity.setAbstract(statement.isAbstract());
        entity.setName(statement.getFulledName());
        entity.setTrace(statement.toTraceInfo(compiler.getContext()));
        entity.setType(statement.getClassType());

        if (statement.getExtend() != null) {
            ClassEntity parent = fetchClass(statement.getExtend().getName().getName());
            if (parent == null)
                compiler.getEnvironment().error(
                        statement.getExtend().toTraceInfo(compiler.getContext()),
                        Messages.ERR_CLASS_NOT_FOUND.fetch(statement.getExtend().getName().toName())
                );

            ClassEntity.ExtendsResult result = entity.setParent(parent, false);
            result.check(compiler.getEnvironment());
        }

        if (!isSystem)
            entity.setInternalName(
                    compiler.getModule().getInternalName() + "_class" + compiler.getModule().getClasses().size()
            );

        if (compiler.getModule().findClass(entity.getLowerName()) != null
              || compiler.getEnvironment().isLoadedClass(entity.getLowerName())){
            throw new FatalException(
                    Messages.ERR_CANNOT_REDECLARE_CLASS.fetch(entity.getName()),
                    statement.getName().toTraceInfo(compiler.getContext())
            );
        }

        if (!statement.isInterface()){
            node.access = ACC_SUPER + ACC_PUBLIC;
            node.name = !isSystem ? entity.getInternalName() : statement.getFulledName(Constants.NAME_DELIMITER);
            node.superName = entity.getParent() == null
                    ? Type.getInternalName(BaseObject.class)
                    : entity.getParent().getInternalName();

            node.sourceFile = compiler.getSourceFile();

            writeSystemInfo();
            writeConstructor();
        }

        // constants
        if (statement.getConstants() != null)
            for(ConstStmtToken constant : statement.getConstants()){
                  writeConstant(constant);
        }

        if (statement.getMethods() != null){
            for (MethodStmtToken method : statement.getMethods()){
                ClassEntity.SignatureResult result = entity.addMethod(compiler.compileMethod(this, method, external), null);
                result.check(compiler.getEnvironment());
            }
        }

        ClassEntity.SignatureResult result = entity.updateParentMethods();
        if (isInterfaceCheck) {
            result.check(compiler.getEnvironment());
        }

        writeTraits();

        writeImplements();
        entity.doneDeclare();

        if (!statement.isInterface()){
            writeDestructor();

            if (entity.getType() != ClassEntity.Type.INTERFACE){
                writeInitEnvironment();
            }

            writeInitStatic();
            cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // !!! IMPORTANT use COMPUTE_FRAMES
            node.accept(cw);

            entity.setData(cw.toByteArray());
        }
        return entity;
    }

    ClassWriter getClassWriter() {
        return cw;
    }
}

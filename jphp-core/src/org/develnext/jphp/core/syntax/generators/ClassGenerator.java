package org.develnext.jphp.core.syntax.generators;

import php.runtime.common.Messages;
import php.runtime.common.Modifier;
import php.runtime.common.Separator;
import php.runtime.exceptions.ParseException;
import php.runtime.exceptions.support.ErrorType;
import org.develnext.jphp.core.syntax.SyntaxAnalyzer;
import org.develnext.jphp.core.syntax.generators.manually.SimpleExprGenerator;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.token.CommentToken;
import org.develnext.jphp.core.tokenizer.token.SemicolonToken;
import org.develnext.jphp.core.tokenizer.token.Token;
import org.develnext.jphp.core.tokenizer.token.expr.BraceExprToken;
import org.develnext.jphp.core.tokenizer.token.expr.CommaToken;
import org.develnext.jphp.core.tokenizer.token.expr.operator.AssignExprToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.FulledNameToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.NameToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.StaticExprToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.VariableExprToken;
import org.develnext.jphp.core.tokenizer.token.stmt.*;
import php.runtime.reflection.ClassEntity;

import java.util.*;

public class ClassGenerator extends Generator<ClassStmtToken> {

    @SuppressWarnings("unchecked")
    private final static Class<? extends Token>[] modifiers = new Class[]{
        PrivateStmtToken.class,
        ProtectedStmtToken.class,
        PublicStmtToken.class,
        StaticExprToken.class,
        FinalStmtToken.class,
        AbstractStmtToken.class,
        VarStmtToken.class,
        ConstStmtToken.class
    };

    public ClassGenerator(SyntaxAnalyzer analyzer) {
        super(analyzer);
    }

    protected void processName(ClassStmtToken result, ListIterator<Token> iterator){
        Token name = nextToken(iterator);
        if (name instanceof NameToken){
            result.setName((NameToken)name);
        } else
            unexpectedToken(name, TokenType.T_STRING);
    }

    protected void _processImplements(Token token, ClassStmtToken result, ListIterator<Token> iterator){
        ImplementsStmtToken implement = new ImplementsStmtToken(token.getMeta());

        Token prev = null;
        List<FulledNameToken> names = new ArrayList<FulledNameToken>();
        do {
            token = nextToken(iterator);
            if (token instanceof NameToken){
                names.add(analyzer.getRealName((NameToken)token));
            } else if (token instanceof CommaToken){
                if (!(prev instanceof NameToken))
                    unexpectedToken(token);
            } else if (isOpenedBrace(token, BraceExprToken.Kind.BLOCK)){
                iterator.previous();
                break;
            } else
                unexpectedToken(token);

            prev = token;
        } while (true);

        implement.setNames(names);
        result.setImplement(implement);
    }

    protected void processExtends(ClassStmtToken result, ListIterator<Token> iterator){
        Token token = nextToken(iterator);
        if (token instanceof ExtendsStmtToken){
            if (result.isTrait())
                unexpectedToken(token);

            if (result.isInterface()){
                _processImplements(token, result, iterator);
            } else {
                ExtendsStmtToken extend = (ExtendsStmtToken)token;
                FulledNameToken what = analyzer.generator(NameGenerator.class).getToken(nextToken(iterator), iterator);
                extend.setName(analyzer.getRealName(what));
                result.setExtend(extend);
            }
        } else
            iterator.previous();
    }

    protected void processImplements(ClassStmtToken result, ListIterator<Token> iterator){
        checkUnexpectedEnd(iterator);
        Token token = iterator.next();
        if (token instanceof ImplementsStmtToken){
            if (result.isTrait())
                unexpectedToken(token);

            if (result.isInterface())
                unexpectedToken(token, "extends");

            _processImplements(token, result, iterator);
            /*
            ImplementsStmtToken implement = new ImplementsStmtToken(token.getMeta());

            Token prev = null;
            List<FulledNameToken> names = new ArrayList<FulledNameToken>();
            do {
                token = nextToken(iterator);
                if (token instanceof NameToken){
                    names.add(analyzer.getRealName((NameToken)token));
                } else if (token instanceof CommaToken){
                    if (!(prev instanceof NameToken))
                        unexpectedToken(token);
                } else if (isOpenedBrace(token, BraceExprToken.Kind.BLOCK)){
                    iterator.previous();
                    break;
                } else
                    unexpectedToken(token);

                prev = token;
            } while (true);

            implement.setNames(names);
            result.setImplement(implement);*/
        } else
            iterator.previous();
    }

    protected List<ClassVarStmtToken> processProperty(VariableExprToken current, List<Token> modifiers,
                                                      ListIterator<Token> iterator){
        Token next = current;
        Token prev = null;
        Set<VariableExprToken> variables = new LinkedHashSet<VariableExprToken>();
        ExprStmtToken initValue = null;

        List<ClassVarStmtToken> result = new ArrayList<ClassVarStmtToken>();

        Modifier modifier = Modifier.PUBLIC;
        boolean isStatic = false;
        for(Token token : modifiers){
            if (token instanceof PrivateStmtToken)
                modifier = Modifier.PRIVATE;
            else if (token instanceof ProtectedStmtToken)
                modifier = Modifier.PROTECTED;
            else if (token instanceof StaticExprToken)
                isStatic = true;
        }

        do {
            if (next instanceof VariableExprToken){
                if (!variables.add((VariableExprToken)next))
                    throw new ParseException(
                            Messages.ERR_IDENTIFIER_X_ALREADY_USED.fetch(next.getWord()),
                            next.toTraceInfo(analyzer.getContext())
                    );
            } else if (next instanceof CommaToken){
                if (!(prev instanceof VariableExprToken))
                    unexpectedToken(next);
            } else if (next instanceof AssignExprToken){
                if (!(prev instanceof VariableExprToken))
                    unexpectedToken(next);

                initValue = analyzer.generator(SimpleExprGenerator.class)
                        .getToken(nextToken(iterator), iterator, Separator.COMMA_OR_SEMICOLON, null);
                break;
            } else if (next instanceof SemicolonToken){
                if (!(prev instanceof VariableExprToken))
                    unexpectedToken(next);
                break;
            }

            prev = next;
            next = nextToken(iterator);
        } while (true);

        for(VariableExprToken variable : variables){
            ClassVarStmtToken classVar = new ClassVarStmtToken(variable.getMeta());
            classVar.setModifier(modifier);
            classVar.setStatic(isStatic);
            classVar.setValue(initValue);
            classVar.setVariable(variable);

            result.add(classVar);
        }
        return result;
    }

    protected void processUse(ClassStmtToken result, ListIterator<Token> iterator) {
        if (result.isInterface())
            unexpectedToken(iterator.previous());

        Token prev = null;
        List<NameToken> uses = new ArrayList<NameToken>();
        do {
            Token token = nextToken(iterator);
            if (token instanceof NameToken){
                uses.add(analyzer.getRealName((NameToken)token));
            } else if (token instanceof CommaToken){
                if (!(prev instanceof NameToken))
                    unexpectedToken(token);
            } else if (token instanceof SemicolonToken) {
                break;
            } else if (isOpenedBrace(token, BraceExprToken.Kind.BLOCK)){
                iterator.previous();
                break;
            } else
                unexpectedToken(token);

            prev = token;
        } while (true);

        result.getUses().addAll(uses);
    }

    @SuppressWarnings("unchecked")
    protected void processBody(ClassStmtToken result, ListIterator<Token> iterator){
        analyzer.setClazz(result);

        Token token = nextToken(iterator);
        if (token instanceof BraceExprToken){
            BraceExprToken brace = (BraceExprToken)token;
            if (brace.isBlockOpened()){

                List<ConstStmtToken> constants = new ArrayList<ConstStmtToken>();
                List<MethodStmtToken> methods = new ArrayList<MethodStmtToken>();
                List<ClassVarStmtToken> properties = new ArrayList<ClassVarStmtToken>();

                List<Token> modifiers = new ArrayList<Token>();
                while (iterator.hasNext()){
                    Token current = iterator.next();
                    if (current instanceof ExprStmtToken)
                        unexpectedToken(current, "expression");

                    if (current instanceof ConstStmtToken){
                        if (!modifiers.isEmpty())
                            unexpectedToken(modifiers.get(0));
                        if (result.isTrait())
                            unexpectedToken(current);

                        ConstStmtToken one = analyzer.generator(ConstGenerator.class).getToken(current, iterator);
                        one.setClazz(result);
                        constants.add(one);
                        modifiers.clear();
                    } else if (isTokenClass(current, ClassGenerator.modifiers)){
                        for(Token modifier : modifiers){
                            if (modifier.getType() == current.getType())
                                unexpectedToken(current);
                        }
                        modifiers.add(current);
                    } else if (current instanceof VariableExprToken) {
                        if (result.isInterface()) {
                            analyzer.getEnvironment().error(
                                    result.toTraceInfo(analyzer.getContext()),
                                    ErrorType.E_ERROR,
                                    Messages.ERR_INTERFACE_MAY_NOT_INCLUDE_VARS
                            );
                        }

                        for(Token modifier : modifiers){
                            if (isTokenClass(modifier, FinalStmtToken.class, AbstractStmtToken.class))
                                unexpectedToken(modifier);
                            if (result.isTrait() && isTokenClass(modifier, StaticExprToken.class))
                                unexpectedToken(modifier);
                        }
                        properties.addAll(processProperty((VariableExprToken)current, modifiers, iterator));
                        modifiers.clear();
                    } else if (current instanceof FunctionStmtToken) {
                        FunctionStmtToken function = analyzer.generator(FunctionGenerator.class).getToken(current, iterator);
                        MethodStmtToken method = new MethodStmtToken(function);
                        method.setClazz(result);

                        for (Token modifier : modifiers) {
                            if (modifier instanceof AbstractStmtToken)
                                method.setAbstract(true);
                            else if (modifier instanceof StaticExprToken)
                                method.setStatic(true);
                            else if (modifier instanceof FinalStmtToken) {
                                method.setFinal(true);
                            } else if (modifier instanceof PublicStmtToken) {
                                if (method.getModifier() != null)
                                    unexpectedToken(modifier);

                                method.setModifier(Modifier.PUBLIC);
                            } else if (modifier instanceof PrivateStmtToken) {
                                if (method.getModifier() != null)
                                    unexpectedToken(modifier);

                                method.setModifier(Modifier.PRIVATE);
                            } else if (modifier instanceof ProtectedStmtToken) {
                                if (method.getModifier() != null)
                                    unexpectedToken(modifier);

                                method.setModifier(Modifier.PROTECTED);
                            }
                        }
                        if (method.getModifier() == null)
                            method.setModifier(Modifier.PUBLIC);

                        methods.add(method);
                        modifiers.clear();
                    } else if (current instanceof NamespaceUseStmtToken) {
                        processUse(result, iterator);
                    } else if (isClosedBrace(current, BraceExprToken.Kind.BLOCK)){
                        break;
                    } else if (current instanceof CommentToken){
                        // todo comment process
                    } else
                        unexpectedToken(current);
                }

                result.setConstants(constants);

                result.setMethods(methods);
                result.setProperties(properties);
                analyzer.setClazz(null);
                return;
            }
        }

        unexpectedToken(token, "{");
    }

    @SuppressWarnings("unchecked")
    protected ClassStmtToken processDefine(Token current, ListIterator<Token> iterator){
        if (isTokenClass(current, FinalStmtToken.class, AbstractStmtToken.class)){
            Token next = nextToken(iterator);
            if (next instanceof ClassStmtToken){
                ClassStmtToken result;
                result = (ClassStmtToken)next;
                result.setInterface(false);

                result.setAbstract(current instanceof AbstractStmtToken);
                result.setFinal(current instanceof FinalStmtToken);

                return result;
            } else if (next instanceof InterfaceStmtToken || next instanceof TraitStmtToken) {
                unexpectedToken(current);
            } else if (next instanceof AbstractStmtToken || next instanceof FinalStmtToken){
                unexpectedToken(next);
            } else {
                iterator.previous();
            }
        }

        if (current instanceof ClassStmtToken)
            return (ClassStmtToken)current;
        else if (current instanceof InterfaceStmtToken){
            ClassStmtToken result = new ClassStmtToken(current.getMeta());
            result.setInterface(true);
            return result;
        } else if (current instanceof TraitStmtToken) {
            ClassStmtToken result = new ClassStmtToken(current.getMeta());
            result.setClassType(ClassEntity.Type.TRAIT);
            return result;
        }

        return null;
    }

    @Override
    public ClassStmtToken getToken(Token current, ListIterator<Token> iterator) {
        ClassStmtToken result = processDefine(current, iterator);

        if (result != null) {
            if (analyzer.getClazz() != null)
                unexpectedToken(current);

            analyzer.setClazz(result);
            result.setNamespace(analyzer.getNamespace());

            processName(result, iterator);
            processExtends(result, iterator);
            processImplements(result, iterator);
            processBody(result, iterator);
            analyzer.setClazz(null);
        }

        return result;
    }
}

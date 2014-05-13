package org.develnext.jphp.core.compiler.jvm;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import php.runtime.Memory;
import php.runtime.common.HintType;
import php.runtime.memory.*;
import php.runtime.reflection.ParameterEntity;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReturnTypeHintingTest extends JvmCompilerCase {

    @Test
    public void testBasic(){
        check("returntypehinting/hint_basic.php", false);
    }

    @Test
    public void testClasses(){
        check("returntypehinting/hint_class.php", false);
    }

    @Test
    public void testMethods(){
        check("returntypehinting/hint_methods.php", false);
    }

    @Test
    public void testMethodsInheritance(){
        check("returntypehinting/hint_inherited_methods.php", false);
    }

    @Test
    public void testErrorArray(){
        check("returntypehinting/hint_error_array.php", true);
    }

    @Test
    public void testErrorClosure(){
        check("returntypehinting/hint_error_closure.php", true);
    }

    @Test
    public void testInvalidInheritance(){
        check("returntypehinting/hint_invalid_inheritance.php", true);
    }

}

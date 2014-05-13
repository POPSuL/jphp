--TEST--
Test class type hinting
--FILE--
<?php

namespace X {
    class A {}
}

namespace {

    class B {}

    function testA() : \X\A {
        return new \X\A();
    }

    function testB() : \B {
        return new B();
    }

    var_dump(testA() instanceof \X\A);
    var_dump(testB() instanceof \B);
}

--EXPECT--
bool(true)
bool(true)
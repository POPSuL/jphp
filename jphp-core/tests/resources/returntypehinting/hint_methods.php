--TEST--
Test class type hinting
--FILE--
<?php

class A {
    public function _array() : array {
        $a = [];
        return [];
    }

    public function _closure() : callable {
        $a = function () {
        };
        return $a;
    }

    public function _class() : \A {
        return new self();
    }
}

$a = new A();
var_dump($a->_array());
var_dump($a->_closure() instanceof \Closure);
var_dump($a->_class() instanceof \A);

--EXPECT--
array(0) {
}
bool(true)
bool(true)
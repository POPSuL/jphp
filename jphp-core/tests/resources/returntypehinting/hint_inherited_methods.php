--TEST--
Test class type hinting
--FILE--
<?php

abstract class AbstractHint {
    public abstract function hint() : array;
}

interface Hintable {
    public function hint() : array;
}

class A extends AbstractHint {
    public function hint() : array {
        $a = [];
        return $a;
    }
}

class B implements Hintable {
    public function hint() : array {
        $a = [];
        return $a;
    }
}

$a = new A();
var_dump($a->hint());
$b = new B();
var_dump($b->hint());

--EXPECT--
array(0) {
}
array(0) {
}
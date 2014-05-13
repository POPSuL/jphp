--TEST--
Test invalid returntype inheritance
--FILE--
<?php

interface Hintable {
    public function hint() : array;
}

class Hinted implements Hintable {
    public function hint() : callable {
        $a = [];
        return $a;
    }
}

$a = new Hinted();
var_dump($a->hint());

--EXPECTF--

Fatal error: Declaration of Hinted::hint() must be compatible with Hintable::hint() in %s on line %d, position %d

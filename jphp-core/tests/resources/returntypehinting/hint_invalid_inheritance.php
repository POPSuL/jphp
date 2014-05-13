--TEST--
Test class type hinting
--FILE--
<?php

interface Hintable {
    public function hint() : array;
}

class Hinted implements Hintable {
    public function hi1nt() : callable {
        $a = [];
        return $a;
    }
}

$a = new Hinted();
var_dump($a->hint());

--EXPECT--
array(0) {
}
array(0) {
}
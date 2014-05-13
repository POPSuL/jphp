--TEST--
Test closure hinting
--FILE--
<?php

function testErrorClosure() : callable {
    $a = [];
    return $a;
}

testErrorClosure();

--EXPECTF--

Recoverable error: the function testErrorClosure() was expected to return an callable and returned an array in %s on line %d, position %d

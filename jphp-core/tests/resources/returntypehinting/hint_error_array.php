--TEST--
Test basic errors with type hinting
--FILE--
<?php

function testErrorArray() : array {
    $a = 1;
    return $a;
}

testErrorArray();

--EXPECTF--

Recoverable error: the function testErrorArray() was expected to return an array and returned an integer in %s on line %d, position %d

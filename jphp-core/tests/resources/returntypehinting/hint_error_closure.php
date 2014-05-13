--TEST--
Test basic errors with type hinting
--FILE--
<?php

function testErrorClosure() : callable {
    $a = [];
    return $a;
}

testErrorClosure();

--EXPECTF--

Fatal error: the function testErrorClosure() was expected to return an callable and returned an array in %s on line %d, position %d

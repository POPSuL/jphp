--TEST--
Test basic type hinting
--FILE--
<?php

function testArray() : array {
    $a = [];
    return $a;
}

function testClosure() : callable {
    $a = function () {
    };
    return $a;
}

var_dump(testArray());
var_dump(testClosure() instanceof Closure);

--EXPECT--
array(0) {
}
bool(true)
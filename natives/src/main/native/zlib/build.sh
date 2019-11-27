#!/bin/bash

$CC -D_LARGEFILE64_SOURCE=1 -DHAVE_HIDDEN -DPIC $DIR/*.c $DIR/lib-*/*.c -o $SO

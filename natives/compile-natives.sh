#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

CXX="gcc -shared -fPIC -Ofast -Wall -Werror -ffast-math -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"

$CXX -lz src/main/native/deflate/*.c -o src/main/resources/libdeflate.so

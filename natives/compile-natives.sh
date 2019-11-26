#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

CXX="g++ -shared -Ofast -Wall -Werror -fPIC -ffast-math -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"

for d in src/main/native/*/; do
    NAME=$( basename $d )
    SO=src/main/resources/lib$NAME.so
    if [ -f $SO ]; then rm -v $SO; fi
    $CXX -lz src/main/native/$NAME/*.c -o $SO
done

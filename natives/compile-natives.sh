#!/bin/bash

exists() {
    [ -e "$1" ]
}

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

CXX="g++ -shared -Ofast -Wall -Werror -fPIC -ffast-math -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"

if exists src/main/resources/lib*.so; then rm -f src/main/resources/*.so; fi

for d in src/main/native/*/; do
    NAME=$( basename $d )
    SO=src/main/resources/lib$NAME.so
    if [ -f $SO ]; then rm -f $SO; fi
    $CXX -lz src/main/native/$NAME/*.c -o $SO
done

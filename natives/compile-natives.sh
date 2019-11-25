#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

CXX="g++ -shared -fPIC -Ofast -Wall -Werror -ffast-math -fwhole-program -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"

rm src/main/resources/libdeflate.so; $CXX -lz src/main/native/deflate/*.cpp -o src/main/resources/libdeflate.so

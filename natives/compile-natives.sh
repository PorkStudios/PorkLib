#!/bin/bash

exists() {
    [ -e "$1" ]
}

if [ -d tmp/ ]; then rm -rf tmp; fi; mkdir tmp/

if [ ! -d src/main/native/zlib/lib-zlib/ ]; then
    echo "Downloading zlib sources..."
    wget -O - https://cloud.daporkchop.net/programs/source/zlib-1.2.11.tar.gz | tar zxf - -C tmp/
    mv tmp/zlib-1.2.11/ src/main/native/zlib/lib-zlib/
fi

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

ARGS="-shared -Ofast -fPIC -ffast-math -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"
export CC="gcc $ARGS"
export CXX="g++ $ARGS"

if exists src/main/resources/lib*.so; then rm -f src/main/resources/*.so; fi

for d in src/main/native/*/; do
    NAME=$( basename $d )
    export DIR=src/main/native/$NAME
    export SO=src/main/resources/lib$NAME.so
    if [ -f $SO ]; then rm -f $SO; fi
    if [ -f $DIR/build.sh ]; then
        bash $DIR/build.sh
    elif exists $DIR/lib-*/; then
        $CC $DIR/*.c $DIR/lib-*/*.c -o $SO
    else
        $CC $DIR/*.c -o $SO
    fi
done

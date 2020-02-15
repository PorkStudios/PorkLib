#!/bin/bash

#i use this script simply so that i can add the natives compilation as a run configuration in intellij

export PORKLIB_NATIVES_DEBUG="true"

#make clean && \
#make -j$( nproc )
make -j$( nproc ) build.x86_64-linux-gnu

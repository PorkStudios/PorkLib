#!/bin/bash

#i use this script simply so that i can add the natives compilation as a run configuration in intellij

#make clean && \
make -j$( nproc )

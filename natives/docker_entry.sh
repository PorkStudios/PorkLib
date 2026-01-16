#!/bin/bash
#
# Adapted from The MIT License (MIT)
#
# Copyright (c) 2018-2026 DaPorkchop_
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
# files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
# modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
# is furnished to do so, subject to the following conditions:
#
# Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
# provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
# OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
# BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

set -e

if [ ! -d docker/ ]; then echo 'must be run from natives dir'; exit 1; fi

docker build -t daporkchop/porklib_build docker/
cd ..
echo "running '$@'..." >&2
exec docker run --rm -v "$PWD:$PWD:rw" --user "$( id -u ):$( id -g )" -w "$PWD/natives" daporkchop/porklib_build "$@"

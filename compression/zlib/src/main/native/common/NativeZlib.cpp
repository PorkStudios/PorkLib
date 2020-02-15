#include <common.h>
#include "NativeZlib.h"

#include <lib-zlib/zlib-ng.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_compressBoundLong
        (JNIEnv* env, jobject obj, jlong srcSize, jint mode)   {
    if (srcSize < 0)    {
        return throwException(env, "srcSize may not be negative!", srcSize);
    }

    long conservativeUpperBound = zng_deflateBound(nullptr, srcSize);
    switch (mode)   {
        case 0: //zlib
            return conservativeUpperBound + 6 + 4; //additional +4 in case `strstart`? whatever that means
        case 1: //gzip
            return conservativeUpperBound + 18; //assume there is no gzip message
        case 2: //raw
            return conservativeUpperBound;
        default:
            return throwException(env, "Invalid zlib mode!", mode);
    }
}

#include "pork-zlib.h"
#include "NativeZlib.h"

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlib_compressBound0
        (JNIEnv* env, jclass cla, jlong srcSize, jint mode)   {
    long conservativeUpperBound = zng_deflateBound(nullptr, srcSize);
    switch (mode)   {
        case Z_MODE_ZLIB:
            return conservativeUpperBound + 6 + 4; //additional +4 in case `strstart`? whatever that means
        case Z_MODE_GZIP:
            return conservativeUpperBound + 18; //assume there is no gzip message
        case Z_MODE_RAW:
        default:
            return conservativeUpperBound;
    }
}

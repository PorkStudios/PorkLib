#include <common.h>
#include "NativeZstd.h"

#include <lib-zstd/lib/zstd.h>

#include <stdlib.h>
#include <string.h>

#include <stdio.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doCompressBound
        (JNIEnv* env, jobject obj, jlong srcSize)   {

    printf("min: %d, max: %d\n", ZSTD_minCLevel(), ZSTD_maxCLevel());

    return ZSTD_compressBound(srcSize);
}

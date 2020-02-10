#include <common.h>
#include "NativeZstdDCtx.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

#include <stdlib.h>
#include <string.h>

#include <stdio.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDCtx_allocateCtx
        (JNIEnv* env, jclass cla)   {
    return (jlong) ZSTD_createDCtx();
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDCtx_releaseCtx
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeDCtx((ZSTD_DCtx*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (int) ret);
        return;
    }
}

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDCtx_doDecompress
        (JNIEnv* env, jobject obj, jlong ctx, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize)   {
    auto ret = ZSTD_decompressDCtx((ZSTD_DCtx*) ctx, (void*) dstAddr, dstSize, (void*) srcAddr, srcSize);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (int) ret);
            return 0;
        }
    }

    return (jint) ret;
}

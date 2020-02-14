#include <common.h>
#include "NativeZstdCCtx.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

#include <stdlib.h>
#include <string.h>

#include <stdio.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdCCtx_allocateCtx
        (JNIEnv* env, jclass cla)   {
    return (jlong) ZSTD_createCCtx();
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdCCtx_releaseCtx
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeCCtx((ZSTD_CCtx*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (int) ret);
        return;
    }
}

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdCCtx_doCompress
        (JNIEnv* env, jobject obj, jlong ctx, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jlong dictAddr, jint dictSize, jint compressionLevel)   {
    auto ret = dictAddr
            ? ZSTD_compress_usingDict((ZSTD_CCtx*) ctx, (void*) dstAddr, dstSize, (void*) srcAddr, srcSize, (void*) dictAddr, dictSize, compressionLevel)
            : ZSTD_compressCCtx((ZSTD_CCtx*) ctx, (void*) dstAddr, dstSize, (void*) srcAddr, srcSize, compressionLevel);

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

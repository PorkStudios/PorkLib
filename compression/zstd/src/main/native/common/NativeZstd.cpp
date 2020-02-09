#include <common.h>
#include "NativeZstd.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

#include <stdlib.h>
#include <string.h>

#include <stdio.h>

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doCompress
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jint compressionLevel)   {
    auto ret = ZSTD_compress((void*) dstAddr, dstSize, (void*) srcAddr, srcSize, compressionLevel);

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

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doDecompress
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize)   {
    auto ret = ZSTD_decompress((void*) dstAddr, dstSize, (void*) srcAddr, srcSize);

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

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doFrameContentSize
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize)   {
    auto contentSize = ZSTD_getFrameContentSize((void*) srcAddr, srcSize);

    if (contentSize == ZSTD_CONTENTSIZE_ERROR)  {
        throwException(env, "ZSTD_CONTENTSIZE_ERROR", (int) ZSTD_CONTENTSIZE_ERROR);
        return 0;
    }

    return contentSize;
}

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doCompressBound
        (JNIEnv* env, jobject obj, jlong srcSize)   {
    return ZSTD_compressBound(srcSize);
}

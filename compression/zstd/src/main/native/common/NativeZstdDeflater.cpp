#include <common.h>
#include "NativeZstdDeflater.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_allocate0
        (JNIEnv* env, jclass cla)   {
    return (jlong) ZSTD_createCCtx();
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_release0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    ZSTD_CCtx* ctx = (ZSTD_CCtx*) _ctx;
    auto ret = ZSTD_freeCCtx(ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compress0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jlong dict, jint level)   {
    ZSTD_CCtx* ctx = (ZSTD_CCtx*) _ctx;

    auto ret;
    if (dict)   {
        //pre-digested dictionary
        ret = ZSTD_compress_usingCDict(ctx, (void*) dst, dstLen, (void*) src, srcLen, (ZSTD_CDict*) dict);
    } else {
        //no dictionary
        ret = ZSTD_compressCCtx(ctx, (void*) dst, dstLen, (void*) src, srcLen, level);
    }

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jint) ret;
}

#include "pork-zstd.h"

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    allocateCtx
 * Signature: ()J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_allocateCtx
        (JNIEnv* env, jclass cla)   {
    return (jlong) ZSTD_createDCtx();
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    releaseCtx
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_releaseCtx
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeDCtx((ZSTD_DCtx*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return;
    }
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    doDecompressNoDict
 * Signature: (JJIJI)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_doDecompressNoDict
        (JNIEnv* env, jobject obj, jlong ctx, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize)   {
    auto ret = ZSTD_decompressDCtx((ZSTD_DCtx*) ctx, (void*) dstAddr, dstSize, (void*) srcAddr, srcSize);

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

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    doDecompressRawDict
 * Signature: (JJIJIJI)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_doDecompressRawDict
        (JNIEnv* env, jobject obj, jlong ctx, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jlong dictAddr, jint dictSize)   {
    auto ret = ZSTD_decompress_usingDict((ZSTD_DCtx*) ctx, (void*) dstAddr, dstSize, (void*) srcAddr, srcSize, (void*) dictAddr, dictSize);

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

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    doDecompressCDict
 * Signature: (JJIJIJ)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_doDecompressCDict
        (JNIEnv* env, jobject obj, jlong ctx, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jlong dictAddr)   {
    auto ret = ZSTD_decompress_usingDDict((ZSTD_DCtx*) ctx, (void*) dstAddr, dstSize, (void*) srcAddr, srcSize, (ZSTD_DDict*) dictAddr);

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

}

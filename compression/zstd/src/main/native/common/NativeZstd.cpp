#include "pork-zstd.h"

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstd
 * Method:    doCompress
 * Signature: (JIJII)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doCompress
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jint compressionLevel)   {
    auto ret = ZSTD_compress((void*) dstAddr, dstSize, (void*) srcAddr, srcSize, compressionLevel);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstd
 * Method:    doDecompress
 * Signature: (JIJI)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_doDecompress
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize)   {
    auto ret = ZSTD_decompress((void*) dstAddr, dstSize, (void*) srcAddr, srcSize);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstd
 * Method:    frameContentSize0
 * Signature: (JI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_frameContentSize0
        (JNIEnv* env, jobject obj, jlong src, jint srcLen)   {
    return ZSTD_getFrameContentSize((void*) src, srcLen);
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstd
 * Method:    compressBound0
 * Signature: (J)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstd_compressBound0
        (JNIEnv* env, jobject obj, jlong srcLen)   {
    return ZSTD_compressBound(srcLen);
}

}

#include "pork-zstd.h"

struct Context {
    jlong read;
    jlong written;
    jlong session;
    ZSTD_DStream* stream;
};

static bool reset(JNIEnv* env, Context* ctx)   {
    auto ret = ZSTD_DCtx_reset(ctx->stream, ZSTD_reset_session_and_parameters);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return false;
    } else {
        return true;
    }
}

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    allocate0
 * Signature: ()J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_allocate0
        (JNIEnv* env, jclass cla)   {
    Context* ctx = new Context();
    ctx->stream = ZSTD_createDStream();
    return (jlong) ctx;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    release0
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_release0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;
    auto ret = ZSTD_freeDCtx(ctx->stream);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }

    delete ctx;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    newSession0
 * Signature: (J)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSession0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    newSessionWithDict0
 * Signature: (JJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSessionWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

    auto ret = ZSTD_DCtx_refDDict(ctx->stream, (ZSTD_DDict*) dict);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    newSessionWithDictD0
 * Signature: (JJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSessionWithDictD0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

    auto ret = ZSTD_DCtx_loadDictionary(ctx->stream, (void*) dict, dictLen);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    newSessionWithDictH0
 * Signature: (J[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSessionWithDictH0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray dict, jint dictOff, jint dictLen)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    auto ret = ZSTD_DCtx_loadDictionary(ctx->stream, &dictPtr[dictOff], dictLen);

    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2D0
 * Signature: (JJIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto ret = ZSTD_decompressDCtx(ctx->stream, (void*) dst, dstLen, (void*) src, srcLen);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2H0
 * Signature: (JJI[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_decompressDCtx(ctx->stream, &dstPtr[dstOff], dstLen, (void*) src, srcLen);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2D0
 * Signature: (J[BIIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto ret = ZSTD_decompressDCtx(ctx->stream, (void*) dst, dstLen, &srcPtr[srcOff], srcLen);

    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2H0
 * Signature: (J[BII[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_decompressDCtx(ctx->stream, &dstPtr[dstOff], dstLen, &srcPtr[srcOff], srcLen);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2DWithDict0
 * Signature: (JJIJIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2DWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;

    auto ret = ZSTD_decompress_usingDDict(ctx->stream, (void*) dst, dstLen, (void*) src, srcLen, (ZSTD_DDict*) dict);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2HWithDict0
 * Signature: (JJI[BIIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2HWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDDict(ctx->stream, &dstPtr[dstOff], dstLen, (void*) src, srcLen, (ZSTD_DDict*) dict);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2DWithDict0
 * Signature: (J[BIIJIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2DWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDDict(ctx->stream, (void*) dst, dstLen, &srcPtr[srcOff], srcLen, (ZSTD_DDict*) dict);

    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2HWithDict0
 * Signature: (J[BII[BIIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2HWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDDict(ctx->stream, &dstPtr[dstOff], dstLen, &srcPtr[srcOff], srcLen, (ZSTD_DDict*) dict);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2DWithDictD0
 * Signature: (JJIJIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2DWithDictD0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto ret = ZSTD_decompress_usingDict(ctx->stream, (void*) dst, dstLen, (void*) src, srcLen, (void*) dict, dictLen);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2HWithDictD0
 * Signature: (JJI[BIIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2HWithDictD0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, &dstPtr[dstOff], dstLen, (void*) src, srcLen, (void*) dict, dictLen);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2DWithDictD0
 * Signature: (J[BIIJIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2DWithDictD0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, (void*) dst, dstLen, &srcPtr[srcOff], srcLen, (void*) dict, dictLen);

    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2HWithDictD0
 * Signature: (J[BII[BIIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2HWithDictD0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, &dstPtr[dstOff], dstLen, &srcPtr[srcOff], srcLen, (void*) dict, dictLen);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2DWithDictH0
 * Signature: (JJIJI[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2DWithDictH0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jbyteArray dict, jint dictOff, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, (void*) dst, dstLen, (void*) src, srcLen, &dictPtr[dictOff], dictLen);

    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressD2HWithDictH0
 * Signature: (JJI[BII[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressD2HWithDictH0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jbyteArray dict, jint dictOff, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, &dstPtr[dstOff], dstLen, (void*) src, srcLen, &dictPtr[dictOff], dictLen);

    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);
    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2DWithDictH0
 * Signature: (J[BIIJI[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2DWithDictH0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jbyteArray dict, jint dictOff, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, (void*) dst, dstLen, &srcPtr[srcOff], srcLen, &dictPtr[dictOff], dictLen);

    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    decompressH2HWithDictH0
 * Signature: (J[BII[BII[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressH2HWithDictH0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jbyteArray dict, jint dictOff, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    auto ret = ZSTD_decompress_usingDict(ctx->stream, &dstPtr[dstOff], dstLen, &srcPtr[srcOff], srcLen, &dictPtr[dictOff], dictLen);

    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);
    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
            return -1;
        } else {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }
    }

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    updateD2D0
 * Signature: (JJIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_updateD2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    ZSTD_outBuffer out;
    out.dst = (void*) dst;
    out.size = dstLen;
    out.pos = 0;

    ZSTD_inBuffer in;
    in.src = (void*) src;
    in.size = srcLen;
    in.pos = 0;

    auto ret = ZSTD_decompressStream(ctx->stream, &out, &in);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    ctx->read = in.pos;
    ctx->written = out.pos;

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    updateD2H0
 * Signature: (JJI[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_updateD2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    ZSTD_outBuffer out;
    out.dst = &dstPtr[dstOff];
    out.size = dstLen;
    out.pos = 0;

    ZSTD_inBuffer in;
    in.src = (void*) src;
    in.size = srcLen;
    in.pos = 0;

    auto ret = ZSTD_decompressStream(ctx->stream, &out, &in);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    ctx->read = in.pos;
    ctx->written = out.pos;

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    updateH2D0
 * Signature: (J[BIIJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_updateH2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    ZSTD_outBuffer out;
    out.dst = (void*) dst;
    out.size = dstLen;
    out.pos = 0;

    ZSTD_inBuffer in;
    in.src = &srcPtr[srcOff];
    in.size = srcLen;
    in.pos = 0;

    auto ret = ZSTD_decompressStream(ctx->stream, &out, &in);

    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    ctx->read = in.pos;
    ctx->written = out.pos;

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    updateH2D0
 * Signature: (J[BII[BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_updateH2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen)   {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    ZSTD_outBuffer out;
    out.dst = &dstPtr[dstOff];
    out.size = dstLen;
    out.pos = 0;

    ZSTD_inBuffer in;
    in.src = &srcPtr[srcOff];
    in.size = srcLen;
    in.pos = 0;

    auto ret = ZSTD_decompressStream(ctx->stream, &out, &in);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    ctx->read = in.pos;
    ctx->written = out.pos;

    return (jlong) ret;
}

}

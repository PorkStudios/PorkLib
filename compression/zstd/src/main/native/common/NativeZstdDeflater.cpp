#include "pork-zstd.h"

struct Context {
    jlong read;
    jlong written;
    jlong session;
    ZSTD_CStream* stream;
};

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    allocate0
 * Signature: ()J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_allocate0
        (JNIEnv* env, jclass cla)   {
    Context* ctx = new Context();
    ctx->stream = ZSTD_createCStream();
    return (jlong) ctx;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    release0
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_release0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;

    auto ret = ZSTD_freeCStream(ctx->stream);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }

    delete ctx;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    newSession0
 * Signature: (J)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSession0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    newSessionWithLevel0
 * Signature: (JI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSessionWithLevel0
        (JNIEnv* env, jclass cla, jlong _ctx, jint level)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto ret = ZSTD_CCtx_setParameter(ctx->stream, ZSTD_c_compressionLevel, level);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    newSessionWithDict0
 * Signature: (JJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSessionWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto ret = ZSTD_CCtx_refCDict(ctx->stream, (ZSTD_CDict*) dict);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressD2D0
 * Signature: (JJIJII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressD2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jint level)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto ret = ZSTD_compressCCtx(ctx->stream, (void*) dst, dstLen, (void*) src, srcLen, level);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressD2H0
 * Signature: (JJI[BIII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressD2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jint level)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_compressCCtx(ctx->stream, &dstPtr[dstOff], dstLen, (void*) src, srcLen, level);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressH2D0
 * Signature: (JJIJII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressH2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jint level)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto ret = ZSTD_compressCCtx(ctx->stream, (void*) dst, dstLen, &srcPtr[srcOff], srcLen, level);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressH2H0
 * Signature: (JJIJII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressH2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jint level)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

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

    auto ret = ZSTD_compressCCtx(ctx->stream, &dstPtr[dstOff], dstLen, &srcPtr[srcOff], srcLen, level);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressD2DWithDict0
 * Signature: (JJIJIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressD2DWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto ret = ZSTD_compress_usingCDict(ctx->stream, (void*) dst, dstLen, (void*) src, srcLen, (ZSTD_CDict*) dict);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressD2HWithDict0
 * Signature: (JJI[BIIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressD2HWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    auto ret = ZSTD_compress_usingCDict(ctx->stream, &dstPtr[dstOff], dstLen, (void*) src, srcLen, (ZSTD_CDict*) dict);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressH2DWithDict0
 * Signature: (J[BIIJIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressH2DWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    auto ret = ZSTD_compress_usingCDict(ctx->stream, (void*) dst, dstLen, &srcPtr[srcOff], srcLen, (ZSTD_CDict*) dict);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    compressH2HWithDict0
 * Signature: (J[BII[BIIJ)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressH2HWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

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

    auto ret = ZSTD_compress_usingCDict(ctx->stream, &dstPtr[dstOff], dstLen, &srcPtr[srcOff], srcLen, (ZSTD_CDict*) dict);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    updateD2D0
 * Signature: (JJIJII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_updateD2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jint endop)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

    ZSTD_outBuffer out;
    out.dst = (void*) dst;
    out.size = dstLen;
    out.pos = 0;

    ZSTD_inBuffer in;
    in.src = (void*) src;
    in.size = srcLen;
    in.pos = 0;

    auto ret = ZSTD_compressStream2(ctx->stream, &out, &in, (ZSTD_EndDirective) endop);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    ctx->read = in.pos;
    ctx->written = out.pos;

    return (jlong) ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    updateD2H0
 * Signature: (JJI[BIII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_updateD2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jint endop)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

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

    auto ret = ZSTD_compressStream2(ctx->stream, &out, &in, (ZSTD_EndDirective) endop);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    updateH2D0
 * Signature: (J[BIIJII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_updateH2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jint endop)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

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

    auto ret = ZSTD_compressStream2(ctx->stream, &out, &in, (ZSTD_EndDirective) endop);

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
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    updateH2D0
 * Signature: (J[BII[BIII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_updateH2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jint endop)   {
    Context* ctx = (Context*) _ctx;
    ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_only);

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

    auto ret = ZSTD_compressStream2(ctx->stream, &out, &in, (ZSTD_EndDirective) endop);

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

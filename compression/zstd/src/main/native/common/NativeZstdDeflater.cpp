#include "pork-zstd.h"

#include <porklib_jni_arrays.hpp>

struct Context {
    jlong read;
    jlong written;
    jlong session;
    ZSTD_CStream* stream;
};

static bool reset(JNIEnv* env, Context* ctx)   {
    auto ret = ZSTD_CCtx_reset(ctx->stream, ZSTD_reset_session_and_parameters);
    
    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return false;
    } else {
        return true;
    }
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    allocate0
 * Signature: ()J
 */
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_allocate0
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
extern "C" __attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_release0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;

    auto ret = ZSTD_freeCStream(ctx->stream);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }

    delete ctx;
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compress__JJ_3BIIIJ_3BIIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining,
        jint level) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadableByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWritableByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};

        auto ret = ZSTD_compressCCtx(ctx->stream, dst.data(), dst.size(), src.data(), src.size(), level);

        if (ZSTD_isError(ret))  {
            if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
                return -1;
            } else {
                throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
                return 0;
            }
        }

        return (jlong) ret;
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressWithDict__JJ_3BIIIJ_3BIIIJ(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining,
        jlong dict) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadableByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWritableByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};

        auto ret = ZSTD_compress_usingCDict(ctx->stream, dst.data(), dst.size(), src.data(), src.size(), reinterpret_cast<ZSTD_CDict*>(dict));

        if (ZSTD_isError(ret))  {
            if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
                return -1;
            } else {
                throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
                return 0;
            }
        }

        return (jlong) ret;
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_compressWithDict__JJ_3BIIIJ_3BIIIJ_3BIIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining,
        jlong dictDirectAddr, jbyteArray dictArray, jint dictArrayOffset, jint dictPosition, jint dictRemaining,
        jint level) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadableByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWritableByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};
        porklib::jni::AnyReadableByteRegion dict{env, dictDirectAddr, dictArray, dictArrayOffset, dictPosition, dictRemaining};

        auto ret = ZSTD_compress_usingDict(ctx->stream, dst.data(), dst.size(), src.data(), src.size(), dict.data(), dict.size(), level);

        if (ZSTD_isError(ret))  {
            if (ZSTD_getErrorCode(ret) == ZSTD_error_dstSize_tooSmall) {
                return -1;
            } else {
                throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
                return 0;
            }
        }

        return (jlong) ret;
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSessionWithDict__JJ_3BIIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong dictDirectAddr, jbyteArray dictArray, jint dictArrayOffset, jint dictPosition, jint dictRemaining,
        jint level) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        if (!reset(env, ctx)) {
            return 0;
        }

        auto ret = ZSTD_CCtx_setParameter(ctx->stream, ZSTD_c_compressionLevel, level);

        if (ZSTD_isError(ret))  {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }

        porklib::jni::AnyReadableByteRegion dict{env, dictDirectAddr, dictArray, dictArrayOffset, dictPosition, dictRemaining};
        ret = ZSTD_CCtx_loadDictionary(ctx->stream, dict.data(), dict.size());

        if (ZSTD_isError(ret))  {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }

        return ++ctx->session;
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_update__JJ_3BIIIJ_3BIIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining,
        jint flush) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadableByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWritableByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};

        ZSTD_outBuffer out;
        out.dst = dst.data();
        out.size = dst.size();
        out.pos = 0;

        ZSTD_inBuffer in;
        in.src = src.data();
        in.size = src.size();
        in.pos = 0;

        auto ret = ZSTD_compressStream2(ctx->stream, &out, &in, (ZSTD_EndDirective) flush);

        if (ZSTD_isError(ret))  {
            throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
            return 0;
        }

        ctx->read = in.pos;
        ctx->written = out.pos;

        return (jlong) ret;
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    newSession0
 * Signature: (J)J
 */
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSession0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

    return ++ctx->session;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater
 * Method:    newSessionWithLevel0
 * Signature: (JI)J
 */
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSessionWithLevel0
        (JNIEnv* env, jclass cla, jlong _ctx, jint level)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

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
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflater_newSessionWithDict0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong dict)   {
    Context* ctx = (Context*) _ctx;
    if (!reset(env, ctx)) {
        return 0;
    }

    auto ret = ZSTD_CCtx_refCDict(ctx->stream, (ZSTD_CDict*) dict);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return 0;
    }

    return ++ctx->session;
}

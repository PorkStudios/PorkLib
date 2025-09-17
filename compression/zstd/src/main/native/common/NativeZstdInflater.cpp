#include "pork-zstd.h"

#include <porklib_jni_arrays.hpp>

namespace {
struct Context {
    jlong read;
    jlong written;
    jlong session;
    ZSTD_DStream* stream;
};
}

static bool reset(JNIEnv* env, Context* ctx)   {
    auto ret = ZSTD_DCtx_reset(ctx->stream, ZSTD_reset_session_and_parameters);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return false;
    } else {
        return true;
    }
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater
 * Method:    allocate0
 * Signature: ()J
 */
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_allocate0
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
extern "C" __attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_release0
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
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSession0
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
extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSessionWithDict0
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

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_newSessionWithDict__JJ_3BIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong dictDirectAddr, jbyteArray dictArray, jint dictArrayOffset, jint dictPosition, jint dictRemaining) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        if (!reset(env, ctx)) {
            return 0;
        }

        porklib::jni::AnyReadOnlyByteRegion dict{env, dictDirectAddr, dictArray, dictArrayOffset, dictPosition, dictRemaining};
        auto ret = ZSTD_DCtx_loadDictionary(ctx->stream, dict.data(), dict.size());

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

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompress__JJ_3BIIIJ_3BIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadOnlyByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWriteOnlyByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};

        auto ret = ZSTD_decompressDCtx(ctx->stream, dst.data(), dst.size(), src.data(), src.size());

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

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressWithDict__JJ_3BIIIJ_3BIIIJ(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining,
        jlong _dict) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);
        ZSTD_DDict* dict = reinterpret_cast<ZSTD_DDict*>(_dict);

        porklib::jni::AnyReadOnlyByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWriteOnlyByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};

        auto ret = ZSTD_decompress_usingDDict(ctx->stream, dst.data(), dst.size(), src.data(), src.size(), dict);

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

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_decompressWithDict__JJ_3BIIIJ_3BIIIJ_3BIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining,
        jlong dictDirectAddr, jbyteArray dictArray, jint dictArrayOffset, jint dictPosition, jint dictRemaining) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadOnlyByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWriteOnlyByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};
        porklib::jni::AnyReadOnlyByteRegion dict{env, dictDirectAddr, dictArray, dictArrayOffset, dictPosition, dictRemaining};

        auto ret = ZSTD_decompress_usingDict(ctx->stream, dst.data(), dst.size(), src.data(), src.size(), dict.data(), dict.size());

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

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflater_update__JJ_3BIIIJ_3BIII(
        JNIEnv* env, jclass, jlong _ctx,
        jlong srcDirectAddr, jbyteArray srcArray, jint srcArrayOffset, jint srcPosition, jint srcRemaining,
        jlong dstDirectAddr, jbyteArray dstArray, jint dstArrayOffset, jint dstPosition, jint dstRemaining) noexcept {
    try {
        Context* ctx = reinterpret_cast<Context*>(_ctx);

        porklib::jni::AnyReadOnlyByteRegion src{env, srcDirectAddr, srcArray, srcArrayOffset, srcPosition, srcRemaining};
        porklib::jni::AnyWriteOnlyByteRegion dst{env, dstDirectAddr, dstArray, dstArrayOffset, dstPosition, dstRemaining};

        ZSTD_outBuffer out = {};
        out.dst = dst.data();
        out.size = dst.size();
        out.pos = 0;

        ZSTD_inBuffer in = {};
        in.src = src.data();
        in.size = src.size();
        in.pos = 0;

        auto ret = ZSTD_decompressStream(ctx->stream, &out, &in);

        if (ZSTD_isError(ret)) {
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

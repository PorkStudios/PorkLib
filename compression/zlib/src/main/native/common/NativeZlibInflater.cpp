#include "pork-zlib.h"

struct Context {
    jlong read;
    jlong written;
    jlong session;
    zng_stream stream;
};

static bool tryReset(JNIEnv* env, Context* ctx)   {
    ctx->session++;
    int ret = zng_inflateReset(&ctx->stream);
    if (ret != Z_OK)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Couldn't reset inflater!", ret);
    }
    return ret == Z_OK;
}

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    allocate0
 * Signature: (I)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_allocate0
        (JNIEnv* env, jclass cla, jint mode)   {
    Context* ctx = new Context();

    int ret = zng_inflateInit2(&ctx->stream, windowBits(mode));

    if (ret != Z_OK)    {
        const char* msg = ctx->stream.msg;
        delete ctx;
        throwException(env, msg == nullptr ? "Couldn't init inflater!" : msg, ret);
        return 0;
    }

    return (jlong) ctx;
}

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    release0
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_release0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;

    if (!tryReset(env, ctx))    {
        return;
    }

    int ret = zng_inflateEnd(&ctx->stream);
    const char* msg = ctx->stream.msg;
    delete ctx;

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end inflater!" : msg, ret);
    }
}

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    newSession0
 * Signature: (JJI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_newSession0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    if (!tryReset(env, ctx))    {
        return 0;
    }

    jlong session = ++ctx->session;

    if (dict && dictLen)   {
        //set dictionary
        int ret = zng_inflateSetDictionary(&ctx->stream, (unsigned char*) dict, dictLen);
        if (ret != Z_OK)    {
            throwException(env, ctx->stream.msg ? ctx->stream.msg : "Couldn't set inflater dictionary!", ret);
            return 0;
        }
    }

    return session;
}

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    updateD2D0
 * Signature: (JJIJII)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_updateD2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jint flush)  {
    Context* ctx = (Context*) _ctx;

    ctx->read = 0;
    ctx->written = 0;

    ctx->stream.next_in = (unsigned char*) src;
    ctx->stream.avail_in = srcLen;

    ctx->stream.next_out = (unsigned char*) dst;
    ctx->stream.avail_out = dstLen;

    int ret = zng_inflate(&ctx->stream, flush);
    if (ret < 0)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Invalid return value from inflate()!", ret);
        return 0;
    }
    ctx->read =    srcLen - ctx->stream.avail_in;
    ctx->written = dstLen - ctx->stream.avail_out;
    return ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    updateD2H0
 * Signature: (JJI[BIII)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_updateD2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jint flush)  {
    Context* ctx = (Context*) _ctx;

    auto dstPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dst, nullptr);
    if (!dstPtr)    {
        throwException(env, "Unable to pin dst array");
        return 0;
    }

    ctx->read = 0;
    ctx->written = 0;

    ctx->stream.next_in = (unsigned char*) src;
    ctx->stream.avail_in = srcLen;

    ctx->stream.next_out = &dstPtr[dstOff];
    ctx->stream.avail_out = dstLen;

    int ret = zng_inflate(&ctx->stream, flush);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);

    if (ret < 0)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Invalid return value from inflate()!", ret);
    } else {
        ctx->read = srcLen - ctx->stream.avail_in;
        ctx->written = dstLen - ctx->stream.avail_out;
    }
    return ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    updateH2D0
 * Signature: (J[BIIJII)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_updateH2D0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jlong dst, jint dstLen, jint flush)  {
    Context* ctx = (Context*) _ctx;

    auto srcPtr = (unsigned char*) env->GetPrimitiveArrayCritical(src, nullptr);
    if (!srcPtr)    {
        throwException(env, "Unable to pin src array");
        return 0;
    }

    ctx->read = 0;
    ctx->written = 0;

    ctx->stream.next_in = &srcPtr[srcOff];
    ctx->stream.avail_in = srcLen;

    ctx->stream.next_out = (unsigned char*) dst;
    ctx->stream.avail_out = dstLen;

    int ret = zng_inflate(&ctx->stream, flush);

    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ret < 0)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Invalid return value from inflate()!", ret);
    } else {
        ctx->read = srcLen - ctx->stream.avail_in;
        ctx->written = dstLen - ctx->stream.avail_out;
    }
    return ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater
 * Method:    updateH2H0
 * Signature: (J[BII[BIII)I
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_updateH2H0
        (JNIEnv* env, jclass cla, jlong _ctx, jbyteArray src, jint srcOff, jint srcLen, jbyteArray dst, jint dstOff, jint dstLen, jint flush)  {
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

    ctx->read = 0;
    ctx->written = 0;

    ctx->stream.next_in = &srcPtr[srcOff];
    ctx->stream.avail_in = srcLen;

    ctx->stream.next_out = &dstPtr[dstOff];
    ctx->stream.avail_out = dstLen;

    int ret = zng_inflate(&ctx->stream, flush);

    env->ReleasePrimitiveArrayCritical(dst, dstPtr, 0);
    env->ReleasePrimitiveArrayCritical(src, srcPtr, 0);

    if (ret < 0)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Invalid return value from inflate()!", ret);
    } else {
        ctx->read = srcLen - ctx->stream.avail_in;
        ctx->written = dstLen - ctx->stream.avail_out;
    }
    return ret;
}

}


#include "pork-zlib.h"
#include "NativeZlibDeflater.h"

struct Context {
    jlong read;
    jlong written;
    jlong session;
    zng_stream stream;
    //jboolean reset;
};

/*bool maybeReset(JNIEnv* env, Context* ctx)   {
    if (true || !ctx->reset)    {
        int ret = zng_deflateReset(&ctx->stream);

        if (ret != Z_OK)    {
            throwException(env, ctx->stream.msg == nullptr ? "Couldn't reset deflater!" : ctx->stream.msg, ret);
            return true;
        }

        ctx->reset = true;
    }

    return false;
}*/

bool tryReset(JNIEnv* env, Context* ctx)   {
    ctx->session++;
    int ret = zng_deflateReset(&ctx->stream);
    if (ret != Z_OK)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Couldn't reset deflater!", ret);
    }
    return ret == Z_OK;
}

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_allocate0
        (JNIEnv* env, jclass cla, jint level, jint strategy, jint mode)   {
    Context* ctx = new Context();

    int ret = zng_deflateInit2(&ctx->stream, level, Z_DEFLATED, windowBits(mode), 8, strategy);

    if (ret != Z_OK)    {
        const char* msg = ctx->stream.msg;
        delete ctx;
        throwException(env, msg == nullptr ? "Couldn't init deflater!" : msg, ret);
        return 0;
    }

    return (jlong) ctx;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_release0
        (JNIEnv* env, jclass cla, jlong _ctx)   {
    Context* ctx = (Context*) _ctx;

    if (!tryReset(env, ctx))    {
        return;
    }

    int ret = zng_deflateEnd(&ctx->stream);
    const char* msg = ctx->stream.msg;
    delete ctx;

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end deflater!" : msg, ret);
    }
}
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_newSession0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong dict, jint dictLen)   {
    Context* ctx = (Context*) _ctx;

    if (!tryReset(env, ctx))    {
        return 0;
    }

    jlong session = ++ctx->session;

    if (dict && dictLen)   {
        //set dictionary
        int ret = zng_deflateSetDictionary(&ctx->stream, (unsigned char*) dict, dictLen);
        if (ret != Z_OK)    {
            throwException(env, ctx->stream.msg ? ctx->stream.msg : "Couldn't set deflater dictionary!", ret);
            return 0;
        }
    }

    return session;
}

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_update0
        (JNIEnv* env, jclass cla, jlong _ctx, jlong src, jint srcLen, jlong dst, jint dstLen, jint flush)  {
    Context* ctx = (Context*) _ctx;

    ctx->stream.next_in = (unsigned char*) src;
    ctx->stream.avail_in = srcLen;

    ctx->stream.next_out = (unsigned char*) dst;
    ctx->stream.avail_out = dstLen;

    int ret = zng_deflate(&ctx->stream, flush);
    if (ret < 0)    {
        throwException(env, ctx->stream.msg ? ctx->stream.msg : "Invalid return value from deflate()!", ret);
        return 0;
    }
    ctx->read =    srcLen - ctx->stream.avail_in;
    ctx->written = dstLen - ctx->stream.avail_out;
    return ret;
}

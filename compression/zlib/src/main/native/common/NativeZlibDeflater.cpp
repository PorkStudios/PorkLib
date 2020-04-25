#include "pork-zlib.h"
#include "NativeZlibDeflater.h"

struct Context {
    jlong read;
    jlong written;
    zng_stream stream;
};

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
    int ret = zng_deflateReset(&ctx->stream);

    if (ret != Z_OK)    {
        throwException(env, ctx->stream.msg == nullptr ? "Couldn't reset deflater!" : ctx->stream.msg, ret);
        return;
    }

    ret = zng_deflateEnd(&ctx->stream);
    const char* msg = ctx->stream.msg;
    delete ctx;

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end deflater!" : msg, ret);
    }
}

/*__attribute__((visibility("default"))) jboolean JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_doFullDeflate
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    //set stream buffers
    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcSize;

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstSize;

    int ret = zng_deflate(stream, Z_FINISH);
    if (ret == Z_STREAM_END)    {
        env->SetIntField(obj, readBytesID,    srcSize - stream->avail_in);
        env->SetIntField(obj, writtenBytesID, dstSize - stream->avail_out);
        return true;
    } else if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from deflate()!" : stream->msg, ret);
    }

    return false;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_doUpdate
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jboolean flush)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    //set stream buffers
    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcSize;

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstSize;

    int ret = zng_deflate(stream, flush ? Z_SYNC_FLUSH : Z_NO_FLUSH);
    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from deflate()!" : stream->msg, ret);
        return;
    }

    env->SetIntField(obj, readBytesID,    srcSize - stream->avail_in);
    env->SetIntField(obj, writtenBytesID, dstSize - stream->avail_out);
}

__attribute__((visibility("default"))) jboolean JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_doFinish
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    //set stream buffers
    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcSize;

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstSize;

    int ret = zng_deflate(stream, Z_FINISH);
    if (ret != Z_STREAM_END && ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from deflate()!" : stream->msg, ret);
        return false;
    }

    env->SetIntField(obj, readBytesID,    srcSize - stream->avail_in);
    env->SetIntField(obj, writtenBytesID, dstSize - stream->avail_out);

    if (ret == Z_STREAM_END)    {
        env->SetBooleanField(obj, finishedID, true);
        return true;
    } else {
        return false;
    }
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_doReset
        (JNIEnv* env, jobject obj)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);
    int ret = zng_deflateReset(stream);

    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't reset deflater!" : stream->msg, ret);
        return;
    }

    env->SetBooleanField(obj, resetID, true);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_doDict
        (JNIEnv* env, jobject obj, jlong dictAddr, jint dictSize)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);
    int ret = zng_deflateSetDictionary(stream, (unsigned char*) dictAddr, dictSize);

    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't set deflater dictionary!" : stream->msg, ret);
        return;
    }
}*/

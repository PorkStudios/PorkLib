#include <common.h>
#include "net_daporkchop_lib_natives_zlib_NativeDeflater.h"

#include "../lib-zlib/zlib.h"

#include <stdlib.h>
#include <string.h>

#include <stdio.h>

static jfieldID ctxID;
static jfieldID readBytesID;
static jfieldID writtenBytesID;
static jfieldID finishedID;

void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_load(JNIEnv* env, jclass cla)  {
    ctxID          = env->GetFieldID(cla, "ctx", "J");
    readBytesID    = env->GetFieldID(cla, "readBytes", "I");
    writtenBytesID = env->GetFieldID(cla, "writtenBytes", "I");
    finishedID     = env->GetFieldID(cla, "finished", "Z");
}

jlong JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_init(JNIEnv* env, jclass cla, jint level, jint mode)   {
    if (level < 0 || level > 9) {
        throwException(env, "Invalid level!", level);
        return 0;
    }

    int windowBits;
    if (mode == 0)  { //zlib
        windowBits = 15;
    } else if (mode == 1) { //gzip
        windowBits = 15 + 16;
    } else if (mode == 3) { //raw deflate
        windowBits = -15;
    } else {
        throwException(env, "Invalid deflater mode!", mode);
        return 0;
    }

    z_stream* stream = (z_stream*) malloc(sizeof(z_stream));
    memset(stream, 0, sizeof(z_stream));

    int ret = deflateInit2(stream, level, Z_DEFLATED, windowBits, 8, Z_DEFAULT_STRATEGY);

    if (ret != Z_OK)    {
        char* msg = stream->msg;
        free(stream);
        throwException(env, msg == nullptr ? "Couldn't init deflater!" : msg, ret);
    }

    return (jlong) stream;
}

void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_end(JNIEnv* env, jclass cla, jlong ctx)  {
    z_stream* stream = (z_stream*) ctx;
    int ret = deflateReset(stream);
    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't reset deflater!" : stream->msg, ret);
        return;
    }

    ret = deflateEnd(stream);
    char* msg = stream->msg;
    free(stream);

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end deflater!" : msg, ret);
    }
}

void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_input(JNIEnv* env, jobject obj, jlong srcAddr, jint srcLen) {
    z_stream* stream = (z_stream*) env->GetLongField(obj, ctxID);

    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcLen;
}

void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_output(JNIEnv* env, jobject obj, jlong dstAddr, jint dstLen)    {
    z_stream* stream = (z_stream*) env->GetLongField(obj, ctxID);

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstLen;
}

void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_deflate(JNIEnv* env, jobject obj, jboolean finish)  {
    z_stream* stream = (z_stream*) env->GetLongField(obj, ctxID);

    jint avail_in  = stream->avail_in;
    jint avail_out = stream->avail_out;

    //even if finish is set to true, don't actually run deflate with the finish flag if the entire data isn't going to be able to be read this invocation
    int ret = deflate(stream, finish ? Z_FINISH : Z_NO_FLUSH);
    if (ret == Z_STREAM_END)    {
        env->SetBooleanField(obj, finishedID, (jboolean) 1);
    } else if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from deflate()!" : stream->msg, ret);
        return;
    }

    env->SetIntField(obj, readBytesID,    (avail_in - stream->avail_in));
    env->SetIntField(obj, writtenBytesID, (avail_out - stream->avail_out));
}

void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeDeflater_reset(JNIEnv* env, jobject obj)     {
    z_stream* stream = (z_stream*) env->GetLongField(obj, ctxID);
    int ret = deflateReset(stream);

    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't reset deflater!" : stream->msg, ret);
    }

    env->SetIntField(obj, readBytesID, 0);
    env->SetIntField(obj, writtenBytesID, 0);
    env->SetBooleanField(obj, finishedID, (jboolean) 0);
}

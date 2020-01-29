#include <common.h>
#include "net_daporkchop_lib_natives_zlib_NativeInflater.h"

#include <lib-zlib/zlib-ng.h>

#include <stdlib.h>
#include <string.h>

static jfieldID ctxID;
static jfieldID readBytesID;
static jfieldID writtenBytesID;
static jfieldID finishedID;

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_load(JNIEnv* env, jclass cla)  {
    ctxID          = env->GetFieldID(cla, "ctx", "J");
    readBytesID    = env->GetFieldID(cla, "readBytes", "I");
    writtenBytesID = env->GetFieldID(cla, "writtenBytes", "I");
    finishedID     = env->GetFieldID(cla, "finished", "Z");
}

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_init(JNIEnv* env, jclass cla, jint mode)   {
    int windowBits;
    if (mode == 0)  { //zlib
        windowBits = 0;
    } else if (mode == 1) { //gzip
        windowBits = 16;
    } else if (mode == 2) { //auto-detect zlib or gzip
        windowBits = 32;
    } else if (mode == 3) { //raw deflate
        windowBits = -15;
    } else {
        throwException(env, "Invalid inflater mode!", mode);
        return 0;
    }
    
    zng_stream* stream = (zng_stream*) malloc(sizeof(zng_stream));
    memset(stream, 0, sizeof(zng_stream));

    int ret = zng_inflateInit2(stream, windowBits);

    if (ret != Z_OK)    {
        const char* msg = stream->msg;
        free(stream);
        throwException(env, msg == nullptr ? "Couldn't init inflater!" : msg, ret);
    }

    return (jlong) stream;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_end(JNIEnv* env, jclass cla, jlong ctx)  {
    zng_stream* stream = (zng_stream*) ctx;
    int ret = zng_inflateReset(stream);
    if (ret != Z_OK)    {
        const char* msg = stream->msg;
        free(stream);
        throwException(env, msg == nullptr ? "Couldn't reset inflater!" : msg, ret);
        return;
    }

    ret = zng_inflateEnd(stream);
    const char* msg = stream->msg;
    free(stream);

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end inflater!" : msg, ret);
    }
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_input(JNIEnv* env, jobject obj, jlong srcAddr, jint srcLen) {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcLen;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_output(JNIEnv* env, jobject obj, jlong dstAddr, jint dstLen)    {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstLen;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_inflate(JNIEnv* env, jobject obj)  {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    jint avail_in  = stream->avail_in;
    jint avail_out = stream->avail_out;

    //don't actually run inflate with the flush flag if the entire data isn't going to be able to be read this invocation
    //of course this doesn't mean it'll be buffering 4GB of data, it just means that it won't begin flushing the data until it decides to
    int ret = zng_inflate(stream, Z_SYNC_FLUSH);
    if (ret == Z_STREAM_END)    {
        env->SetBooleanField(obj, finishedID, (jboolean) 1);
    } else if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from inflate()!" : stream->msg, ret);
        return;
    }

    env->SetIntField(obj, readBytesID,    avail_in - stream->avail_in);
    env->SetIntField(obj, writtenBytesID, avail_out - stream->avail_out);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_natives_zlib_NativeInflater_reset(JNIEnv* env, jobject obj)     {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);
    
    int ret = zng_inflateReset(stream);
    
    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't reset inflater!" : stream->msg, ret);
    }

    env->SetIntField(obj, readBytesID, 0);
    env->SetIntField(obj, writtenBytesID, 0);
    env->SetBooleanField(obj, finishedID, (jboolean) 0);
}

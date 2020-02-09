#include <common.h>
#include "NativeZlibDeflater.h"

#include <lib-zlib/zlib-ng.h>

#include <stdlib.h>
#include <string.h>

#include <stdio.h>

static jfieldID ctxID;
static jfieldID readBytesID;
static jfieldID writtenBytesID;
static jfieldID resetID;
static jfieldID finishedID;

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_load
        (JNIEnv* env, jclass cla)  {
    ctxID          = env->GetFieldID(cla, "ctx", "J");
    readBytesID    = env->GetFieldID(cla, "readBytes", "I");
    writtenBytesID = env->GetFieldID(cla, "writtenBytes", "I");
    resetID        = env->GetFieldID(cla, "reset", "Z");
    finishedID     = env->GetFieldID(cla, "finished", "Z");
}

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_allocateCtx
        (JNIEnv* env, jclass cla, jint level, jint strategy, jint mode)   {
    if (level < -1 || level > 9) {
        throwException(env, "Invalid level!", level);
        return 0;
    } else if (strategy < 0 || strategy > 4)    {
        throwException(env, "Invalid strategy!", strategy);
        return 0;
    }

    int windowBits;
    switch (mode)   {
        case 0: //zlib
            windowBits = 15;
            break;
        case 1: //gzip
            windowBits = 15 + 16;
            break;
        case 2: //raw
            windowBits = -15;
            break;
        default:
            throwException(env, "Invalid deflater mode!", mode);
            return 0;
    }

    zng_stream* stream = (zng_stream*) malloc(sizeof(zng_stream));
    memset(stream, 0, sizeof(zng_stream));

    int ret = zng_deflateInit2(stream, level, Z_DEFLATED, windowBits, 8, strategy);

    if (ret != Z_OK)    {
        const char* msg = stream->msg;
        free(stream);
        throwException(env, msg == nullptr ? "Couldn't init deflater!" : msg, ret);
        return 0;
    }

    return (jlong) stream;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_releaseCtx
        (JNIEnv* env, jclass cla, jlong ctx)   {
    zng_stream* stream = (zng_stream*) ctx;
    int ret = zng_deflateReset(stream);

    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't reset deflater!" : stream->msg, ret);
        return;
    }

    ret = zng_deflateEnd(stream);
    const char* msg = stream->msg;
    free(stream);

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end deflater!" : msg, ret);
    }
}

__attribute__((visibility("default"))) jboolean JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibDeflater_doFullDeflate
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
}

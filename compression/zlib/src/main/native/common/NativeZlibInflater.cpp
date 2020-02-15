#include <common.h>
#include "NativeZlibInflater.h"

#include <lib-zlib/zlib-ng.h>

static jfieldID ctxID;
static jfieldID readBytesID;
static jfieldID writtenBytesID;
static jfieldID resetID;
static jfieldID finishedID;

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_load
        (JNIEnv* env, jclass cla)  {
    ctxID          = env->GetFieldID(cla, "ctx", "J");
    readBytesID    = env->GetFieldID(cla, "readBytes", "I");
    writtenBytesID = env->GetFieldID(cla, "writtenBytes", "I");
    resetID        = env->GetFieldID(cla, "reset", "Z");
    finishedID     = env->GetFieldID(cla, "finished", "Z");
}

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_allocateCtx
        (JNIEnv* env, jclass cla, jint mode)   {
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
        case 3: //auto
            windowBits = 15 + 32;
            break;
        default:
            throwException(env, "Invalid inflater mode!", mode);
            return 0;
    }

    zng_stream* stream = (zng_stream*) new char[sizeof(zng_stream)]();

    int ret = zng_inflateInit2(stream, windowBits);

    if (ret != Z_OK)    {
        const char* msg = stream->msg;
        delete stream;
        throwException(env, msg == nullptr ? "Couldn't init inflater!" : msg, ret);
        return 0;
    }

    return (jlong) stream;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_releaseCtx
        (JNIEnv* env, jclass cla, jlong ctx)   {
    zng_stream* stream = (zng_stream*) ctx;

    int ret = zng_inflateEnd(stream);
    const char* msg = stream->msg;
    delete stream;

    if (ret != Z_OK)    {
        throwException(env, msg == nullptr ? "Couldn't end inflater!" : msg, ret);
    }
}

__attribute__((visibility("default"))) jboolean JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_doFullInflate
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jlong dictAddr, jint dictSize)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    //set stream buffers
    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcSize;

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstSize;

    int ret = zng_inflate(stream, Z_FINISH);
    if (ret == Z_NEED_DICT)  {
        if (dictAddr)    {
            //set dictionary
            ret = zng_inflateSetDictionary(stream, (unsigned char*) dictAddr, dictSize);
            if (ret != Z_OK)    {
                throwException(env, stream->msg == nullptr ? "Couldn't set inflater dictionary!" : stream->msg, ret);
                return false;
            }

            //try again
            ret = zng_inflate(stream, Z_FINISH);
        } else {
            throwException(env, "Dictionary needed, but none was given!", ret);
            return false;
        }
    }

    if (ret == Z_STREAM_END)    {
        env->SetIntField(obj, readBytesID,    srcSize - stream->avail_in);
        env->SetIntField(obj, writtenBytesID, dstSize - stream->avail_out);
        return true;
    } else if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from inflate()!" : stream->msg, ret);
    }

    return false;
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_doUpdate
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jlong dictAddr, jint dictSize, jboolean flush)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    //set stream buffers
    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcSize;

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstSize;

    int ret = zng_inflate(stream, flush ? Z_SYNC_FLUSH : Z_NO_FLUSH);
    if (ret == Z_NEED_DICT)  {
        if (dictAddr)    {
            //set dictionary
            ret = zng_inflateSetDictionary(stream, (unsigned char*) dictAddr, dictSize);
            if (ret != Z_OK)    {
                throwException(env, stream->msg == nullptr ? "Couldn't set inflater dictionary!" : stream->msg, ret);
                return;
            }

            //try again
            ret = zng_inflate(stream, flush ? Z_SYNC_FLUSH : Z_NO_FLUSH);
        } else {
            printf("not setting dict\n");
            throwException(env, "Dictionary needed, but none was given!", ret);
            return;
        }
    }

    if (ret == Z_STREAM_END)    {
        env->SetBooleanField(obj, finishedID, true);
    } else if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from inflate()!" : stream->msg, ret);
        return;
    }

    env->SetIntField(obj, readBytesID,    srcSize - stream->avail_in);
    env->SetIntField(obj, writtenBytesID, dstSize - stream->avail_out);
}

__attribute__((visibility("default"))) jboolean JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_doFinish
        (JNIEnv* env, jobject obj, jlong srcAddr, jint srcSize, jlong dstAddr, jint dstSize, jlong dictAddr, jint dictSize)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);

    //set stream buffers
    stream->next_in = (unsigned char*) srcAddr;
    stream->avail_in = srcSize;

    stream->next_out = (unsigned char*) dstAddr;
    stream->avail_out = dstSize;

    int ret = zng_inflate(stream, Z_FINISH);
    if (ret == Z_NEED_DICT)  {
        if (dictAddr)    {
            //set dictionary
            ret = zng_inflateSetDictionary(stream, (unsigned char*) dictAddr, dictSize);
            if (ret != Z_OK)    {
                throwException(env, stream->msg == nullptr ? "Couldn't set inflater dictionary!" : stream->msg, ret);
                return false;
            }

            //try again
            ret = zng_inflate(stream, Z_FINISH);
        } else {
            throwException(env, "Dictionary needed, but none was given!", ret);
            return false;
        }
    }

    if (ret != Z_STREAM_END && ret != Z_OK && ret != Z_BUF_ERROR)    {
        throwException(env, stream->msg == nullptr ? "Invalid return value from inflate()!" : stream->msg, ret);
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

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_doReset
        (JNIEnv* env, jobject obj)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);
    int ret = zng_inflateReset(stream);

    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't reset deflater!" : stream->msg, ret);
        return;
    }

    env->SetBooleanField(obj, resetID, true);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zlib_natives_NativeZlibInflater_doDict
        (JNIEnv* env, jobject obj, jlong dictAddr, jint dictSize)   {
    zng_stream* stream = (zng_stream*) env->GetLongField(obj, ctxID);
    int ret = zng_inflateSetDictionary(stream, (unsigned char*) dictAddr, dictSize);

    if (ret != Z_OK)    {
        throwException(env, stream->msg == nullptr ? "Couldn't set inflater dictionary!" : stream->msg, ret);
        return;
    }
}


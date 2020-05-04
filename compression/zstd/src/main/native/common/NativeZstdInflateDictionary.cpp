#include "pork-zstd.h"

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary
 * Method:    digestD0
 * Signature: (JI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_digestD0
        (JNIEnv* env, jclass cla, jlong dict, jint dictLen)   {
    return (jlong) ZSTD_createDDict((void*) dict, dictLen);
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary
 * Method:    digestH0
 * Signature: ([BII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_digestH0
        (JNIEnv* env, jclass cla, jbyteArray dict, jint dictOff, jint dictLen)   {
    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    jlong ret = (jlong) ZSTD_createDDict(&dictPtr[dictOff], dictLen);
    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);
    return ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary
 * Method:    release0
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_release0
        (JNIEnv* env, jclass cla, jlong dict)   {
    auto ret = ZSTD_freeDDict((ZSTD_DDict*) dict);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary
 * Method:    id0
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_id0
        (JNIEnv* env, jclass cla, jlong dict) {
    return (jint) ZSTD_getDictID_fromDDict((ZSTD_DDict*) dict);
}

}

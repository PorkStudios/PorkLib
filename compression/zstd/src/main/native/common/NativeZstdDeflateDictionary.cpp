#include "pork-zstd.h"

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary
 * Method:    digestD0
 * Signature: (JII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_digestD0
        (JNIEnv* env, jclass cla, jlong dict, jint dictLen, jint level)   {
    return (jlong) ZSTD_createCDict((void*) dict, dictLen, level);
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary
 * Method:    digestH0
 * Signature: ([BIII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_digestH0
        (JNIEnv* env, jclass cla, jbyteArray dict, jint dictOff, jint dictLen, jint level)   {
    auto dictPtr = (unsigned char*) env->GetPrimitiveArrayCritical(dict, nullptr);
    if (!dictPtr)    {
        throwException(env, "Unable to pin dict array");
        return 0;
    }

    jlong ret = (jlong) ZSTD_createCDict(&dictPtr[dictOff], dictLen, level);
    env->ReleasePrimitiveArrayCritical(dict, dictPtr, 0);
    return ret;
}

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary
 * Method:    release0
 * Signature: (J)V
 */
__attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_release0
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeCDict((ZSTD_CDict*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

}

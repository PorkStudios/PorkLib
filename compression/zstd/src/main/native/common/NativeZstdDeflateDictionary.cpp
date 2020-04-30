#include "pork-zstd.h"

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary
 * Method:    digest0
 * Signature: (JII)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_digest0
        (JNIEnv* env, jclass cla, jlong dictAddr, jint dictSize, jint compressionLevel)   {
    return (jlong) ZSTD_createCDict((void*) dictAddr, dictSize, compressionLevel);
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

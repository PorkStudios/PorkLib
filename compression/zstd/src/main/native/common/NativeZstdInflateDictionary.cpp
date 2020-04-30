#include "pork-zstd.h"

extern "C" {

/*
 * Class:     net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary
 * Method:    digest0
 * Signature: (JI)J
 */
__attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_digest0
        (JNIEnv* env, jclass cla, jlong dictAddr, jint dictSize)   {
    return (jlong) ZSTD_createDDict((void*) dictAddr, dictSize);
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

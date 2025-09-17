#include "pork-zstd.h"

#include <porklib_jni_arrays.hpp>

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_digest__J_3BIII(
        JNIEnv* env, jclass,
        jlong dictDirectAddr, jbyteArray dictArray, jint dictArrayOffset, jint dictPosition, jint dictRemaining) noexcept {
    try {
        porklib::jni::AnyReadOnlyByteRegion dict{env, dictDirectAddr, dictArray, dictArrayOffset, dictPosition, dictRemaining};

        return reinterpret_cast<jlong>(ZSTD_createDDict(dict.data(), dict.size()));
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_release0
        (JNIEnv* env, jclass, jlong dict)   {
    auto ret = ZSTD_freeDDict((ZSTD_DDict*) dict);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_id0
        (JNIEnv* env, jclass, jlong dict) {
    return (jint) ZSTD_getDictID_fromDDict((ZSTD_DDict*) dict);
}

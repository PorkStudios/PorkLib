#include "pork-zstd.h"

#include <porklib_jni_arrays.hpp>

extern "C" __attribute__((visibility("default"))) JNIEXPORT jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_digest__J_3BIIII(
        JNIEnv* env, jclass,
        jlong dictDirectAddr, jbyteArray dictArray, jint dictArrayOffset, jint dictPosition, jint dictRemaining,
        jint level) noexcept {
    try {
        porklib::jni::AnyReadOnlyByteRegion dict{env, dictDirectAddr, dictArray, dictArrayOffset, dictPosition, dictRemaining};

        return reinterpret_cast<jlong>(ZSTD_createCDict(dict.data(), dict.size(), level));
    } catch (...) {
        porklib::jni::handleCppExceptionTail(env);
        return 0;
    }
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_release0
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeCDict((ZSTD_CDict*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

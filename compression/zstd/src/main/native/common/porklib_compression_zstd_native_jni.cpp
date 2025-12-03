#include "porklib_compression_zstd_native_jni.hpp"

namespace porklib::compression::zstd::jni {
    static jint OnLoad(JNIEnv* env, porklib::jni::LoadParams params) {
        if (auto result = JniZstdFunctions::OnLoad(env, params); result != JNI_OK) [[unlikely]] return result;

        return JNI_OK;
    }

    static void OnUnload(JNIEnv* env, porklib::jni::UnloadParams params) {
        JniZstdFunctions::OnUnload(env, params);
    }
}

PORKLIB_JNI_REGISTER_LOADERS(porklib_compression_zstd_native_jni, porklib::compression::zstd::jni::OnLoad, porklib::compression::zstd::jni::OnUnload)

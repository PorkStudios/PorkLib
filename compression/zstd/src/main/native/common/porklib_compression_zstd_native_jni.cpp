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

extern "C" __attribute__((visibility("default"))) JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    return porklib::jni::JNI_OnLoad(vm, reserved, "porklib_compression_zstd_native_jni", porklib::compression::zstd::jni::OnLoad);
}

extern "C" __attribute__((visibility("default"))) JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    return porklib::jni::JNI_OnUnload(vm, reserved, "porklib_compression_zstd_native_jni", porklib::compression::zstd::jni::OnUnload);
}
#pragma once

#include <jni.h>

#include <span>

namespace porklib::jni {
    struct _LoadState;

    using LoadParams = _LoadState&;
    using UnloadParams = const char*;

    [[nodiscard]] jint registerNatives(JNIEnv* env, LoadParams params, const char* className, std::span<const JNINativeMethod> methods);

    [[nodiscard, gnu::always_inline]] static inline JNINativeMethod makeJNINativeMethod(const char* name, const char* signature, auto* fnPtr) noexcept {
        return JNINativeMethod{
            .name = const_cast<char*>(name),
            .signature = const_cast<char*>(signature),
            .fnPtr = const_cast<void*>(reinterpret_cast<const void*>(fnPtr)),
        };
    }

    void unregisterNatives(JNIEnv* env, UnloadParams params, const char* className);

    [[nodiscard]] jint JNI_OnLoad(JavaVM* vm, void* reserved, const char* libName, jint (*loadFunction)(JNIEnv*, LoadParams));
    void JNI_OnUnload(JavaVM* vm, void* reserved, const char* libName, void (*unloadFunction)(JNIEnv*, UnloadParams));
}

#define PORKLIB_JNI_REGISTER_LOADERS(LIB_NAME, ONLOAD, ONUNLOAD) \
    extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) { \
        return porklib::jni::JNI_OnLoad(vm, reserved, #LIB_NAME, ONLOAD); \
    } \
    extern "C" JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) { \
        return porklib::jni::JNI_OnUnload(vm, reserved, #LIB_NAME, ONUNLOAD); \
    }
    // We could also add the JNI_OnLoad_##LIB_NAME functions here for when the library is statically linked with the JVM,
    // but since I don't see that happening any time soon I won't worry about it.

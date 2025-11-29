#pragma once

#include <jni.h>

#include <span>
#include <string_view>

namespace porklib::jni {
    using LoadParams = std::string_view;
    using UnloadParams = int;

    [[nodiscard]] jclass findClass(JNIEnv* env, std::string_view packagePrefix, const char* className);
    [[nodiscard]] jint registerNatives(JNIEnv* env, std::string_view packagePrefix, const char* className, std::span<const JNINativeMethod> methods);

    [[nodiscard, gnu::always_inline]] static inline JNINativeMethod makeJNINativeMethod(const char* name, const char* signature, auto* fnPtr) noexcept {
        return JNINativeMethod{
            .name = const_cast<char*>(name),
            .signature = const_cast<char*>(signature),
            .fnPtr = const_cast<void*>(reinterpret_cast<const void*>(fnPtr)),
        };
    }

    [[nodiscard]] jint JNI_OnLoad(JavaVM* vm, void* reserved, const char* libName, jint (*loadFunction)(JNIEnv*, LoadParams));
    void JNI_OnUnload(JavaVM* vm, void* reserved, const char* libName, void (*unloadFunction)(JNIEnv*, UnloadParams));
}

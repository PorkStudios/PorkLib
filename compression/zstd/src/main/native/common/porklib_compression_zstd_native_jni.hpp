#pragma once

#include <jni.h>

#include <porklib_jni_load.hpp>

#define PORKLIB_COMPRESSION_ZSTD_NATIVE_JNI_PACKAGE "net/daporkchop/lib/compression/zstd/natives/"

namespace porklib::compression::zstd::jni::JniZstdFunctions {
    [[nodiscard]] jint OnLoad(JNIEnv* env, porklib::jni::LoadParams params);
    void OnUnload(JNIEnv* env, porklib::jni::UnloadParams params);
}
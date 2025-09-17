#pragma once

#include <jni.h>

#include <exception>
#include <new> // std::bad_alloc

namespace porklib::jni {
    struct AlreadyThrownJniException : std::exception {
        ~AlreadyThrownJniException() override;

        const char* what() const noexcept override;
    };

    [[gnu::cold]] void throwNewJniException(JNIEnv* env, const char* className, const char* msg) noexcept;

    [[gnu::cold]] void handleCppExceptionTail(JNIEnv* env) noexcept;
}

#pragma once

#include <jni.h>

#include <concepts> // std::same_as
#include <exception>

namespace porklib::jni {
    struct AlreadyThrownJniException : std::exception {
        ~AlreadyThrownJniException() override;

        const char* what() const noexcept override;
    };

    [[gnu::cold]] void throwNewJniException(JNIEnv* env, const char* className, const char* msg) noexcept;

    [[gnu::cold]] void handleCppExceptionTail(JNIEnv* env) noexcept;

    template<typename RET>
    [[nodiscard]] inline RET runWithExceptionHandling(JNIEnv* env, auto action) {
        static_assert(std::same_as<RET, decltype(action())>);
        try {
            return action();
        } catch (...) {
            handleCppExceptionTail(env);
            return {};
        }
    }

    template<std::same_as<void> RET>
    [[nodiscard]] inline RET runWithExceptionHandling(JNIEnv* env, auto action) {
        static_assert(std::same_as<RET, decltype(action())>);
        try {
            action();
        } catch (...) {
            handleCppExceptionTail(env);
        }
    }
}

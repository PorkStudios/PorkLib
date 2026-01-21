#pragma once

#include <jni.h>

#include <concepts> // std::same_as
#include <exception>

namespace porklib::jni {
    constexpr static const char* java_lang_AssertionError = "java/lang/AssertionError";
    constexpr static const char* java_lang_Exception = "java/lang/Exception";
    constexpr static const char* java_lang_NullPointerException = "java/lang/NullPointerException";
    constexpr static const char* java_lang_OutOfMemoryError = "java/lang/OutOfMemoryError";

    struct AlreadyThrownJniException : std::exception {
        ~AlreadyThrownJniException() override;

        const char* what() const noexcept override;
    };

    //throw an AlreadyThrownJniException in C++.
    //this assumes that a Java exception has already been thrown.
    [[gnu::cold, noreturn]] void throwAlreadyThrownJniException(JNIEnv* env);

    //throw a new Java exception with the given message, but without throwing a C++ AlreadyThrownJniException.
    [[gnu::cold]] void throwNewJniException(JNIEnv* env, const char* className, const char* msg) noexcept;

    //internal method, called when a C++ exception has been caught at the topmost level of a JNI method.
    //this is done in a separate method for code size reasons, so that the exception handling stuff doesn't have to be duplicated into every method body.
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

#include <porklib_jni_exceptions.hpp>

#include <exception> // std::current_exception(), ...
#include <new> // std::bad_alloc

namespace porklib::jni {
    AlreadyThrownJniException::~AlreadyThrownJniException() = default;

    const char* AlreadyThrownJniException::what() const noexcept {
        return "JNI exception already thrown";
    };

    [[gnu::cold]] void throwNewJniException(JNIEnv* env, const char* className, const char* msg) noexcept {
        if (jclass clazz = env->FindClass(className)) {
            if (env->ThrowNew(clazz, msg) < 0) { //this should never happen
                env->FatalError(msg);
            }
        } else {
            //an exception has already been thrown by env->FindClass()
        }
    }

    [[gnu::cold]] void handleCppExceptionTail(JNIEnv* env) noexcept {
        try {
            if (std::exception_ptr e = std::current_exception()) {
                std::rethrow_exception(e);
            } else {
                //throwNewJniException(env, "java/lang/AssertionError", "porklib::jni::handleCppExceptionTail() was called, but there is no current exception???");
                env->FatalError("porklib::jni::handleCppExceptionTail() was called, but there is no current exception???");
            }
        } catch (const AlreadyThrownJniException&) {
            //no-op, an exception has already been thrown
        } catch (const std::bad_alloc& e) {
            //throw an OutOfMemoryError
            throwNewJniException(env, "java/lang/OutOfMemoryError", e.what());
        } catch (const std::exception& e) {
            //throw an OutOfMemoryError
            throwNewJniException(env, "net/daporkchop/lib/natives/NativeException", e.what());
        //not necessary: this function is already marked noexcept
        //} catch (...) {
        //    env->FatalError("porklib::jni::handleCppExceptionTail() encountered an unknown exception type???");
        }
    }
}

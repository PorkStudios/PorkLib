#include <porklib_jni_load.hpp>

#include <atomic> // std::atomic_flag
#include <cassert>
#include <cstdio> // fflush(), fprintf(), stderr
#include <memory> // std::unique_ptr
#include <new> // std::bad_alloc
#include <stdexcept> // std::runtime_error
#include <string>
#include <utility> // std::exchange()
#include <vector>

#include <porklib_jni_arrays.hpp> // porklib::jni::_detail::configureJniArrays()

namespace porklib::jni {
    constexpr static auto REQUIRED_JNI_VERSION = JNI_VERSION_1_8;

    [[gnu::cold]] static void Load_LogFatal_Void(const char* fmt, auto... args) noexcept __attribute__((format(printf, 1, 2)));
    [[gnu::cold]] static void Load_LogFatal_Void(const char* fmt, auto... args) noexcept {
        fprintf(stderr, fmt, args...);
        fflush(stderr);
    }

    [[nodiscard, gnu::cold]] static jint Load_LogFatal(jint result, const char* fmt, auto... args) noexcept __attribute__((format(printf, 2, 3)));
    [[nodiscard, gnu::cold]] static jint Load_LogFatal(jint result, const char* fmt, auto... args) noexcept {
        fprintf(stderr, fmt, args...);
        fflush(stderr);
        return result;
    }

    [[noreturn, gnu::cold]] static void throwRuntimeError(const char* msg) {
        throw std::runtime_error{msg};
    }

    static std::atomic_flag LIBRARY_LOADED = {};
    static std::string PACKAGE_PREFIX = {};

    struct _LoadState {
        std::vector<jclass> registeredClasses;

        void onLoadSuccess(JNIEnv* env) noexcept {
            //no-op
        }

        void onLoadFailure(JNIEnv* env) noexcept {
            for (jclass clazz : this->registeredClasses) {
                env->UnregisterNatives(clazz);
            }
        }

        ~_LoadState() = default;
    };

    jint registerNatives(JNIEnv* env, _LoadState& state, const char* className, std::span<const JNINativeMethod> methods) {
        jclass clazz = env->FindClass((PACKAGE_PREFIX + className).c_str());
        if (!clazz) [[unlikely]] return JNI_ERR;

        //save the class instance so that we can un-register the natives again if an exception occurs
        state.registeredClasses.push_back(clazz);

        return env->RegisterNatives(clazz, methods.data(), static_cast<jint>(methods.size()));
    }

    void unregisterNatives(JNIEnv* env, const char* packagePrefix, const char* className) {
        jclass clazz = env->FindClass((PACKAGE_PREFIX + className).c_str());
        if (clazz == nullptr) [[unlikely]] {
            Load_LogFatal_Void("FATAL: UNREGISTER: Couldn't find class '%s%s'\n", packagePrefix, className);
        } else if (jint result = env->UnregisterNatives(clazz); result != JNI_OK) [[unlikely]] {
            Load_LogFatal_Void("FATAL: UNREGISTER: Couldn't unregister natives in class '%s%s'\n", packagePrefix, className);
        }
    }

    [[nodiscard]] static std::unique_ptr<char[]> javaStringToCString(JNIEnv* env, jstring str) {
        assert(str != nullptr);

        size_t utfLength = env->GetStringUTFLength(str);
        std::unique_ptr<char[]> result = std::make_unique<char[]>(utfLength + 1);
        env->GetStringUTFRegion(str, 0, env->GetStringLength(str), result.get());

        assert(std::string_view(result.get()).size() == utfLength);

        return result;
    }

    [[nodiscard]] static std::string javaStringToStdString(JNIEnv* env, jstring str) {
        assert(str != nullptr);

        auto deleter = [env, str](const char* utf) { env->ReleaseStringUTFChars(str, utf); };
        std::unique_ptr<const char[], decltype(deleter)> utf{env->GetStringUTFChars(str, nullptr), deleter};

        if (!utf) [[unlikely]] {
            throwRuntimeError("GetStringUTFChars() failed");
        }

        return utf.get();
    }

    [[nodiscard]] static jfieldID getFieldID(JNIEnv* env, jclass clazz, const char* name, const char* sig) {
        jfieldID result = env->GetFieldID(clazz, name, sig);
        if (result == nullptr) [[unlikely]] {
            throwRuntimeError("GetFieldID() failed");
        }
        return result;
    }

    [[nodiscard]] static jobject getLoaderParameterObjectFromJni(JNIEnv* env, const char* prefix, const char* libName) {
        //return System.getProperties().get(prefix + libName);

        jclass class_java_lang_System = env->FindClass("java/lang/System");
        if (class_java_lang_System == nullptr) [[unlikely]] {
            throwRuntimeError("couldn't find class java.lang.System");
        }
        jclass class_java_util_Properties = env->FindClass("java/util/Properties");
        if (class_java_util_Properties == nullptr) [[unlikely]] {
            throwRuntimeError("couldn't find class java.util.Properties");
        }
        jmethodID method_java_lang_System_getProperties = env->GetStaticMethodID(class_java_lang_System, "getProperties", "()Ljava/util/Properties;");
        if (method_java_lang_System_getProperties == nullptr) [[unlikely]] {
            throwRuntimeError("couldn't find method java.lang.System#getProperties()");
        }
        jmethodID method_java_util_Properties_remove = env->GetMethodID(class_java_util_Properties, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
        if (method_java_util_Properties_remove == nullptr) [[unlikely]] {
            throwRuntimeError("couldn't find method java.util.Properties#remove(java.lang.Object)");
        }
        jobject obj_System_getProperties = env->CallStaticObjectMethod(class_java_lang_System, method_java_lang_System_getProperties);
        if (env->ExceptionCheck()) [[unlikely]] {
            throwRuntimeError("method java.lang.System#getProperties() threw an exception");
        } else if (obj_System_getProperties == nullptr) [[unlikely]] {
            throwRuntimeError("java.lang.System#getProperties() returned null");
        }

        jstring obj_key = env->NewStringUTF((std::string{prefix} + libName).c_str());
        if (obj_key == nullptr) [[unlikely]] {
            throwRuntimeError("couldn't construct key");
        }

        jobject result = env->CallObjectMethod(obj_System_getProperties, method_java_util_Properties_remove, obj_key);
        if (env->ExceptionCheck()) [[unlikely]] {
            throwRuntimeError("method java.util.Properties#remove(java.lang.Object) threw an exception");
        } else if (result == nullptr) [[unlikely]] {
            throwRuntimeError("couldn't find loader parameter");
        }

        return result;
    }

    [[nodiscard]] static jobject getLibraryConfig(JNIEnv* env, const char* libName) {
        //return System.getProperties().get("porklib_natives_load_config#" + libName);
        return getLoaderParameterObjectFromJni(env, "porklib_natives_load_config#", libName);
    }

    jint JNI_OnLoad(JavaVM* vm, void* reserved, const char* libName, jint (*loadFunction)(JNIEnv*, LoadParams)) {
        JNIEnv* env = nullptr;
        if (jint result = vm->GetEnv(reinterpret_cast<void**>(&env), REQUIRED_JNI_VERSION); result != JNI_OK) [[unlikely]] {
            return Load_LogFatal(result, "FATAL: JNI version mismatch\n");
        }

        try {
            //atomically set the LIBRARY_LOADED flag
            if (LIBRARY_LOADED.test_and_set()) [[unlikely]] {
                throwRuntimeError("JNI library is already loaded!");
            }

            //get library config from java object
            jobject obj_libraryConfig = getLibraryConfig(env, libName);
            jclass cla_libraryConfig = env->GetObjectClass(obj_libraryConfig);

            //get JNI arrays config
            porklib::jni::_detail::configureJniArrays({
                .allowJniCriticalRead = env->GetBooleanField(obj_libraryConfig, getFieldID(env, cla_libraryConfig, "allowJniCriticalRead", "Z")),
                .allowJniCriticalWrite = env->GetBooleanField(obj_libraryConfig, getFieldID(env, cla_libraryConfig, "allowJniCriticalWrite", "Z")),

                .allowJniGetElementsRead = env->GetBooleanField(obj_libraryConfig, getFieldID(env, cla_libraryConfig, "allowJniGetElementsRead", "Z")),
                .allowJniGetElementsWrite = env->GetBooleanField(obj_libraryConfig, getFieldID(env, cla_libraryConfig, "allowJniGetElementsWrite", "Z")),
            });

            //get the package prefix string from JNI, and copy it into a C++ string
            //also save it in a global variable so that we can access it from JNI_OnUnload()
            jstring obj_packagePrefix = reinterpret_cast<jstring>(env->GetObjectField(obj_libraryConfig, getFieldID(env, cla_libraryConfig, "packagePrefix", "Ljava/lang/String;")));
            if (obj_packagePrefix == nullptr) [[unlikely]] throwRuntimeError("package prefix is null!");
            PACKAGE_PREFIX = javaStringToStdString(env, obj_packagePrefix);

            //actually invoke the load function
            _LoadState loadState{
                .registeredClasses = {}
            };
            try {
                if (jint result = loadFunction(env, loadState); result != JNI_OK) [[unlikely]] {
                    //if anything goes wrong, we should unload any native methods which were previously registered to avoid leaking memory or whatever
                    loadState.onLoadFailure(env);
                    return result;
                }

                //notify the load state object that the library was loaded successfully
                loadState.onLoadSuccess(env);

                return REQUIRED_JNI_VERSION;
            } catch (...) {
                //if anything goes wrong, we should unload any native methods which were previously registered to avoid leaking memory or whatever
                loadState.onLoadFailure(env);
                throw;
            }
        } catch (const std::bad_alloc&) {
            return JNI_ENOMEM;
        } catch (const std::exception& e) {
            fprintf(stderr, "FATAL: while loading JNI library '%s': %s\n", libName, e.what());
            fflush(stderr);
            return JNI_ERR;
        }
    }

    void JNI_OnUnload(JavaVM* vm, void* reserved, const char* libName, void (*unloadFunction)(JNIEnv*, UnloadParams)) {
        assert(LIBRARY_LOADED.test());

        struct Cleaner {
            const char* libName;

            ~Cleaner() {
                fprintf(stderr, "JNI_OnUnload() called for '%s'\n", libName);
                fflush(stderr);

                PACKAGE_PREFIX = std::string{};
                LIBRARY_LOADED.clear();
            }
        } cleaner = { libName };

        JNIEnv* env = nullptr;
        if (jint result = vm->GetEnv(reinterpret_cast<void**>(&env), REQUIRED_JNI_VERSION); result != JNI_OK) [[unlikely]] {
            return;
        }

        unloadFunction(env, PACKAGE_PREFIX.c_str());
    }
}

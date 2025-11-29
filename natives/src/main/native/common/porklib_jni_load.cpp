#include <porklib_jni_load.hpp>

#include <atomic> // std::atomic_flag
#include <cstdio> // fflush(), fprintf(), stderr
#include <memory> // std::unique_ptr
#include <new> // std::bad_alloc
#include <string>
#include <utility> // std::exchange()
#include <vector>

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

    static std::atomic_flag LIBRARY_LOADED = {};
    static std::vector<std::string> REGISTERED_CLASS_NAMES;

    jclass findClass(JNIEnv* env, std::string_view packagePrefix, const char* className) {
        if (packagePrefix.empty()) {
            return env->FindClass(className);
        } else {
            return env->FindClass((std::string{packagePrefix} + className).c_str());
        }
    }

    jint registerNatives(JNIEnv* env, std::string_view packagePrefix, const char* className, std::span<const JNINativeMethod> methods) {
        std::string& realClassName = REGISTERED_CLASS_NAMES.emplace_back(std::string{packagePrefix} + className);

        jclass clazz = env->FindClass(realClassName.c_str());
        if (!clazz) [[unlikely]] return JNI_ERR;

        return env->RegisterNatives(clazz, methods.data(), static_cast<jint>(methods.size()));
    }

    [[gnu::cold]] static void unregisterNativesAndClearLoadedFlag(JNIEnv* env) noexcept {
        for (auto& className : std::exchange(REGISTERED_CLASS_NAMES, {})) {
            jclass clazz = env->FindClass(className.c_str());
            if (clazz == nullptr) [[unlikely]] {
                Load_LogFatal_Void("FATAL: UNREGISTER: Couldn't find class '%s'\n", className.c_str());
            } else if (jint result = env->UnregisterNatives(clazz); result != JNI_OK) [[unlikely]] {
                Load_LogFatal_Void("FATAL: UNREGISTER: Couldn't unregister natives in class '%s'\n", className.c_str());
            }
        }

        LIBRARY_LOADED.clear();
    }

    jint JNI_OnLoad(JavaVM* vm, void* reserved, const char* libName, jint (*loadFunction)(JNIEnv*, LoadParams)) {
        try {
            JNIEnv* env = nullptr;
            if (jint result = vm->GetEnv(reinterpret_cast<void**>(&env), REQUIRED_JNI_VERSION); result != JNI_OK) [[unlikely]] {
                return Load_LogFatal(result, "FATAL: JNI version mismatch\n");
            }

            //
            // String packagePrefix = System.getProperties().get("porklib_natives_load_packagePrefix#" + libName);
            //

            jclass class_java_lang_System = env->FindClass("java/lang/System");
            if (class_java_lang_System == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Couldn't find class java.lang.System\n");
            }
            jclass class_java_util_Properties = env->FindClass("java/util/Properties");
            if (class_java_util_Properties == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Couldn't find class java.util.Properties\n");
            }
            jmethodID method_java_lang_System_getProperties = env->GetStaticMethodID(class_java_lang_System, "getProperties", "()Ljava/util/Properties;");
            if (method_java_lang_System_getProperties == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Couldn't find method java.lang.System#getProperties()\n");
            }
            jmethodID method_java_util_Properties_remove = env->GetMethodID(class_java_util_Properties, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
            if (method_java_util_Properties_remove == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Couldn't find method java.util.Properties#remove(java.lang.Object)\n");
            }
            jobject obj_System_getProperties = env->CallStaticObjectMethod(class_java_lang_System, method_java_lang_System_getProperties);
            if (env->ExceptionCheck()) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Method java.lang.System#getProperties() threw an exception\n");
            } else if (obj_System_getProperties == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: java.lang.System#getProperties() returned null\n");
            }

            jstring obj_packagePrefix_key = env->NewStringUTF((std::string{"porklib_natives_load_packagePrefix#"} + libName).c_str());
            if (obj_packagePrefix_key == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Couldn't construct package prefix key\n");
            }

            jstring obj_packagePrefix = reinterpret_cast<jstring>(env->CallObjectMethod(obj_System_getProperties, method_java_util_Properties_remove, obj_packagePrefix_key));
            if (env->ExceptionCheck()) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Method java.util.Properties#remove(java.lang.Object) threw an exception\n");
            } else if (obj_packagePrefix == nullptr) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Couldn't find package prefix for library name '%s'\n", libName);
            }

            auto ptr_packagePrefix = std::make_unique<char[]>(env->GetStringUTFLength(obj_packagePrefix) + 1);
            env->GetStringUTFRegion(obj_packagePrefix, 0, env->GetStringLength(obj_packagePrefix), ptr_packagePrefix.get());
            if (env->ExceptionCheck()) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: Failed to copy package prefix to native heap\n");
            }
            std::string_view packagePrefix{ptr_packagePrefix.get()};

            //atomically set LIBRARY_LOADED to true, and fail if it was already set
            if (LIBRARY_LOADED.test_and_set()) [[unlikely]] {
                return Load_LogFatal(JNI_ERR, "FATAL: LIBRARY_LOADED flag was already set for '%s'\n", libName);
            }

            //actually invoke the load function
            try {
                if (jint result = loadFunction(env, packagePrefix); result != JNI_OK) [[unlikely]] {
                    //if anything goes wrong, we should unload any native methods which were previously registered to avoid leaking memory or whatever
                    unregisterNativesAndClearLoadedFlag(env);
                    return result;
                }

                return REQUIRED_JNI_VERSION;
            } catch (...) {
                //if anything goes wrong, we should unload any native methods which were previously registered to avoid leaking memory or whatever
                unregisterNativesAndClearLoadedFlag(env);
                throw;
            }
        } catch (const std::bad_alloc&) {
            return JNI_ENOMEM;
        }
    }

    void JNI_OnUnload(JavaVM* vm, void* reserved, const char* libName, void (*unloadFunction)(JNIEnv*, UnloadParams)) {
        JNIEnv* env = nullptr;
        if (jint result = vm->GetEnv(reinterpret_cast<void**>(&env), REQUIRED_JNI_VERSION); result != JNI_OK) [[unlikely]] {
            return;
        }

        if (LIBRARY_LOADED.test()) {
            unregisterNativesAndClearLoadedFlag(env);

            unloadFunction(env, {});
        }
    }
}

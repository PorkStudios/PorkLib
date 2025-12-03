#include "pork-zstd.h"
#include "porklib_compression_zstd_native_jni.hpp"

#include <porklib_jni_arrays.hpp>
#include <porklib_jni_exceptions.hpp>

namespace porklib::compression::zstd::jni::JniZstdFunctions {
    static jboolean JNICALL isError(JNIEnv* env, jobject, jlong code) {
        return ZSTD_isError(static_cast<size_t>(code));
    }

    static jint JNICALL getErrorCode(JNIEnv* env, jobject, jlong code) {
        return ZSTD_getErrorCode(static_cast<size_t>(code));
    }

    static jstring JNICALL getErrorName(JNIEnv* env, jobject, jlong code) {
        const char* name = ZSTD_getErrorName(static_cast<size_t>(code));
        return env->NewStringUTF(name);
    }

    static jlong JNICALL createCCtx(JNIEnv* env, jobject) {
        return reinterpret_cast<jlong>(ZSTD_createCCtx());
    }

    static void JNICALL freeCCtx(JNIEnv* env, jobject, jlong _cctx) {
        auto* cctx = reinterpret_cast<ZSTD_CCtx*>(_cctx);
        ZSTD_freeCCtx(cctx);
    }

    static jlong JNICALL CCtx_refCDict(JNIEnv* env, jobject, jlong _cctx, jlong _cdict) {
        auto* cctx = reinterpret_cast<ZSTD_CCtx*>(_cctx);
        auto* cdict = reinterpret_cast<ZSTD_CDict*>(_cdict);
        return static_cast<jlong>(ZSTD_CCtx_refCDict(cctx, cdict));
    }

    static jlong JNICALL CCtx_reset(JNIEnv* env, jobject, jlong _cctx, jint _reset) {
        auto* cctx = reinterpret_cast<ZSTD_CCtx*>(_cctx);
        auto reset = static_cast<ZSTD_ResetDirective>(_reset);
        return static_cast<jlong>(ZSTD_CCtx_reset(cctx, reset));
    }

    static jlong JNICALL CCtx_setParameter(JNIEnv* env, jobject, jlong _cctx, jint _param, jint value) {
        auto* cctx = reinterpret_cast<ZSTD_CCtx*>(_cctx);
        auto param = static_cast<ZSTD_cParameter>(_param);
        return static_cast<jlong>(ZSTD_CCtx_setParameter(cctx, param, value));
    }

    static jlong JNICALL compress2(JNIEnv* env, jobject,
            jlong _cctx,
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(src),
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(dst)) {
        return porklib::jni::runWithExceptionHandling<jlong>(env, [=]() {
            auto* cctx = reinterpret_cast<ZSTD_CCtx*>(_cctx);

            porklib::jni::AnyReadOnlyByteRegion src{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(src)};
            porklib::jni::AnyWriteOnlyByteRegion dst{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(dst)};

            size_t result = ZSTD_compress2(cctx, dst.data(), dst.size(), src.data(), src.size());

            dst.setDirtyCount(ZSTD_isError(result) ? 0 : result);
            return static_cast<jlong>(result);
        });
    }

    static jlong JNICALL createCDict(JNIEnv* env, jobject,
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(dict),
            jint level) {
        return porklib::jni::runWithExceptionHandling<jlong>(env, [=]() {
            porklib::jni::AnyReadOnlyByteRegion dict{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(dict)};

            return reinterpret_cast<jlong>(ZSTD_createCDict(dict.data(), dict.size(), level));
        });
    }

    static void JNICALL freeCDict(JNIEnv* env, jobject, jlong _cdict) {
        auto* cdict = reinterpret_cast<ZSTD_CDict*>(_cdict);
        ZSTD_freeCDict(cdict);
    }

    //TODO: update ZSTD library
    /*static jint JNICALL getDictID_fromCDict(JNIEnv* env, jobject, jlong _cdict) {
        auto* cdict = reinterpret_cast<const ZSTD_CDict*>(_cdict);
        return ZSTD_getDictID_fromCDict(cdict);
    }*/

    static jlong JNICALL createDCtx(JNIEnv* env, jobject) {
        return reinterpret_cast<jlong>(ZSTD_createDCtx());
    }

    static void JNICALL freeDCtx(JNIEnv* env, jobject, jlong _dctx) {
        auto* dctx = reinterpret_cast<ZSTD_DCtx*>(_dctx);
        ZSTD_freeDCtx(dctx);
    }

    static jlong JNICALL DCtx_refDDict(JNIEnv* env, jobject, jlong _dctx, jlong _ddict) {
        auto* dctx = reinterpret_cast<ZSTD_DCtx*>(_dctx);
        auto* ddict = reinterpret_cast<ZSTD_DDict*>(_ddict);
        return static_cast<jlong>(ZSTD_DCtx_refDDict(dctx, ddict));
    }

    static jlong JNICALL DCtx_reset(JNIEnv* env, jobject, jlong _dctx, jint _reset) {
        auto* dctx = reinterpret_cast<ZSTD_DCtx*>(_dctx);
        auto reset = static_cast<ZSTD_ResetDirective>(_reset);
        return static_cast<jlong>(ZSTD_DCtx_reset(dctx, reset));
    }

    static jlong JNICALL DCtx_setParameter(JNIEnv* env, jobject, jlong _dctx, jint _param, jint value) {
        auto* dctx = reinterpret_cast<ZSTD_DCtx*>(_dctx);
        auto param = static_cast<ZSTD_dParameter>(_param);
        return static_cast<jlong>(ZSTD_DCtx_setParameter(dctx, param, value));
    }

    static jlong JNICALL decompressDCtx(JNIEnv* env, jobject,
            jlong _dctx,
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(src),
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(dst)) {
        return porklib::jni::runWithExceptionHandling<jlong>(env, [=]() {
            auto* dctx = reinterpret_cast<ZSTD_DCtx*>(_dctx);

            porklib::jni::AnyReadOnlyByteRegion src{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(src)};
            porklib::jni::AnyWriteOnlyByteRegion dst{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(dst)};

            size_t result = ZSTD_decompressDCtx(dctx, dst.data(), dst.size(), src.data(), src.size());

            dst.setDirtyCount(ZSTD_isError(result) ? 0 : result);
            return static_cast<jlong>(result);
        });
    }

    static jlong JNICALL createDDict(JNIEnv* env, jobject,
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(dict)) {
        return porklib::jni::runWithExceptionHandling<jlong>(env, [=]() {
            porklib::jni::AnyReadOnlyByteRegion dict{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(dict)};

            return reinterpret_cast<jlong>(ZSTD_createDDict(dict.data(), dict.size()));
        });
    }

    static void JNICALL freeDDict(JNIEnv* env, jobject, jlong _ddict) {
        auto* ddict = reinterpret_cast<ZSTD_DDict*>(_ddict);
        ZSTD_freeDDict(ddict);
    }

    //TODO: update ZSTD library
    /*static jint JNICALL getDictID_fromDDict(JNIEnv* env, jobject, jlong _ddict) {
        auto* ddict = reinterpret_cast<const ZSTD_DDict*>(_ddict);
        return ZSTD_getDictID_fromDDict(ddict);
    }*/

    jint OnLoad(JNIEnv* env, porklib::jni::LoadParams params) {
        return porklib::jni::registerNatives(env, params, PORKLIB_COMPRESSION_ZSTD_NATIVE_JNI_PACKAGE "JniZstdFunctions", {{
            porklib::jni::makeJNINativeMethod("ZSTD_isError", "(J)Z", JniZstdFunctions::isError),
            porklib::jni::makeJNINativeMethod("ZSTD_getErrorCode", "(J)I", JniZstdFunctions::getErrorCode),
            porklib::jni::makeJNINativeMethod("ZSTD_getErrorName", "(J)Ljava/lang/String;", JniZstdFunctions::getErrorName),

            porklib::jni::makeJNINativeMethod("ZSTD_createCCtx", "()J", JniZstdFunctions::createCCtx),
            porklib::jni::makeJNINativeMethod("ZSTD_freeCCtx", "(J)V", JniZstdFunctions::freeCCtx),
            porklib::jni::makeJNINativeMethod("ZSTD_CCtx_refCDict", "(JJ)J", JniZstdFunctions::CCtx_refCDict),
            porklib::jni::makeJNINativeMethod("ZSTD_CCtx_reset", "(JI)J", JniZstdFunctions::CCtx_reset),
            porklib::jni::makeJNINativeMethod("ZSTD_CCtx_setParameter", "(JII)J", JniZstdFunctions::CCtx_setParameter),
            porklib::jni::makeJNINativeMethod("ZSTD_compress2", "(J" PORKLIB_JNI_BYTEREGION_SIG PORKLIB_JNI_BYTEREGION_SIG ")J", JniZstdFunctions::compress2),

            porklib::jni::makeJNINativeMethod("ZSTD_createCDict", "(" PORKLIB_JNI_BYTEREGION_SIG "I)J", JniZstdFunctions::createCDict),
            porklib::jni::makeJNINativeMethod("ZSTD_freeCDict", "(J)V", JniZstdFunctions::freeCDict),
            //porklib::jni::makeJNINativeMethod("ZSTD_getDictID_fromCDict", "(J)I", JniZstdFunctions::getDictID_fromCDict),

            porklib::jni::makeJNINativeMethod("ZSTD_createDCtx", "()J", JniZstdFunctions::createDCtx),
            porklib::jni::makeJNINativeMethod("ZSTD_freeDCtx", "(J)V", JniZstdFunctions::freeDCtx),
            porklib::jni::makeJNINativeMethod("ZSTD_DCtx_refDDict", "(JJ)J", JniZstdFunctions::DCtx_refDDict),
            porklib::jni::makeJNINativeMethod("ZSTD_DCtx_reset", "(JI)J", JniZstdFunctions::DCtx_reset),
            porklib::jni::makeJNINativeMethod("ZSTD_DCtx_setParameter", "(JII)J", JniZstdFunctions::DCtx_setParameter),
            porklib::jni::makeJNINativeMethod("ZSTD_decompressDCtx", "(J" PORKLIB_JNI_BYTEREGION_SIG PORKLIB_JNI_BYTEREGION_SIG ")J", JniZstdFunctions::decompressDCtx),

            porklib::jni::makeJNINativeMethod("ZSTD_createDDict", "(" PORKLIB_JNI_BYTEREGION_SIG ")J", JniZstdFunctions::createDDict),
            porklib::jni::makeJNINativeMethod("ZSTD_freeDDict", "(J)V", JniZstdFunctions::freeDDict),
            //porklib::jni::makeJNINativeMethod("ZSTD_getDictID_fromDDict", "(J)I", JniZstdFunctions::getDictID_fromDDict),
        }});
    }

    void OnUnload(JNIEnv* env, porklib::jni::UnloadParams params) {
        porklib::jni::unregisterNatives(env, params, PORKLIB_COMPRESSION_ZSTD_NATIVE_JNI_PACKAGE "JniZstdFunctions");
    }
}

#include <jni.h>

#include <porklib_jni_arrays.hpp>
#include <porklib_jni_exceptions.hpp>
#include <porklib_jni_load.hpp>

#include <libdeflate.h>

#define PORKLIB_COMPRESSION_DEFLATE_LIBDEFLATE_PACKAGE "net/daporkchop/lib/compression/deflate/libdeflate/"

namespace porklib::compression::deflate::libdeflate::JniLibdeflateFunctions {
    constexpr static jbyte MODE_DEFLATE = 1;
    constexpr static jbyte MODE_ZLIB = 2;
    constexpr static jbyte MODE_GZIP = 3;

    static jlong JNICALL alloc_compressor(JNIEnv* env, jobject, jint compression_level) {
        return reinterpret_cast<jlong>(libdeflate_alloc_compressor(compression_level));
    }

    static void JNICALL free_compressor(JNIEnv* env, jobject, jlong _compressor) {
        auto* compressor = reinterpret_cast<libdeflate_compressor*>(_compressor);
        libdeflate_free_compressor(compressor);
    }

    static jlong JNICALL deflate_compress_bound(JNIEnv* env, jobject, jlong _compressor, jlong in_nbytes) {
        auto* compressor = reinterpret_cast<libdeflate_compressor*>(_compressor);
        return static_cast<jlong>(libdeflate_deflate_compress_bound(compressor, in_nbytes));
    }

    static jlong JNICALL zlib_compress_bound(JNIEnv* env, jobject, jlong _compressor, jlong in_nbytes) {
        auto* compressor = reinterpret_cast<libdeflate_compressor*>(_compressor);
        return static_cast<jlong>(libdeflate_zlib_compress_bound(compressor, in_nbytes));
    }

    static jlong JNICALL gzip_compress_bound(JNIEnv* env, jobject, jlong _compressor, jlong in_nbytes) {
        auto* compressor = reinterpret_cast<libdeflate_compressor*>(_compressor);
        return static_cast<jlong>(libdeflate_gzip_compress_bound(compressor, in_nbytes));
    }

    static jlong JNICALL compress(JNIEnv* env, jobject,
            jlong _compressor,
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(src),
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(dst),
            jbyte mode) {
        return porklib::jni::runWithExceptionHandling<jlong>(env, [=]() {
            auto* compressor = reinterpret_cast<libdeflate_compressor*>(_compressor);

            porklib::jni::AnyReadOnlyByteRegion src{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(src)};
            porklib::jni::AnyWriteOnlyByteRegion dst{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(dst), porklib::jni::NothingDirtyTag{}};

            size_t result;
            switch (mode) {
                case MODE_DEFLATE:
                    result = libdeflate_deflate_compress(compressor, src.data(), src.size(), dst.data(), dst.size());
                    break;
                case MODE_ZLIB:
                    result = libdeflate_zlib_compress(compressor, src.data(), src.size(), dst.data(), dst.size());
                    break;
                case MODE_GZIP:
                    result = libdeflate_gzip_compress(compressor, src.data(), src.size(), dst.data(), dst.size());
                    break;
                default:
                    __builtin_unreachable();
            }

            dst.setDirtyCount(result);
            return static_cast<jlong>(result);
        });
    }

    static jlong JNICALL alloc_decompressor(JNIEnv* env, jobject) {
        return reinterpret_cast<jlong>(libdeflate_alloc_decompressor());
    }

    static void JNICALL free_decompressor(JNIEnv* env, jobject, jlong _decompressor) {
        auto* decompressor = reinterpret_cast<libdeflate_decompressor*>(_decompressor);
        libdeflate_free_decompressor(decompressor);
    }

    static jint JNICALL decompress(JNIEnv* env, jobject,
            jlong _decompressor,
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(src),
            PORKLIB_JNI_BYTEREGION_ARGS_DECL(dst),
            jbyte mode, jlongArray nbytesArray) {
        return porklib::jni::runWithExceptionHandling<jint>(env, [=]() {
            auto* decompressor = reinterpret_cast<libdeflate_decompressor*>(_decompressor);

            porklib::jni::AnyReadOnlyByteRegion src{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(src)};
            porklib::jni::AnyWriteOnlyByteRegion dst{env, PORKLIB_JNI_BYTEREGION_ARGS_USE(dst), porklib::jni::NothingDirtyTag{}};

            size_t actual_in_nbytes_ret = 0;
            size_t actual_out_nbytes_ret = 0;

            libdeflate_result result;
            switch (mode) {
                case MODE_DEFLATE:
                    result = libdeflate_deflate_decompress_ex(decompressor, src.data(), src.size(), dst.data(), dst.size(), &actual_in_nbytes_ret, &actual_out_nbytes_ret);
                    break;
                case MODE_ZLIB:
                    result = libdeflate_zlib_decompress_ex(decompressor, src.data(), src.size(), dst.data(), dst.size(), &actual_in_nbytes_ret, &actual_out_nbytes_ret);
                    break;
                case MODE_GZIP:
                    result = libdeflate_gzip_decompress_ex(decompressor, src.data(), src.size(), dst.data(), dst.size(), &actual_in_nbytes_ret, &actual_out_nbytes_ret);
                    break;
                default:
                    __builtin_unreachable();
            }

            dst.setDirtyCount(actual_out_nbytes_ret);

            jlong nbytesTmp[2] = {
                static_cast<jlong>(actual_in_nbytes_ret),
                static_cast<jlong>(actual_out_nbytes_ret),
            };
            env->SetLongArrayRegion(nbytesArray, 0, 2, nbytesTmp);

            return static_cast<jint>(result);
        });
    }
}

namespace porklib::compression::deflate::libdeflate {
    static jint OnLoad(JNIEnv* env, porklib::jni::LoadParams params) {
        return porklib::jni::registerNatives(env, params, PORKLIB_COMPRESSION_DEFLATE_LIBDEFLATE_PACKAGE "JniLibdeflateFunctions", {{
            porklib::jni::makeJNINativeMethod("libdeflate_alloc_compressor", "(I)J", JniLibdeflateFunctions::alloc_compressor),
            porklib::jni::makeJNINativeMethod("libdeflate_free_compressor", "(J)V", JniLibdeflateFunctions::free_compressor),
            porklib::jni::makeJNINativeMethod("libdeflate_deflate_compress_bound", "(JJ)J", JniLibdeflateFunctions::deflate_compress_bound),
            porklib::jni::makeJNINativeMethod("libdeflate_zlib_compress_bound", "(JJ)J", JniLibdeflateFunctions::zlib_compress_bound),
            porklib::jni::makeJNINativeMethod("libdeflate_gzip_compress_bound", "(JJ)J", JniLibdeflateFunctions::gzip_compress_bound),

            porklib::jni::makeJNINativeMethod("libdeflate_compress", "(J" PORKLIB_JNI_BYTEREGION_SIG PORKLIB_JNI_BYTEREGION_SIG "B)J", JniLibdeflateFunctions::compress),

            porklib::jni::makeJNINativeMethod("libdeflate_alloc_decompressor", "()J", JniLibdeflateFunctions::alloc_decompressor),
            porklib::jni::makeJNINativeMethod("libdeflate_free_decompressor", "(J)V", JniLibdeflateFunctions::free_decompressor),

            porklib::jni::makeJNINativeMethod("libdeflate_decompress", "(J" PORKLIB_JNI_BYTEREGION_SIG PORKLIB_JNI_BYTEREGION_SIG "B[J)I", JniLibdeflateFunctions::decompress),
        }});
    }

    static void OnUnload(JNIEnv* env, porklib::jni::UnloadParams params) {
        porklib::jni::unregisterNatives(env, params, PORKLIB_COMPRESSION_DEFLATE_LIBDEFLATE_PACKAGE "JniLibdeflateFunctions");
    }
}

PORKLIB_JNI_REGISTER_LOADERS(porklib_compression_deflate_libdeflate, porklib::compression::deflate::libdeflate::OnLoad, porklib::compression::deflate::libdeflate::OnUnload)

#pragma once

#include <jni.h>

#include "porklib_jni_exceptions.hpp"
#include "porklib_noinit_vector.hpp"

#include <cassert>
#include <memory> // std::allocator
#include <span>
#include <type_traits>
#include <optional>
#include <vector>

namespace porklib::jni {
    namespace _detail {
        struct JniArraysConfig {
            jboolean allowJniCriticalRead = false;
            jboolean allowJniCriticalWrite = false;

            jboolean allowJniGetElementsRead = false;
            jboolean allowJniGetElementsWrite = false;
        };

        //TODO: this is never used
        constinit inline JniArraysConfig ARRAYS_CONFIG = {};

        inline void configureJniArrays(const JniArraysConfig& config) noexcept {
            ARRAYS_CONFIG = config;
        }

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsCriticalRead(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            //TODO: implement this
            return nullptr;

            return reinterpret_cast<jbyte*>(env->GetPrimitiveArrayCritical(array, nullptr));
        }

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsCriticalWrite(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            //TODO: implement this
            return nullptr;

            return reinterpret_cast<jbyte*>(env->GetPrimitiveArrayCritical(array, nullptr));
        }

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsRead(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            //we never use Get*ArrayElements(): as of this writing (Sep. 2025), there aren't any GC implementations on any Java version
            //  which ever pin arrays here. Shenandoah pins the array when using GetPrimitiveArrayCritical() since Java 15, but the performance
            //  impact of that function on on other GCs is bad enough that it isn't really worth the added complexity.
            return nullptr;

            if (jsize arrayLength = env->GetArrayLength(array); arrayLength - remaining >= arrayLength / 8) {
                //we're accessing less than 7/8 of the total array, don't use GetArrayElements()
                return nullptr;
            }

            //use GetArrayElements(), the array won't be pinned if supported
            return env->GetByteArrayElements(array, nullptr);
        }

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsWrite(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            //TODO: if GetByteArrayElements() returns a copy and we aren't trying to access the entire array, we should abort so that
            //  committing changes doesn't overwrite changes made to other parts of the array
            return tryGetByteArrayElementsCriticalRead(env, array, remaining);
        }
    }

    // NOTE: AnyReadOnlyByteRegion and AnyWriteOnlyByteRegion are only safe to use if no AnyWriteOnlyByteRegion
    //       aliases any other Any*ByteRegion

    class AnyReadOnlyByteRegion {
        [[no_unique_address]] std::allocator<jbyte> _alloc = {};

        const jbyte* _data;
        size_t const _size;

        enum {
            k_Direct,
            k_Copy,
            k_GetElementsCritical,
            k_GetElements,
        } _kind;

        JNIEnv* const _env;
        jbyteArray const _array;
        jbyte* _elems;

    public:
        AnyReadOnlyByteRegion(JNIEnv* env, jlong directAddress, jbyteArray array, jsize arrayOffset, jsize position, jsize remaining)
              : _size{static_cast<size_t>(remaining)},
                _env{env},
                _array{array} {
            assert(remaining == 0 || (directAddress != 0) != (array != nullptr));
            assert(position >= 0 && remaining >= 0);

            if (array == nullptr) {
                //use the direct address
                _kind = k_Direct;
                _data = reinterpret_cast<const jbyte*>(directAddress) + position;
            } else if (jbyte* elems = _detail::tryGetByteArrayElementsCriticalWrite(env, array, remaining)) {
                _kind = k_GetElementsCritical;
                _elems = elems;
                _data = elems + arrayOffset + position;
            } else if (jbyte* elems = _detail::tryGetByteArrayElementsWrite(env, array, remaining)) {
                _kind = k_GetElements;
                _elems = elems;
                _data = elems + arrayOffset + position;
            } else {
                //allocate a buffer and then copy the elements into it
                _kind = k_Copy;
                _data = _alloc.allocate(remaining);

                env->GetByteArrayRegion(array, arrayOffset + position, remaining, const_cast<jbyte*>(_data));
            }
        }

        AnyReadOnlyByteRegion() = delete;
        AnyReadOnlyByteRegion(const AnyReadOnlyByteRegion&) = delete;

        ~AnyReadOnlyByteRegion() {
            switch (_kind) {
                case k_Direct:
                    break;
                case k_Copy:
                    _alloc.deallocate(const_cast<jbyte*>(_data), _size);
                    break;
                case k_GetElementsCritical:
                    _env->ReleasePrimitiveArrayCritical(_array, _elems, JNI_ABORT);
                    break;
                case k_GetElements:
                    _env->ReleaseByteArrayElements(_array, _elems, JNI_ABORT);
                    break;
            }
        }

        const jbyte* data() const noexcept { return _data; }
        size_t size() const noexcept { return _size; }

        const jbyte* begin() const noexcept { return _data; }
        const jbyte* end() const noexcept { return _data + _size; }
    };

    struct CommitNothingTag {};
    struct CommitEverythingTag {};

    class AnyWriteOnlyByteRegion {
        [[no_unique_address]] std::allocator<jbyte> _alloc = {};

        jbyte* _data;
        size_t const _size;
        size_t _dirtyCount;

        JNIEnv* const _env;
        jbyteArray const _array;
        jbyte* _elems;
        jsize const _arrayOffset;

        enum {
            k_Direct,
            k_Copy,
            k_GetElementsCritical,
            k_GetElements,
        } _kind;

    public:
        AnyWriteOnlyByteRegion(JNIEnv* env, jlong directAddress, jbyteArray array, jsize arrayOffset, jsize position, jsize remaining)
              : _size{static_cast<size_t>(remaining)},
                _dirtyCount{static_cast<size_t>(remaining)},
                _env{env},
                _array{array},
                _arrayOffset{arrayOffset} {
            assert(remaining == 0 || (directAddress != 0) != (array != nullptr));
            assert(position >= 0 && remaining >= 0);

            if (array == nullptr) {
                //use the direct address
                _kind = k_Direct;
                _data = reinterpret_cast<jbyte*>(directAddress) + position;
            } else if (jbyte* elems = _detail::tryGetByteArrayElementsCriticalWrite(env, array, remaining)) {
                _kind = k_GetElementsCritical;
                _elems = elems;
                _data = elems + arrayOffset + position;
            } else if (jbyte* elems = _detail::tryGetByteArrayElementsWrite(env, array, remaining)) {
                _kind = k_GetElements;
                _elems = elems;
                _data = elems + arrayOffset + position;
            } else {
                //allocate a buffer which we will copy the elements out of eventually
                _kind = k_Copy;
                _data = _alloc.allocate(remaining);
            }
        }

        AnyWriteOnlyByteRegion(JNIEnv* env, jlong directAddress, jbyteArray array, jsize arrayOffset, jsize position, jsize remaining, CommitEverythingTag)
              : AnyWriteOnlyByteRegion{env, directAddress, array, arrayOffset, position, remaining} {}

        AnyWriteOnlyByteRegion(JNIEnv* env, jlong directAddress, jbyteArray array, jsize arrayOffset, jsize position, jsize remaining, CommitNothingTag)
              : AnyWriteOnlyByteRegion{env, directAddress, array, arrayOffset, position, remaining} {
            _dirtyCount = 0;
        }

        AnyWriteOnlyByteRegion() = delete;
        AnyWriteOnlyByteRegion(const AnyWriteOnlyByteRegion&) = delete;

        ~AnyWriteOnlyByteRegion() {
            switch (_kind) {
                case k_Direct:
                    break;
                case k_Copy:
                    //copy the data back to the destination array (only the dirty bytes, though)
                    _env->SetByteArrayRegion(_array, _arrayOffset, static_cast<jsize>(_dirtyCount), _data);

                    _alloc.deallocate(_data, _size);
                    break;
                case k_GetElementsCritical:
                    _env->ReleasePrimitiveArrayCritical(_array, _elems, _dirtyCount ? 0 : JNI_ABORT);
                    break;
                case k_GetElements:
                    _env->ReleaseByteArrayElements(_array, _elems, _dirtyCount ? 0 : JNI_ABORT);
                    break;
            }
        }

        jbyte* data() noexcept { return _data; }
        size_t size() noexcept { return _size; }

        jbyte* begin() noexcept { return _data; }
        jbyte* end() noexcept { return _data + _size; }

        void setDirtyCount(size_t count) noexcept {
            assert(count <= _size);
            _dirtyCount = count;
        }
    };
}

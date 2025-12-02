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
    namespace _arrays {
        enum TernaryState : jbyte {
            NEVER = 0,
            ALLOWED = 1,
            ALWAYS = 2,
        };

        struct JniArraysConfig {
            TernaryState useCriticalRead = NEVER;
            TernaryState useCriticalWrite = NEVER;

            TernaryState useGetElementsRead = NEVER;
            TernaryState useGetElementsWrite = NEVER;
        };

        constinit inline JniArraysConfig ARRAYS_CONFIG = {};

        inline void configureJniArrays(const JniArraysConfig& config) noexcept {
            ARRAYS_CONFIG = config;
            //TODO: do i need some kind of memory fence here to ensure that other threads see this?
        }

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsCriticalRead(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            // We never use GetPrimitiveArrayCritical() as of this writing (Dec. 2025), mainly because there's no way to guarantee
            // that no other JNI functions are called while the array is pinned. This will need to be improved in the *ByteRegion
            // classes below.
            return nullptr;

            switch (ARRAYS_CONFIG.useCriticalRead) {
                default: __builtin_unreachable();
                case NEVER:
                    return nullptr;
                case ALLOWED:
                    //TODO: maybe add some extra tunables to avoid using critical sections when the array is below a certain size?
                    return nullptr;
                case ALWAYS:
                    return reinterpret_cast<jbyte*>(env->GetPrimitiveArrayCritical(array, nullptr));
            }
        }

        constinit inline bool ARRAYS_CRITICAL_WRITE_GOT_COPY_FOR_PARTIAL = false;

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsCriticalWrite(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            // We never use GetPrimitiveArrayCritical() as of this writing (Dec. 2025), mainly because there's no way to guarantee
            // that no other JNI functions are called while the array is pinned. This will need to be improved in the *ByteRegion
            // classes below.
            return nullptr;

            switch (ARRAYS_CONFIG.useCriticalWrite) {
                default: __builtin_unreachable();
                case NEVER:
                    return nullptr;
                case ALLOWED:
                    //TODO: maybe add some extra tunables to avoid using critical sections when the array is below a certain size?

                    //fallthrough
                case ALWAYS: {
                    bool partial = env->GetArrayLength(array) != remaining;
                    if (partial && ARRAYS_CRITICAL_WRITE_GOT_COPY_FOR_PARTIAL) {
                        return nullptr;
                    }

                    jboolean copy = false;
                    void* result = env->GetPrimitiveArrayCritical(array, &copy);

                    if (partial && copy) [[unlikely]] {
                        // If GetPrimitiveArrayCritical() returns a copy of the array even though we're only planning on writing
                        // to part of it, we cannot safely invoke ReleasePrimitiveArrayCritical() with mode 0 or JNI_COMMIT. Doing
                        // so would cause the ENTIRE array's contents to be overwritten with the copied data, which could cause writes
                        // made to another part of the array in the mean time to be overwritten with old data.
                        //
                        // To avoid corruption, we'll release the array again using JNI_ABORT to ensure that no data is copied back,
                        // and also remember that this occurred so that we can avoid triggering this again in the future.
                        ARRAYS_CRITICAL_WRITE_GOT_COPY_FOR_PARTIAL = true;
                        env->ReleasePrimitiveArrayCritical(array, result, JNI_ABORT);
                        return nullptr;
                    }

                    return reinterpret_cast<jbyte*>(result);
                }
            }
        }

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsRead(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            // We never use Get*ArrayElements(): as of this writing (Sep. 2025), there aren't any GC implementations on any Java version
            // which ever pin the arrays. Effectively, this means that the array is always copied, which makes it no better than an
            // implementation using manual buffer allocation and JNI copies (plus, manual copying lets us avoid moving unnecessary
            // data if not the entire array is accessed).
            return nullptr;

            switch (ARRAYS_CONFIG.useGetElementsRead) {
                default: __builtin_unreachable();
                case NEVER:
                    return nullptr;
                case ALLOWED:
                    if (jsize arrayLength = env->GetArrayLength(array); arrayLength - remaining >= arrayLength / 8) {
                        //we're accessing less than 7/8 of the total array, don't use GetArrayElements()
                        return nullptr;
                    }

                    //TODO: maybe add some extra tunables to avoid using GetArrayElements() when the array is below a certain size?

                    //fallthrough
                case ALWAYS:
                    //use GetArrayElements(), the array won't be pinned if supported
                    return env->GetByteArrayElements(array, nullptr);
            }
        }

        constinit inline bool ARRAY_GETELEMENTS_WRITE_GOT_COPY_FOR_PARTIAL = false;

        [[nodiscard]] inline jbyte* tryGetByteArrayElementsWrite(JNIEnv* env, jbyteArray array, jsize remaining) noexcept {
            // We never use Get*ArrayElements(): as of this writing (Sep. 2025), there aren't any GC implementations on any Java version
            // which ever pin the arrays. Effectively, this means that the array is always copied, which makes it no better than an
            // implementation using manual buffer allocation and JNI copies (plus, manual copying lets us avoid moving unnecessary
            // data if not the entire array is accessed).
            return nullptr;

            switch (ARRAYS_CONFIG.useGetElementsWrite) {
                default: __builtin_unreachable();
                case NEVER:
                    return nullptr;
                case ALLOWED:
                    // The 7/8 check from tryGetByteArrayElementsRead() would be kinda pointless here since we always abort
                    //   if we get a copy for any partial array access, so that optimization would only benefit a case which
                    //   can never happen anyway.

                    //TODO: maybe add some extra tunables to avoid using GetArrayElements() when the array is below a certain size?

                    //fallthrough
                case ALWAYS: {
                    bool partial = env->GetArrayLength(array) != remaining;
                    if (partial && ARRAY_GETELEMENTS_WRITE_GOT_COPY_FOR_PARTIAL) {
                        return nullptr;
                    }

                    jboolean copy = false;
                    jbyte* result = env->GetByteArrayElements(array, &copy);

                    if (partial && copy) [[unlikely]] {
                        // If GetByteArrayElements() returns a copy of the array even though we're only planning on writing
                        // to part of it, we cannot safely invoke ReleaseByteArrayElements() with mode 0 or JNI_COMMIT. Doing
                        // so would cause the ENTIRE array's contents to be overwritten with the copied data, which could cause writes
                        // made to another part of the array in the mean time to be overwritten with old data.
                        //
                        // To avoid corruption, we'll release the array again using JNI_ABORT to ensure that no data is copied back,
                        // and also remember that this occurred so that we can avoid triggering this again in the future.
                        ARRAY_GETELEMENTS_WRITE_GOT_COPY_FOR_PARTIAL = true;
                        env->ReleaseByteArrayElements(array, result, JNI_ABORT);
                        return nullptr;
                    }

                    return result;
                }
            }
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
            } else if (jbyte* elems = _arrays::tryGetByteArrayElementsCriticalWrite(env, array, remaining)) {
                _kind = k_GetElementsCritical;
                _elems = elems;
                _data = elems + arrayOffset + position;
            } else if (jbyte* elems = _arrays::tryGetByteArrayElementsWrite(env, array, remaining)) {
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
            } else if (jbyte* elems = _arrays::tryGetByteArrayElementsCriticalWrite(env, array, remaining)) {
                _kind = k_GetElementsCritical;
                _elems = elems;
                _data = elems + arrayOffset + position;
            } else if (jbyte* elems = _arrays::tryGetByteArrayElementsWrite(env, array, remaining)) {
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

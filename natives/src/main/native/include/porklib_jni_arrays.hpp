#pragma once

#include <jni.h>

#include "porklib_jni_exceptions.hpp"
#include "porklib_noinit_vector.hpp"

#include <cassert>
#include <type_traits>
#include <optional>
#include <variant>
#include <vector>

namespace porklib::jni {
    class AnyReadableByteRegion {
        const jbyte* _data;
        size_t _size;

        struct PinnedState {
            JNIEnv* _env;
            jbyteArray _array;
            jbyte* _elems;

            PinnedState(JNIEnv* env, jbyteArray array, jbyte* elems) noexcept :
                _env(env),
                _array(array),
                _elems(elems) {}

            PinnedState(const PinnedState&) = delete;

            ~PinnedState() {
                _env->ReleaseByteArrayElements(_array, _elems, JNI_ABORT);
            }
        };

        std::variant<
            bool,
            porklib::noinit_vector<jbyte>,
            PinnedState
        > _state;

    public:
        AnyReadableByteRegion(JNIEnv* env, jlong directAddress, jbyteArray array, jsize arrayOffset, jsize position, jsize remaining) {
            assert(remaining == 0 || (directAddress != 0) != (array != nullptr));
            assert(position >= 0 && remaining >= 0);

            if (array == nullptr) {
                //use the direct address
                _data = reinterpret_cast<const jbyte*>(directAddress) + position;
                _size = remaining;
            } else if (jsize arrayLength = env->GetArrayLength(array); arrayLength - remaining < arrayLength / 8) {
                //we're accessing at least 7/8 of the total array, use GetArrayElements() so that the array can be pinned if supported
                //TODO: we should probably use a smarter heuristic here...

                jbyte* elems = env->GetByteArrayElements(array, nullptr);
                if (elems == nullptr) [[unlikely]] throw std::bad_alloc{};

                _state.emplace<PinnedState>(env, array, elems);

                _data = elems + arrayOffset + position;
                _size = remaining;
            } else {
                //allocate a vector and then copy the elements into it
                auto& vec = _state.emplace<porklib::noinit_vector<jbyte>>(remaining);
                env->GetByteArrayRegion(array, arrayOffset + position, remaining, vec.data());

                _data = vec.data();
                _size = remaining;
            }
        }

        AnyReadableByteRegion() = delete;
        AnyReadableByteRegion(const AnyReadableByteRegion&) = delete;

        ~AnyReadableByteRegion() = default;

        const jbyte* data() const noexcept { return _data; }
        size_t size() const noexcept { return _size; }

        const jbyte* begin() const noexcept { return _data; }
        const jbyte* end() const noexcept { return _data + _size; }
    };

    class AnyWritableByteRegion {
        jbyte* _data;
        size_t _size;

        struct CopyState {
            porklib::noinit_vector<jbyte> _buf;

            JNIEnv* _env;
            jbyteArray _array;
            jsize _arrayOffset;

            CopyState(JNIEnv* env, jbyteArray array, jsize arrayOffset, jsize arrayCount) :
                _env(env),
                _array(array),
                _arrayOffset(arrayOffset),
                _buf(arrayCount) {}

            ~CopyState() {
                //copy the data back to the destination array
                _env->SetByteArrayRegion(_array, _arrayOffset, static_cast<jsize>(_buf.size()), _buf.data());
            }
        };


        struct PinnedState {
            JNIEnv* _env;
            jbyteArray _array;
            jbyte* _elems;

            PinnedState(JNIEnv* env, jbyteArray array, jbyte* elems) noexcept :
                _env(env),
                _array(array),
                _elems(elems) {}

            PinnedState(const PinnedState&) = delete;

            ~PinnedState() {
                _env->ReleaseByteArrayElements(_array, _elems, 0);
            }
        };

        std::variant<
            bool,
            CopyState,
            PinnedState
        > _state;

    public:
        AnyWritableByteRegion(JNIEnv* env, jlong directAddress, jbyteArray array, jsize arrayOffset, jsize position, jsize remaining) {
            assert(remaining == 0 || (directAddress != 0) != (array != nullptr));
            assert(position >= 0 && remaining >= 0);

            if (array == nullptr) {
                //use the direct address
                _data = reinterpret_cast<jbyte*>(directAddress) + position;
                _size = remaining;
            } else if (jsize arrayLength = env->GetArrayLength(array); arrayLength - remaining < arrayLength / 8) {
                //we're accessing at least 7/8 of the total array, use GetArrayElements() so that the array can be pinned if supported
                //TODO: we should probably use a smarter heuristic here...

                jbyte* elems = env->GetByteArrayElements(array, nullptr);
                if (elems == nullptr) [[unlikely]] throw std::bad_alloc{};

                _state.emplace<PinnedState>(env, array, elems);

                _data = elems + arrayOffset + position;
                _size = remaining;
            } else {
                //allocate a vector which we will copy the elements out of eventually
                auto& state = _state.emplace<CopyState>(env, array, arrayOffset + position, remaining);

                _data = state._buf.data();
                _size = remaining;
            }
        }

        AnyWritableByteRegion() = delete;
        AnyWritableByteRegion(const AnyWritableByteRegion&) = delete;

        ~AnyWritableByteRegion() = default;

        jbyte* data() noexcept { return _data; }
        size_t size() noexcept { return _size; }

        jbyte* begin() noexcept { return _data; }
        jbyte* end() noexcept { return _data + _size; }
    };
}

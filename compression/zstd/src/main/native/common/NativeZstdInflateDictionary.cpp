#include <common.h>
#include "NativeZstdInflateDictionary.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_digest0
        (JNIEnv* env, jclass cla, jlong dictAddr, jint dictSize)   {
    return (jlong) ZSTD_createDDict((void*) dictAddr, dictSize);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_release0
        (JNIEnv* env, jclass cla, jlong dict)   {
    auto ret = ZSTD_freeDDict((ZSTD_DDict*) dict);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

__attribute__((visibility("default"))) jint JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdInflateDictionary_id0
        (JNIEnv* env, jclass cla, jlong dict) {
    return (jint) ZSTD_getDictID_fromDDict((ZSTD_DDict*) dict);
}

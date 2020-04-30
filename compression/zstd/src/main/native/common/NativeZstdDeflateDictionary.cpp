#include <common.h>
#include "NativeZstdDeflateDictionary.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_digest0
        (JNIEnv* env, jclass cla, jlong dictAddr, jint dictSize, jint compressionLevel)   {
    return (jlong) ZSTD_createCDict((void*) dictAddr, dictSize, compressionLevel);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDeflateDictionary_release0
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeCDict((ZSTD_CDict*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
    }
}

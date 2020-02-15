#include <common.h>
#include "NativeZstdCDict.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdCDict_createCDict
        (JNIEnv* env, jclass cla, jlong dictAddr, jint dictSize, jint compressionLevel, jboolean copy)   {
    return copy
            ? (jlong) ZSTD_createCDict((void*) dictAddr, dictSize, compressionLevel)
            : 0;//: (jlong) ZSTD_createCDict_byReference((void*) dictAddr, dictSize, compressionLevel);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdCDict_releaseCDict
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeCDict((ZSTD_CDict*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return;
    }
}

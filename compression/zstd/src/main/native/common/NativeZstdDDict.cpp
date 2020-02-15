#include <common.h>
#include "NativeZstdDDict.h"

#include <lib-zstd/lib/zstd.h>
#include <lib-zstd/lib/common/zstd_errors.h>

__attribute__((visibility("default"))) jlong JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDDict_createDDict
        (JNIEnv* env, jclass cla, jlong dictAddr, jint dictSize, jboolean copy)   {
    return copy
            ? (jlong) ZSTD_createDDict((void*) dictAddr, dictSize)
            : 0;//: (jlong) ZSTD_createDDict_byReference((void*) dictAddr, dictSize);
}

__attribute__((visibility("default"))) void JNICALL Java_net_daporkchop_lib_compression_zstd_natives_NativeZstdDDict_releaseDDict
        (JNIEnv* env, jclass cla, jlong ctx)   {
    auto ret = ZSTD_freeDDict((ZSTD_DDict*) ctx);

    if (ZSTD_isError(ret))  {
        throwException(env, ZSTD_getErrorName(ret), (jlong) ret);
        return;
    }
}

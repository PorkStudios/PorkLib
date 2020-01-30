#include <common.h>

jint throwException(JNIEnv* env, const char* msg, int err)  {
    jclass clazz = env->FindClass("net/daporkchop/lib/natives/NativeCodeException");

    return env->Throw((jthrowable) env->NewObject(
        clazz,
        env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;I)V"),
        env->NewStringUTF(msg),
        err
    ));
}

jlong max_l(jlong a, jlong b) {
    return a > b ? a : b;
}

jlong min_l(jlong a, jlong b) {
    return a < b ? a : b;
}

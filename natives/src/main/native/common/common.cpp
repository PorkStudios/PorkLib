#include <common.h>

jint throwException(JNIEnv* env, const char* msg)  {
    jclass clazz = env->FindClass("net/daporkchop/lib/natives/NativeException");

    return env->Throw((jthrowable) env->NewObject(
        clazz,
        env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;)V"),
        env->NewStringUTF(msg)
    ));
}

jint throwException(JNIEnv* env, const char* msg, jint err)  {
    jclass clazz = env->FindClass("net/daporkchop/lib/natives/NativeException");

    return env->Throw((jthrowable) env->NewObject(
        clazz,
        env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;I)V"),
        env->NewStringUTF(msg),
        err
    ));
}

jint throwException(JNIEnv* env, const char* msg, jlong err)  {
    jclass clazz = env->FindClass("net/daporkchop/lib/natives/NativeException");

    return env->Throw((jthrowable) env->NewObject(
        clazz,
        env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;J)V"),
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

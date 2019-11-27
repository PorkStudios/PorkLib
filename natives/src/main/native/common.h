#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

jint throwException(JNIEnv* env, const char* msg, int err)  {
    jclass clazz = (*env)->FindClass(env, "net/daporkchop/lib/natives/NativeCodeException");

    return (*env)->Throw(env, (jthrowable) (*env)->NewObject(env,
        clazz,
        (*env)->GetMethodID(env, clazz, "<init>", "(Ljava/lang/String;I)V"),
        (*env)->NewStringUTF(env, msg),
        err
    ));
}

jlong max_l(jlong a, jlong b) {
    return a > b ? a : b;
}

jlong min_l(jlong a, jlong b) {
    return a < b ? a : b;
}

#ifdef __cplusplus
}
#endif

#include <jni.h>

#ifdef PORKLIB_NATIVES_DEBUG
//include some extra headers that might be useful while debugging
#include <stdlib.h>
#include <string.h>

#include <stdio.h>
#endif

jint throwException(JNIEnv* env, const char* msg);

jint throwException(JNIEnv* env, const char* msg, jint err);

jint throwException(JNIEnv* env, const char* msg, jlong err);

jlong max_l(jlong a, jlong b);

jlong min_l(jlong a, jlong b);

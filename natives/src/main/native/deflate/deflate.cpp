#include "NativeTest.h"

#include <stdio.h>

extern "C" void JNICALL Java_NativeTest_print(JNIEnv* env, jobject clazz)  {
    printf("Hello World!\n");

    return;
}

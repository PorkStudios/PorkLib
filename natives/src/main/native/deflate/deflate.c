#include "net_daporkchop_lib_natives_NativeTest.h"

#include <stdio.h>

void JNICALL Java_net_daporkchop_lib_natives_NativeTest_print(JNIEnv* env, jobject clazz)  {
    printf("Hello World!\n");

    return;
}

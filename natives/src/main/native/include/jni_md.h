#ifndef _JAVASOFT_JNI_MD_H_
#define _JAVASOFT_JNI_MD_H_

//this emulates the standard jni_md.h with a single header instead of using different ones depending on the OS

#include <cstdint>

#ifdef _MSC_VER
    #define JNIEXPORT __declspec(dllexport)
    #define JNIIMPORT __declspec(dllimport)
#elif __linux
    #ifdef ARM
        #define JNIEXPORT __attribute__((externally_visible,visibility("default")))
        #define JNIIMPORT __attribute__((externally_visible,visibility("default")))
    #else
        #define JNIEXPORT __attribute__((visibility("default")))
        #define JNIIMPORT __attribute__((visibility("default")))
    #endif
#else
    #error "couldn't determine if target OS is linux or windows"
#endif

#define JNICALL

typedef int32_t jint;
typedef int64_t jlong;

typedef char8_t jbyte;

#endif

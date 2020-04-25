#include <common.h>
#include <lib-zlib/zlib-ng.h>

#define Z_MODE_ZLIB 0
#define Z_MODE_GZIP 1
#define Z_MODE_RAW  2
#define Z_MODE_AUTO 3

#define Z_STRATEGY_DEFAULT  0
#define Z_STRATEGY_FILTERED 1
#define Z_STRATEGY_HUFFMAN  2
#define Z_STRATEGY_RLE      3
#define Z_STRATEGY_FIXED    4

int windowBits(jint mode);

# this godawful excuse for a makefile is hacked together using the one i made for libdsi, but at least it works

export TARGET	:=	$(shell basename $(CURDIR))
export TOPDIR	:=	$(CURDIR)

export CFLAGS		:=	-shared -Ofast -ffast-math -fPIC -flto
export CXXFLAGS		:=	$(CFLAGS)
export LDFLAGS		:=  $(CFLAGS)

export CXX	:= g++
export CC	:= gcc
export LD	:= $(CXX)

export SOURCES	:=	$(CURDIR)/src/main/native/common/source
export INCLUDES	:=	$(CURDIR)/src/main/native/common/include $(JAVA_HOME)include $(JAVA_HOME)include/linux

LIBS	:=	src/main/resources/libzlib.so

.PHONY: build clean

build: $(LIBS)

src/main/resources/libzlib.so:
	@$(MAKE) -C src/main/native/zlib build

clean:
	@rm -rf src/main/resources/lib*.so
	@$(MAKE) -C src/main/native/zlib clean

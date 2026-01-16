export SHELL		:=	/bin/bash

export TARGET		:=	$(shell basename $(CURDIR))
export TOPDIR		:=	$(CURDIR)
export TOOLCHAINS	:=	$(CURDIR)/toolchain
export COMMONSRC	:=	$(CURDIR)/src/main/native

# global flags (these apply during both compilation and linking)
export GLOBAL_FLAGS_COMMON		+=	-ffunction-sections -fdata-sections -fvisibility=hidden
export GLOBAL_FLAGS_COMMON		+=	-O2
export GLOBAL_FLAGS_COMMON		+=	-fno-fat-lto-objects

export GLOBAL_FLAGS_GNUC		+=	-flto=jobserver
export GLOBAL_FLAGS_CLANG		+=	-flto=jobserver

export GLOBAL_FLAGS_LINUX		+=
export GLOBAL_FLAGS_WINDOWS		+=

# compile flags (both C and C++ code)
export COMPILE_FLAGS_COMMON		+=	-Wall

export COMPILE_FLAGS_GNUC		+=
export COMPILE_FLAGS_CLANG		+=

export COMPILE_FLAGS_LINUX		+=	-fPIC
export COMPILE_FLAGS_WINDOWS	+=

# compile flags (only C code)
export COMPILE_CFLAGS_COMMON	+=

export COMPILE_CFLAGS_GNUC		+=
export COMPILE_CFLAGS_CLANG		+=

export COMPILE_CFLAGS_LINUX		+=
export COMPILE_CFLAGS_WINDOWS	+=

# compile flags (only C++ code)
export COMPILE_CXXFLAGS_COMMON	+=	-std=c++20

export COMPILE_CXXFLAGS_GNUC	+=
export COMPILE_CXXFLAGS_CLANG	+=

export COMPILE_CXXFLAGS_LINUX	+=
export COMPILE_CXXFLAGS_WINDOWS	+=

# link flags
export LDFLAGS_COMMON			+=	-Wl,--gc-sections
export LDFLAGS_COMMON			+=	-shared

export LDFLAGS_GNUC				+=
export LDFLAGS_CLANG			+=

export LDFLAGS_LINUX			+=
export LDFLAGS_WINDOWS			+=	-fuse-ld=lld

export BUILD_TYPE	:=	release
$(info natives: building for $(BUILD_TYPE))

export SOURCES		:=
export INCLUDES		:=	$(COMMONSRC)/include $(JAVA_HOME)/include $(JAVA_HOME)/include/linux

export ARCHS		:=	$(notdir $(wildcard $(TOOLCHAINS)/*))
export ARCH_TASKS	:=	$(foreach arch,$(ARCHS),build.$(arch))

export MODULES		:=	../compression/deflate ../compression/zstd

export LIBDIR		:=	$(TOPDIR)/src/main/native/lib
export LIB_URL_BASE	:=	https://cloud.daporkchop.net/programs/source/
export LIBS			:=	libdeflate-1.25.tar.gz zlib-ng-1.2.11.tar.gz zstd-1.5.7.tar.gz

.PHONY: build clean .FORCE

build: $(ARCH_TASKS) $(LIBS)
	@echo 'Done!'

build.%: .FORCE $(foreach module,$(MODULES),%,$(module).lib)
	@echo Built libraries for $(shell echo '$@' | perl -n -e '/build\.(.+)/ && print $$1')!

%.lib: .FORCE $(LIBS)
	@_PRJ_NAME=$(shell echo "$@" | perl -n -e '/,(.*?)\.lib$$/ && print $$1') && \
		_ARCH=$(shell echo "$@" | perl -n -e '/^([^,]*?),.*?\.lib$$/ && print $$1') && \
		$(MAKE) --no-print-directory -C $(TOPDIR)/$$_PRJ_NAME BUILD=$$_ARCH PROJDIR=$(TOPDIR)/$$_PRJ_NAME $$_ARCH && \
		echo Built $$_PRJ_NAME for target $$_ARCH!

#echo "arg = $@" && echo "prj_name = $$_PRJ_NAME" && echo "arch = $$_ARCH" && \

clean:
	@for f in $(MODULES); do $(MAKE) -C $(TOPDIR)/$$f clean; done

%.tar.gz:
	@echo "Downloading source for $@"
	@curl -o $@ $(LIB_URL_BASE)$@

.FORCE:

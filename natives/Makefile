export SHELL		:=	/bin/bash

export NPROC		:=	$(shell nproc)

export TARGET		:=	$(shell basename $(CURDIR))
export TOPDIR		:=	$(CURDIR)
export TOOLCHAINS	:=	$(CURDIR)/toolchain
export COMMONSRC	:=	$(CURDIR)/src/main/native

FLAGS				:=	-shared -fPIC -ffunction-sections -fdata-sections -fvisibility=hidden
export CFLAGS		:=	$(FLAGS) -Ofast -ffast-math
export CXXFLAGS		:=	$(CFLAGS)
export LDFLAGS		:=	$(FLAGS) -O3 -Wl,--gc-sections

ifndef PORKLIB_NATIVES_DEBUG
export CFLAGS		:=	$(CFLAGS)
export BUILD_TYPE	:=	release
else
export CFLAGS		:=	$(CFLAGS) -DPORKLIB_NATIVES_DEBUG
export BUILD_TYPE	:=	debug
endif
$(info natives: building for $(BUILD_TYPE))

export SOURCES		:=
export INCLUDES		:=	$(COMMONSRC)/include $(JAVA_HOME)/include $(JAVA_HOME)/include/linux

export ARCHS		:=	aarch64-linux-gnu x86_64-linux-gnu x86_64-w64-mingw32
#TODO: export ARCHS		:=	aarch64-linux-gnu arm-linux-gnueabihf x86_64-linux-gnu x86_64-w64-mingw32
#export ARCHS		:=	$(foreach arch,$(ARCHS),$(if $(shell which $(arch)-gcc),$(arch)))
#export ARCHS		:=	$(foreach arch,$(ARCHS),$(if $(shell which $(arch)-g++),$(arch)))
export ARCH_TASKS	:=	$(foreach arch,$(ARCHS),build.$(arch))

export MODULES		:=	../compression/zlib ../compression/zstd

export LIBDIR		:=	$(TOPDIR)/src/main/native/lib
export LIB_URL_BASE	:=	https://cloud.daporkchop.net/programs/source/
export LIBS			:=	zlib-ng-1.2.11.tar.gz zstd-1.4.4.tar.gz

.PHONY: build clean .FORCE

build: $(ARCH_TASKS) $(LIBS)

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

export SHELL		:=	/bin/bash
export GCC_VERSION	:=	8.1.0

export TARGET		:=	$(shell basename $(CURDIR))
export TOPDIR		:=	$(CURDIR)
export SRCDIR		:=	$(TOPDIR)/src/main/native
export OUTDIR		:=	$(TOPDIR)/src/main/resources

export CFLAGS		:=	-shared -Ofast -ffast-math -fPIC -ffunction-sections -fdata-sections -fvisibility=hidden -flto
export CXXFLAGS		:=	$(CFLAGS)
export LDFLAGS		:=	$(CFLAGS) -Wl,--gc-sections

export SOURCES		:=	$(SRCDIR)/common/source
export INCLUDES		:=	$(SRCDIR)/common/include $(JAVA_HOME)include $(JAVA_HOME)include/linux

export ARCHS		:=	x86_64-linux-gnu x86-linux-gnu x86_64-w64-mingw32
#export ARCHS		:=	$(foreach arch,$(ARCHS),$(if $(shell which $(arch)-gcc),$(arch)))
#export ARCHS		:=	$(foreach arch,$(ARCHS),$(if $(shell which $(arch)-g++),$(arch)))
export ARCH_TASKS	:=	$(foreach arch,$(ARCHS),build.$(arch))

export MODULES		:=	zlib

#.PHONY: build clean .FORCE $(ARCH_TASKS) $(foreach arch,$(ARCHS),$(foreach module,$(MODULES),$(arch)/$(module).lib))
.PHONY: build clean .FORCE

#build: $(ARCH_TASKS) $(foreach arch,$(ARCHS),$(foreach mod,$(MODULES),$(OUTDIR)/$(arch)/lib$(mod).so))
build: $(ARCH_TASKS)

build.%: .FORCE $(foreach module,$(MODULES),%/$(module).lib)
	@echo Built libraries for $(shell echo '$@' | perl -n -e '/build\.(.+)/ && print $$1')!

%.lib: .FORCE
	@_PRJ_NAME=$(shell echo "$@" | perl -n -e '/\/([^.]+)\.lib$$/ && print $$1') && \
		_ARCH=$(shell echo "$@" | perl -n -e '/^([^\/]*?)\/[^.]+\.lib$$/ && print $$1') && \
		$(MAKE) -C $(SRCDIR)/$$_PRJ_NAME BUILD=$$_ARCH PROJDIR=$(SRCDIR)/$$_PRJ_NAME $$_ARCH && \
		echo Built $$_PRJ_NAME for target $$_ARCH!

clean: $(foreach mod,$(MODULES),clean.$(mod))
	@rm -rf $(OUTDIR)/*/

clean.%:
	@$(MAKE) -C $(SRCDIR)/$(subst clean.,,$@) clean

.FORCE:

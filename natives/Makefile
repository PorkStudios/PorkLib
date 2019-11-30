# this godawful excuse for a makefile is hacked together using the one i made for libdsi, but at least it works

export SHELL		:=	/bin/bash

export TARGET		:=	$(shell basename $(CURDIR))
export TOPDIR		:=	$(CURDIR)
export SRCDIR		:=	$(TOPDIR)/src/main/native
export OUTDIR		:=	$(TOPDIR)/src/main/resources

export CFLAGS		:=	-shared -Ofast -ffast-math -fPIC -flto
export CXXFLAGS		=	$(CFLAGS)
export LDFLAGS		=	$(CFLAGS)

export CXX			:=	g++
export CC			:=	gcc
export LD			:=	$(CXX)

export SOURCES		:=	$(SRCDIR)/common/source
export INCLUDES		:=	$(SRCDIR)/common/include $(JAVA_HOME)include $(JAVA_HOME)include/linux

export ARCHS		:=	x86_64-pc-linux-gnu
export ARCH_TASKS	:=	$(foreach arch,$(ARCHS),build.$(arch))

export MODULES		:=	zlib

.PHONY: build clean .FORCE

build: $(ARCH_TASKS) $(foreach arch,$(ARCHS),$(foreach mod,$(MODULES),$(OUTDIR)/$(arch)/lib$(mod).so))

build.%: $(foreach mod,$(MODULES),$(OUTDIR)/%/lib$(mod).so) .FORCE
	@echo Built libraries for $(shell echo '$@' | perl -n -e '/build\.(.+)/ && print $$1')!

%.so: .FORCE
	@_PRJ_NAME=$(shell echo "$@" | perl -n -e '/\/lib([^.]+)\.so$$/ && print $$1') && \
		_ARCH=$(shell echo "$@" | perl -n -e '/([^\/]*?)\/lib[^.]+\.so$$/ && print $$1') && \
		$(MAKE) -C $(SRCDIR)/$$_PRJ_NAME BUILD=$$_ARCH PROJDIR=$(SRCDIR)/$$_PRJ_NAME $$_ARCH && \
		echo Built $$_PRJ_NAME for target $$_ARCH!

clean: $(foreach mod,$(MODULES),clean.$(mod))
	@rm -rf $(OUTDIR)/*/

clean.%:
	@$(MAKE) -C $(SRCDIR)/$(subst clean.,,$@) clean

.FORCE:

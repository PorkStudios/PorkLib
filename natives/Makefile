# this godawful excuse for a makefile is hacked together using the one i made for libdsi, but at least it works

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

.PHONY: build clean

build: $(ARCH_TASKS) $(foreach arch,$(ARCHS),$(foreach mod,$(MODULES),$(OUTDIR)/$(arch)/lib$(mod).so))

build.%: $(foreach mod,$(MODULES),$(OUTDIR)/%/lib$(mod).so)
	@echo Built libraries for $(shell echo '$@' | perl -n -e '/build\.(.+)/ && print $$1')!

%.so:
	@$(MAKE) -C $(SRCDIR)/$(shell echo "$@" | perl -n -e '/\/lib([^.]+)\.so$$/ && print $$1') \
		BUILD=$(shell echo "$@" | perl -n -e '/([^\/]*?)\/lib[^.]+\.so$$/ && print $$1') \
		$(shell echo "$@" | perl -n -e '/([^\/]*?)\/lib[^.]+\.so$$/ && print $$1') \
		PROJDIR=$(SRCDIR)/$(shell echo "$@" | perl -n -e '/\/lib([^.]+)\.so$$/ && print $$1')
	@echo Built $(shell echo "$@" | perl -n -e '/\/lib([^.]+)\.so$$/ && print $$1') for target $(shell echo "$@" | perl -n -e '/([^\/]*?)\/lib[^.]+\.so$$/ && print $$1')!

clean: $(foreach mod,$(MODULES),clean.$(mod))
	@rm -rf $(OUTDIR)/*/

clean.%:
	@$(MAKE) -C $(SRCDIR)/$(subst clean.,,$@) clean

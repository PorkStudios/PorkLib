BUILD		?=	$(DEFAULT_ARCH)
BUILDDIR	:=	build-$(BUILD)

CFLAGS		:=	$(CFLAGS) -D_LARGEFILE64_SOURCE=1 -DHAVE_HIDDEN -DPIC

ifneq ($(BUILDDIR),$(notdir $(CURDIR)))

export INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(CURDIR)/include

export SOURCES			:=	$(SOURCES) $(CURDIR)/source $(CURDIR)/lib-zlib
export CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.c'))
export CPPFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.cpp'))

export CFILES_COMP		:=  $(subst /,_,$(CFILES))
export CPPFILES_COMP	:=  $(subst /,_,$(CPPFILES))
export OFILES			:=	$(CFILES_COMP:.c=.c.o) $(CPPFILES_COMP:.cpp=.cpp.o)

export CFLAGS			:=	$(CFLAGS) $(INCLUDE)
export CXXFLAGS			:=	$(CXXFLAGS) $(INCLUDE)
export LDFLAGS			:=	$(LDFLAGS) $(INCLUDE)

.PHONY: $(BUILD) clean

clean:
	@rm -rf build-*

$(BUILD):
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD)

else

$(OUTDIR)/$(BUILD)/libzlib.so: $(OFILES)
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) -o $@ $(OFILES)

%.c.o:
	@echo "Building $(subst .c.o,.c,$(subst _,/,$@))..."
	@$(CC) $(CFLAGS) -c $(subst .c.o,.c,$(subst _,/,$@)) -o $@

%.cpp.o:
	@echo "Building $(subst .cpp.o,.cpp,$(subst _,/,$@))..."
	@$(CXX) $(CXXFLAGS) -c $(subst .cpp.o,.cpp,$(subst _,/,$@)) -o $@

endif

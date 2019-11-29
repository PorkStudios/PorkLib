BUILDDIR	:=	build-$(BUILD)

CFLAGS		:=	$(CFLAGS) -D_LARGEFILE64_SOURCE=1 -DHAVE_HIDDEN -DPIC

ifneq ($(BUILDDIR),$(notdir $(CURDIR)))

.PHONY: $(BUILD) clean

clean:
	@rm -rf build-* tmp/

$(BUILD): lib-zlib/zlib.h
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD)

lib-zlib/zlib.h:
	@echo "Downloading zlib sources..."
	@wget --no-proxy -O - https://cloud.daporkchop.net/programs/source/zlib-1.2.11.tar.gz | tar zxf -
	@mv zlib-1.2.11/ lib-zlib/

else

INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(CURDIR)/include

SOURCES			:=	$(SOURCES) $(PROJDIR)/source $(PROJDIR)/lib-zlib
CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.c'))
CPPFILES		:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.cpp'))

CFILES_COMP		:=  $(subst /,_,$(CFILES))
CPPFILES_COMP	:=  $(subst /,_,$(CPPFILES))
OFILES			:=	$(CFILES_COMP:.c=.c.o) $(CPPFILES_COMP:.cpp=.cpp.o)

CFLAGS			:=	$(CFLAGS) $(INCLUDE)
CXXFLAGS		:=	$(CXXFLAGS) $(INCLUDE)
LDFLAGS			:=	$(LDFLAGS) $(INCLUDE)

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

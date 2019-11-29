BUILDDIR	:=	build-$(BUILD)

CFLAGS		:=	$(CFLAGS) -D__LARGEFILE64__SOURCE=1 -DHAVE__HIDDEN -DPIC

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

SOURCES			:=	$(SOURCES) $(PROJDIR)/source $(PROJDIR)/lib-zlib
CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.c'))
CPPFILES		:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.cpp'))

CFILES_COMP		:=  $(subst /,__,$(CFILES))
CPPFILES_COMP	:=  $(subst /,__,$(CPPFILES))
OFILES			:=	$(CFILES_COMP:.c=.c.o) $(CPPFILES_COMP:.cpp=.cpp.o)
#OFILES			:=	$(foreach f,$(CFILES:.c=.c.o),$(CURDIR)$(f)) $(foreach f,$(CPPFILES:.cpp=.cpp.o),$(CURDIR)$(f))
OFILES			:=	$(addsuffix .$(BUILD).o,$(CFILES) $(CPPFILES))

INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(PROJDIR)/include
CFLAGS			:=	$(CFLAGS) $(INCLUDE)
CXXFLAGS		:=	$(CXXFLAGS) $(INCLUDE)
LDFLAGS			:=	$(LDFLAGS) $(INCLUDE)

$(OUTDIR)/$(BUILD)/libzlib.so: $(CFILES) $(CPPFILES) $(OFILES)
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) -o $@ $(OFILES)

%.c.$(BUILD).o: %.c
#	@echo "Building $(subst .c.o,.c,$(subst __,/,$@))..."
#	@$(CC) $(CFLAGS) -c $(subst .c.o,.c,$(subst __,/,$@)) -o $@
	@echo "Building $(subst .c.$(BUILD).o,.c,$@)..."
	@$(CC) $(CFLAGS) -c $(subst .c.$(BUILD).o,.c,$@) -o $@

%.cpp.$(BUILD).o: %.cpp
#	@echo "Building $(subst .cpp.o,.cpp,$(subst __,/,$@))..."
#	@$(CXX) $(CXXFLAGS) -c $(subst .cpp.o,.cpp,$(subst __,/,$@)) -o $@
	@echo "Building $(subst .cpp.$(BUILD).o,.cpp,$@)..."
	@$(CXX) $(CXXFLAGS) -c $(subst .cpp.$(BUILD).o,.cpp,$@) -o $@

endif

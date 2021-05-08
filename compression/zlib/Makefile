ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

export OUTDIR	:=	$(CURDIR)/src/main/resources/net/daporkchop/lib/compression/zlib/natives
BUILDDIR		:=	build/native/$(BUILD_TYPE)/$(BUILD)

clean:
	@if [ -d build/native/ ]; then rm -rf build/native/; fi
	@rm -rf $(foreach arch,$(ARCHS),$(OUTDIR)/$(arch))

$(BUILD):
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD) build

else
.PHONY: build

include $(TOOLCHAINS)/$(BUILD)

SOURCES			:=	$(PROJDIR)/src/main/native/common $(PROJDIR)/src/main/native/$(BUILD) $(COMMONSRC)/common $(COMMONSRC)/$(BUILD)
SOURCES			:=	$(abspath $(SOURCES))

CFILES			:=	$(foreach dir,$(SOURCES),$(wildcard $(dir)/*.c))
CPPFILES		:=	$(foreach dir,$(SOURCES),$(wildcard $(dir)/*.cpp))
OFILES			:=	$(addsuffix .o,$(subst /,__,$(CFILES)) $(subst /,__,$(CPPFILES)))

INCLUDE			:=	$(addprefix -I,$(INCLUDES) $(CURDIR))

build: libzlib.$(EXT)
	@echo "Copying libzlib.$(EXT) to $(OUTDIR)/..."
	@[ -d $(OUTDIR) ] || mkdir -p $(OUTDIR)
	@cp libzlib.$(EXT) $(OUTDIR)/$(BUILD).$(EXT)

libzlib.$(EXT): $(CFILES) $(CPPFILES) $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a

%.c.o: $(CFILES) $(CURDIR)/lib-zlib/libz-ng.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CC) $(CFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

%.cpp.o: $(CPPFILES) $(CURDIR)/lib-zlib/libz-ng.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	$(CXX) $(CXXFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zlib/libz-ng.a: $(TOPDIR)/zlib-ng-1.2.11.tar.gz
	@[ ! -d lib-zlib ] || rm -rf lib-zlib/
	@tar zxf $(TOPDIR)/zlib-ng-1.2.11.tar.gz
	@mv zlib-ng-1.2.11/ lib-zlib/
	@cd lib-zlib/ && ./configure --static && cd ..
#	@cd lib-zlib/ && ./configure --static --with-gzfileops && cd ..
	@$(MAKE) -C $(CURDIR)/lib-zlib/ -f $(CURDIR)/lib-zlib/Makefile libz-ng.a

endif

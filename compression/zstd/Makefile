ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

export OUTDIR	:=	$(CURDIR)/src/main/resources
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

build: libzstd.$(EXT)
	@echo "Copying libzstd.$(EXT) to $(OUTDIR)/$(BUILD)/..."
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
	@cp libzstd.$(EXT) $(OUTDIR)/$(BUILD)/net_daporkchop_lib_compression_zstd_natives_NativeZstd.$(EXT)

libzstd.$(EXT): $(CFILES) $(CPPFILES) $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Stripping $@..."
	@$(STRIP) $@

%.c.o: $(CFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CC) $(CFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

%.cpp.o: $(CPPFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	$(CXX) $(CXXFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zstd/lib/libzstd.a: $(TOPDIR)/zstd-1.4.4.tar.gz
	@[ ! -d lib-zstd ] || rm -rf lib-zstd/
	@tar zxf $(TOPDIR)/zstd-1.4.4.tar.gz
	@mv zstd-1.4.4/ lib-zstd/
	@$(MAKE) -C $(CURDIR)/lib-zstd/ -f $(CURDIR)/lib-zstd/Makefile ZSTD_LEGACY_SUPPORT=0 ZSTD_LEGACY_MULTITHREADED_API=0 lib-release

endif

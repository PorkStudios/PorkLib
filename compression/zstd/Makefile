ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

export OUTDIR	:=	$(CURDIR)/src/main/resources/net/daporkchop/lib/compression/zstd/natives
BUILDDIR		:=	build/native/$(BUILD_TYPE)/$(BUILD)

clean:
	@if [ -d build/native/ ]; then rm -rf build/native/; fi
	@rm -rf $(foreach arch,$(ARCHS),$(OUTDIR)/$(arch))

$(BUILD):
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD) build

else
.PHONY: build
.SECONDEXPANSION:

include $(TOOLCHAINS)/$(BUILD)

SOURCES			:=	$(PROJDIR)/src/main/native/common $(PROJDIR)/src/main/native/$(BUILD) $(COMMONSRC)/common $(COMMONSRC)/$(BUILD)
SOURCES			:=	$(abspath $(SOURCES))

CPPFILES		:=	$(foreach dir,$(SOURCES),$(wildcard $(dir)/*.cpp))
OFILES			:=	$(addsuffix .o,$(subst /,__,$(CPPFILES)))

INCLUDE			:=	$(addprefix -I,$(INCLUDES) $(CURDIR))

-include $(OFILES:.o=.d)

build: libzstd.$(EXT)
	@echo "Copying libzstd.$(EXT) to $(OUTDIR)/..."
	@[ -d $(OUTDIR) ] || mkdir -p $(OUTDIR)
	@cp libzstd.$(EXT) $(OUTDIR)/$(BUILD).$(EXT)

libzstd.$(EXT): $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a

%.cpp.o: $(CURDIR)/lib-zstd/lib/libzstd.a $$(subst __,/,%.cpp)
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	$(CXX) $(CXXFLAGS) $(INCLUDE) -MMD -MP -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zstd/lib/libzstd.a: $(TOPDIR)/zstd-1.4.4.tar.gz
	@[ ! -d lib-zstd ] || rm -rf lib-zstd/
	@tar zxf $(TOPDIR)/zstd-1.4.4.tar.gz
	@mv zstd-1.4.4/ lib-zstd/
	@$(MAKE) -C $(CURDIR)/lib-zstd/ -f $(CURDIR)/lib-zstd/Makefile \
		ZSTD_LEGACY_SUPPORT=0 ZSTD_LEGACY_MULTITHREADED_API=0 \
		ZSTDLIB_VISIBLE=ZSTDLIB_HIDDEN ZSTDLIB_VISIBILITY=ZSTDLIB_HIDDEN \
		ZSTDERRORLIB_VISIBLE=ZSTDLIB_HIDDEN ZSTDERRORLIB_VISIBILITY=ZSTDLIB_HIDDEN \
		ZDICTLIB_VISIBLE=ZSTDLIB_HIDDEN ZDICTLIB_VISIBILITY=ZSTDLIB_HIDDEN \
 		lib-release

endif

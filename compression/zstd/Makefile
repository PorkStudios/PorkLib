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

MAKEFILES		:=	$(TOPDIR)/Makefile $(TOOLCHAINS)/$(BUILD) $(PROJDIR)/Makefile

#dummy rule
$(TOOLCHAINS)/$(BUILD):

SOURCES			:=	$(PROJDIR)/src/main/native/common $(PROJDIR)/src/main/native/$(BUILD) $(COMMONSRC)/common $(COMMONSRC)/$(BUILD)
SOURCES			:=	$(abspath $(SOURCES))

CPPFILES		:=	$(foreach dir,$(SOURCES),$(wildcard $(dir)/*.cpp))
OFILES			:=	$(addsuffix .o,$(subst /,__,$(CPPFILES)))

INCLUDE			:=	$(addprefix -I,$(INCLUDES) $(CURDIR))

CFLAGS			+= -DZSTDLIB_VISIBLE=ZSTDLIB_HIDDEN -DZSTDERRORLIB_VISIBLE=ZSTDERRORLIB_HIDDEN -DZDICTLIB_VISIBLE=ZDICTLIB_HIDDEN
CXXFLAGS		+= -DZSTDLIB_VISIBLE=ZSTDLIB_HIDDEN -DZSTDERRORLIB_VISIBLE=ZSTDERRORLIB_HIDDEN -DZDICTLIB_VISIBLE=ZDICTLIB_HIDDEN

-include $(OFILES:.o=.d)

build: libzstd.$(EXT) $(MAKEFILES)
	@echo "Copying libzstd.$(EXT) to $(OUTDIR)/..."
	@[ -d $(OUTDIR) ] || mkdir -p $(OUTDIR)
	@cp --reflink=auto libzstd.$(EXT) $(OUTDIR)/$(BUILD).$(EXT)

libzstd.$(EXT): $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a $(MAKEFILES)
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a

%.cpp.o: $(CURDIR)/lib-zstd/lib/libzstd.a $$(subst __,/,%.cpp) $(MAKEFILES)
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	$(CXX) $(CXXFLAGS) $(INCLUDE) -MMD -MP -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zstd/lib/libzstd.a: $(CURDIR)/lib-zstd/lib/zstd.h $(MAKEFILES)
	@$(MAKE) -C $(CURDIR)/lib-zstd/lib/ -f $(CURDIR)/lib-zstd/lib/Makefile \
		clean
	@$(MAKE) -C $(CURDIR)/lib-zstd/lib/ -f $(CURDIR)/lib-zstd/lib/Makefile \
		ZSTD_LEGACY_SUPPORT=0 ZSTD_LEGACY_MULTITHREADED_API=0 \
		CFLAGS='$(CFLAGS)' \
		libzstd.a

$(CURDIR)/lib-zstd/lib/zstd.h: $(TOPDIR)/zstd-1.5.7.tar.gz
	@[ ! -d lib-zstd ] || rm -rf lib-zstd/
	@tar zxf $(TOPDIR)/zstd-1.5.7.tar.gz
	@mv zstd-1.5.7/ lib-zstd/

endif

OUTDIR		:=	$(PROJDIR)/src/main/resources

ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

BUILDDIR	:=	build/$(BUILD)

clean:
	@rm -rf build/ $(foreach arch,$(ARCHS),$(OUTDIR)/$(arch))

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

build: $(OUTDIR)/$(BUILD)/libzstd.$(EXT)

$(OUTDIR)/$(BUILD)/libzstd.$(EXT): $(CFILES) $(CPPFILES) $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
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
	@$(MAKE) -C $(CURDIR)/lib-zstd/ -f $(CURDIR)/lib-zstd/Makefile lib-release

endif

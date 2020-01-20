BUILDDIR	:=	build/$(BUILD)

ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

clean:
	@rm -rf build/ tmp/

$(BUILD):
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD) zstd-$(BUILD)

else
.PHONY: zstd-$(BUILD) lib-zstd

include $(TOPDIR)/toolchain/$(BUILD)

SOURCES			:=	$(SOURCES) $(PROJDIR)/source

CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.c'))
CPPFILES		:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.cpp'))
OFILES			:=	$(addsuffix .o,$(subst /,__,$(CFILES)) $(subst /,__,$(CPPFILES)))

INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(PROJDIR)/include -I$(CURDIR)

zstd-$(BUILD): $(OUTDIR)/$(BUILD)/libzstd.$(EXT)

$(OUTDIR)/$(BUILD)/libzstd.$(EXT): $(CFILES) $(CPPFILES) $(OFILES) $(TOPDIR)/Makefile $(PROJDIR)/Makefile $(CURDIR)/lib-zstd/lib/libzstd.a
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES) $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Stripping $@..."
	@strip $@

%.c.o: $(CFILES) $(TOPDIR)/Makefile $(PROJDIR)/Makefile $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CC) $(CFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

%.cpp.o: $(CPPFILES) $(TOPDIR)/Makefile $(PROJDIR)/Makefile $(CURDIR)/lib-zstd/lib/libzstd.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CXX) $(CXXFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zstd/lib/libzstd.a: $(TOPDIR)/zstd-1.4.4.tar.gz
	@[ ! -d lib-zstd ] || rm -rf lib-zstd/
	@tar zxf $(TOPDIR)/zstd-1.4.4.tar.gz
	@mv zstd-1.4.4/ lib-zstd/
	@$(MAKE) -C $(CURDIR)/lib-zstd/ -f $(CURDIR)/lib-zstd/Makefile lib-release

endif

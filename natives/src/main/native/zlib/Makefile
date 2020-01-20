BUILDDIR	:=	build/$(BUILD)

ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

clean:
	@rm -rf build/ tmp/

$(BUILD):
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD) zlib-$(BUILD)

else
.PHONY: zlib-$(BUILD) lib-zlib

include $(TOPDIR)/toolchain/$(BUILD)

SOURCES			:=	$(SOURCES) $(PROJDIR)/source

CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.c'))
CPPFILES		:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.cpp'))
OFILES			:=	$(addsuffix .o,$(subst /,__,$(CFILES)) $(subst /,__,$(CPPFILES)))

CFLAGS			:=	$(CFLAGS) -D__LARGEFILE64__SOURCE=1 -DHAVE__HIDDEN -DPIC
INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(PROJDIR)/include -I$(CURDIR)

zlib-$(BUILD): $(OUTDIR)/$(BUILD)/libzlib.$(EXT)

$(OUTDIR)/$(BUILD)/libzlib.$(EXT): $(CFILES) $(CPPFILES) $(OFILES) $(TOPDIR)/Makefile $(TOPDIR)/src/main/native/zlib/Makefile $(CURDIR)/lib-zlib/libz-ng.a
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a
	@echo "Stripping $@..."
	@strip $@

%.c.o: $(CFILES) $(TOPDIR)/Makefile $(TOPDIR)/src/main/native/zlib/Makefile $(CURDIR)/lib-zlib/libz-ng.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CC) $(CFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

%.cpp.o: $(CPPFILES) $(TOPDIR)/Makefile $(TOPDIR)/src/main/native/zlib/Makefile $(CURDIR)/lib-zlib/libz-ng.a
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CXX) $(CXXFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zlib/libz-ng.a: $(TOPDIR)/zlib-ng-1.2.11.tar.gz
	@[ ! -d lib-zlib ] || rm -rf lib-zlib/
	@tar zxf $(TOPDIR)/zlib-ng-1.2.11.tar.gz
	@mv zlib-ng-1.2.11/ lib-zlib/
	@cd lib-zlib/ && CFLAGS="-fPIC -Ofast -fvisibility=hidden -ffunction-sections -fdata-sections" ./configure --static --64 --with-gzfileops && cd ..
	@$(MAKE) -C $(CURDIR)/lib-zlib/ -f $(CURDIR)/lib-zlib/Makefile libz-ng.a

endif

ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

export OUTDIR	:=	$(CURDIR)/src/main/resources/net/daporkchop/lib/compression/deflate
BUILDDIR		:=	build/native/$(BUILD_TYPE)/$(BUILD)

clean:
	@if [ -d build/native/ ]; then rm -rf build/native/; fi
	@rm -rf $(foreach arch,$(ARCHS),$(OUTDIR)/libdeflate/$(arch))

$(BUILD): $(BUILDDIR)/lib-libdeflate/README.md
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD) build

$(BUILDDIR)/lib-libdeflate/README.md: $(TOPDIR)/libdeflate-1.25.tar.gz Makefile
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@[ ! -d $(BUILDDIR)/lib-libdeflate ] || rm -rf $(BUILDDIR)/lib-libdeflate/
	@cd $(BUILDDIR)/ && tar zxf $(TOPDIR)/libdeflate-1.25.tar.gz

	#fun workaround to force libdeflate stuff to have hidden visibility
	@echo '#undef LIBDEFLATEAPI' >> $(BUILDDIR)/libdeflate-1.25/tmp
	@echo '#define LIBDEFLATEAPI __attribute__((visibility("hidden")))' >> $(BUILDDIR)/libdeflate-1.25/tmp
	@cat $(BUILDDIR)/libdeflate-1.25/tmp $(BUILDDIR)/libdeflate-1.25/libdeflate.h >> $(BUILDDIR)/libdeflate-1.25/libdeflate.h.tmp
	@mv $(BUILDDIR)/libdeflate-1.25/libdeflate.h.tmp $(BUILDDIR)/libdeflate-1.25/libdeflate.h
	@echo '#undef LIBDEFLATEAPI' >> $(BUILDDIR)/libdeflate-1.25/lib/lib_common.h
	@echo '#define LIBDEFLATEAPI __attribute__((visibility("hidden")))' >> $(BUILDDIR)/libdeflate-1.25/lib/lib_common.h

	@touch $(BUILDDIR)/libdeflate-1.25/README.md
	@mv $(BUILDDIR)/libdeflate-1.25/ $(BUILDDIR)/lib-libdeflate/

else
.PHONY: build
.SECONDEXPANSION:

include $(TOOLCHAINS)/$(BUILD)

MAKEFILES		:=	$(TOPDIR)/Makefile $(TOOLCHAINS)/$(BUILD) $(PROJDIR)/Makefile

#dummy rule
$(TOOLCHAINS)/$(BUILD):

SOURCEDIRS_COMMON		:=	$(abspath $(COMMONSRC)/common $(COMMONSRC)/$(BUILD))

CPPFILES_COMMON			:=	$(foreach dir,$(SOURCEDIRS_COMMON),$(wildcard $(dir)/*.cpp))

SOURCEDIRS_LIBDEFLATE	:=	$(SOURCEDIRS_COMMON) $(abspath $(CURDIR)/lib-libdeflate/lib/ $(wildcard $(CURDIR)/lib-libdeflate/lib/*/))
SOURCEFILES_LIBDEFLATE	:=	$(foreach dir,$(SOURCEDIRS_LIBDEFLATE),$(wildcard $(dir)/*.cpp))
SOURCEFILES_LIBDEFLATE	+=	$(foreach dir,$(SOURCEDIRS_LIBDEFLATE),$(wildcard $(dir)/*.c))
SOURCEFILES_LIBDEFLATE	+=	$(abspath $(PROJDIR)/src/main/native/porklib_compression_deflate_libdeflate.cpp)

OFILES_LIBDEFLATE		:=	$(addsuffix .o,$(subst /,__,$(SOURCEFILES_LIBDEFLATE)))

OFILES					:=	$(OFILES_LIBDEFLATE)

INCLUDE			:=	$(addprefix -I,$(INCLUDES) $(CURDIR))

-include $(OFILES:.o=.d)

build: $(OUTDIR)/libdeflate/$(BUILD).$(EXT)
	@echo '$(OUTDIR)/libdeflate/$(BUILD).$(EXT) is ready!'

$(OUTDIR)/libdeflate/$(BUILD).$(EXT): $(OFILES_LIBDEFLATE) $(MAKEFILES)
	@[ -d $(OUTDIR) ] || mkdir -p $(OUTDIR)
	$(LD) $(LDFLAGS) -o $@ $(OFILES)

$(OUTDIR)/zlibng/$(BUILD).$(EXT): $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a $(MAKEFILES)
	@[ -d $(OUTDIR) ] || mkdir -p $(OUTDIR)
	$(LD) $(LDFLAGS) -o $@ $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a

%.c.o: $$(subst __,/,%.c) $(MAKEFILES)
	$(CC) $(CFLAGS) $(INCLUDE) -MMD -MP -c $(subst .o,,$(subst __,/,$@)) -o $@

%.cpp.o: $$(subst __,/,%.cpp) $(MAKEFILES)
	$(CXX) $(CXXFLAGS) $(INCLUDE) -MMD -MP -c $(subst .o,,$(subst __,/,$@)) -o $@

$(CURDIR)/lib-zlibng/libz-ng.a: $(TOPDIR)/zlib-ng-1.2.11.tar.gz $(MAKEFILES)
	@[ ! -d lib-zlib ] || rm -rf lib-zlib/
	@tar zxf $(TOPDIR)/zlib-ng-1.2.11.tar.gz
	@mv zlib-ng-1.2.11/ lib-zlibng/
	@cd lib-zlibng/ && ./configure --static && cd ..
	@$(MAKE) -C $(CURDIR)/lib-zlibng/ -f $(CURDIR)/lib-zlibng/Makefile libz-ng.a

endif

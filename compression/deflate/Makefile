ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

export OUTDIR	:=	$(CURDIR)/src/main/resources/net/daporkchop/lib/compression/deflate/libdeflate
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

-include $(OFILES:.o=.d)

build: $(OUTDIR)/$(BUILD).$(EXT)
	@echo '$(OUTDIR)/$(BUILD).$(EXT) is ready!'

$(OUTDIR)/$(BUILD).$(EXT): $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a $(MAKEFILES)
	@[ -d $(OUTDIR) ] || mkdir -p $(OUTDIR)
#	$(LD) $(LDFLAGS) -o $@ $(OFILES) $(CURDIR)/lib-zlib/libz-ng.a
	$(LD) $(LDFLAGS) -ldeflate -o $@ $(OFILES)

#%.cpp.o: $(CURDIR)/lib-zlib/libz-ng.a $$(subst __,/,%.cpp) $(MAKEFILES)
%.cpp.o: $$(subst __,/,%.cpp) $(MAKEFILES)
	$(CXX) $(CXXFLAGS) $(INCLUDE) -MMD -MP -c $(subst .o,,$(subst __,/,$@)) -o $@

#$(CURDIR)/lib-zlib/libz-ng.a: $(TOPDIR)/zlib-ng-1.2.11.tar.gz $(MAKEFILES)
#	@[ ! -d lib-zlib ] || rm -rf lib-zlib/
#	@tar zxf $(TOPDIR)/zlib-ng-1.2.11.tar.gz
#	@mv zlib-ng-1.2.11/ lib-zlib/
#	@cd lib-zlib/ && ./configure --static && cd ..
##	@cd lib-zlib/ && ./configure --static --with-gzfileops && cd ..
#	@$(MAKE) -C $(CURDIR)/lib-zlib/ -f $(CURDIR)/lib-zlib/Makefile libz-ng.a

endif

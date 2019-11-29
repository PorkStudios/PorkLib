BUILDDIR	:=	build/$(BUILD)

ifneq ($(BUILD),$(notdir $(CURDIR)))
.PHONY: $(BUILD) clean

clean:
	@rm -rf build/ tmp/

$(BUILD): $(CURDIR)/lib-zlib/zlib.h
	@[ -d $(BUILDDIR) ] || mkdir -p $(BUILDDIR)
	@$(MAKE) --no-print-directory -C $(BUILDDIR) -f $(CURDIR)/Makefile BUILD=$(BUILD) zlib-$(BUILD)

$(CURDIR)/lib-zlib/zlib.h:
	@echo "Downloading zlib sources..."
	@wget --no-proxy -O - https://cloud.daporkchop.net/programs/source/zlib-1.2.11.tar.gz | tar zxf -
	@mv zlib-1.2.11/ lib-zlib/

else
.PHONY: zlib-$(BUILD)

SOURCES			:=	$(SOURCES) $(PROJDIR)/source $(PROJDIR)/lib-zlib

CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.c'))
CPPFILES		:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -maxdepth 1 -type f -name '*.cpp'))
OFILES			:=	$(addsuffix .o,$(subst /,__,$(CFILES)) $(subst /,__,$(CPPFILES)))

CFLAGS			:=	$(CFLAGS) -D__LARGEFILE64__SOURCE=1 -DHAVE__HIDDEN -DPIC
INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(PROJDIR)/include

zlib-$(BUILD): $(OUTDIR)/$(BUILD)/libzlib.so

$(OUTDIR)/$(BUILD)/libzlib.so: $(CFILES) $(CPPFILES) $(OFILES)
	@[ -d $(OUTDIR)/$(BUILD) ] || mkdir -p $(OUTDIR)/$(BUILD)
	@echo "Linking $@..."
	@$(LD) $(LDFLAGS) $(INCLUDE) -o $@ $(OFILES)

%.c.o: $(CPPFILES)
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CC) $(CFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

%.cpp.o: $(CFILES)
	@echo "Building $(subst .o,,$(subst __,/,$@))..."
	@$(CXX) $(CXXFLAGS) $(INCLUDE) -c $(subst .o,,$(subst __,/,$@)) -o $@

endif

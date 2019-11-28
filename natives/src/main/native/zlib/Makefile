ifneq (build,$(notdir $(CURDIR)))

export INCLUDE			:=	$(foreach dir,$(INCLUDES),-I$(dir)) -I$(CURDIR)/include

export SOURCES			:=	$(SOURCES) $(CURDIR)/source
export CFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -type f -name '*.c'))
export CPPFILES			:=	$(foreach dir,$(SOURCES),$(shell find -L $(dir) -type f -name '*.cpp'))

export CFILES_COMP		:=  $(subst /,_,$(CFILES))
export CPPFILES_COMP	:=  $(subst /,_,$(CPPFILES))
export OFILES			:=	$(CFILES_COMP:.c=.c.o) $(CPPFILES_COMP:.cpp=.cpp.o)

export CFLAGS	:=	$(CFLAGS) $(INCLUDE)
export CXXFLAGS	:=	$(CXXFLAGS) $(INCLUDE)
export LDFLAGS	:=	$(LDFLAGS) $(INCLUDE)

.PHONY: build clean

clean:
	@rm -rf build

build:
	@echo $(CFILES)
	@echo $(CFILES_COMP)
	@echo $(OFILES)
	@[ -d $@ ] || mkdir -p $@
	@$(MAKE) --no-print-directory -C build -f $(CURDIR)/Makefile

else

all: $(TOPDIR)/src/main/resources/libzlib.so

$(TOPDIR)/src/main/resources/libzlib.so: $(OFILES)
	@echo $(dir)
	@echo $(OFILES)
	$(LD) $(LDFLAGS) -o $@ $(OFILES)

%.c.o:
	$(CC) $(CFLAGS) -c $(subst .c.o,.c,$(subst _,/,$@)) -o $@

%.cpp.o:
	$(CXX) $(CXXFLAGS) -c $(subst .cpp.o,.cpp,$(subst _,/,$@)) -o $@

endif

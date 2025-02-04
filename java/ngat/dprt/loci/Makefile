# Makefile
# $Header$
include ../../../../../Makefile.common
include	../../../../Makefile.common

PACKAGEDIR	= ngat/dprt/loci
BINDIR 		= $(DPRT_BIN_HOME)/java/$(PACKAGEDIR)

SRCS 		= DpRtLibrary.java
OBJS 		= $(SRCS:%.java=$(BINDIR)/%.class)

top: $(OBJS)

$(BINDIR)/%.class:%.java
	$(JAVAC) -source 1.4 -target 1.4 -sourcepath .:/home/dev/src:$(DPRT_SRC_HOME)/java \
		-classpath $(DPRT_BIN_HOME)/java:$(CLASSPATH) $(JAVAC_OPTIONS) -d $(DPRT_BIN_HOME)/java $<

checkout:
	$(CO) $(CO_OPTIONS) $(SRCS)

checkin:
	-$(CI) $(CI_OPTIONS) $(SRCS)

clean: tidy
	-$(RM) $(RM_OPTIONS) $(OBJS)

tidy:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)

backup: tidy

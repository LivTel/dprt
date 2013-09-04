# Makefile
# $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/ioi/Makefile,v 1.1 2013-09-04 15:18:57 cjm Exp $ 
include ../../../../../Makefile.common
include	../../../../Makefile.common

PACKAGEDIR	= ngat/dprt/ioi
BINDIR 		= $(DPRT_BIN_HOME)/java/$(PACKAGEDIR)

SRCS 		= DpRtLibrary.java
OBJS 		= $(SRCS:%.java=$(BINDIR)/%.class)

top: $(OBJS)

$(BINDIR)/%.class:%.java
	$(JAVAC) -sourcepath .:/home/dev/src:$(DPRT_SRC_HOME)/java \
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

#
# $Log: not supported by cvs2svn $
#

# Makefile -*- mode: Fundamental;-*-
# $Header: /space/home/eng/cjm/cvs/dprt/test/Makefile,v 1.1 2001-08-10 13:06:54 cjm Exp $ 

include ../../Makefile.common
include ../Makefile.common

BINDIR 		= $(DPRT_BIN_HOME)/test
DOCSDIR 	= $(DPRT_DOC_HOME)/test_javadocs
DOCFLAGS 	= -version -author -private
SRCS 		= DpRtTestTCPClientConnectionThread.java SendDpRtCommand.java
OBJS 		= $(SRCS:%.java=$(BINDIR)/%.class) 
DOCS 		= $(SRCS:%.java=$(DOCSDIR)/%.html)

top: $(OBJS) docs

$(BINDIR)/%.class:%.java
	$(JAVAC) $(JAVAC_OPTIONS) -d $(BINDIR) $<

docs: $(DOCS)

$(DOCS): $(SRCS)
	$(JAVADOC) -d $(DOCSDIR) $(DOCFLAGS) $(SRCS)

checkout:
	$(CO) $(CO_OPTIONS) $(SRCS)

checkin:
	-$(CI) $(CI_OPTIONS) $(SRCS)

clean: tidy
	-$(RM) $(RM_OPTIONS) $(OBJS) $(BINDIR)/*.obj

tidy:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)

backup: tidy

# $Log: not supported by cvs2svn $


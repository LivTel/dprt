# Makefile -*- mode: Fundamental;-*-
# $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/Makefile,v 0.1 1999-06-24 10:56:30 dev Exp $ 
include ../../Makefile.common

INCDIR = ../include
DOCSDIR = ../javadocs
DOCFLAGS = -version -author -private
SRCS = DpRtLibrary.java DpRtStatus.java DpRtTCPServer.java DpRtTCPServerConnectionThread.java DpRtConstants.java DpRt.java
OBJS = $(SRCS:%.java=%.class)
DOCS = $(SRCS:%.java=$(DOCSDIR)/%.html)

top: $(OBJS) $(DOCS)

DpRt.class: DpRt.java
	$(JAVAC) $(JAVAC_OPTIONS) DpRt.java

DpRtStatus.class: DpRtStatus.java
	$(JAVAC) $(JAVAC_OPTIONS) DpRtStatus.java

DpRtTCPServer.class: DpRtTCPServer.java
	$(JAVAC) $(JAVAC_OPTIONS) DpRtTCPServer.java

DpRtTCPServerConnectionThread.class: DpRtTCPServerConnectionThread.java DpRtConstants.class
	$(JAVAC) $(JAVAC_OPTIONS) DpRtTCPServerConnectionThread.java

DpRtConstants.class: DpRtConstants.java
	$(JAVAC) $(JAVAC_OPTIONS) DpRtConstants.java

DpRtLibrary.class: DpRtLibrary.java
	$(JAVAC) $(JAVAC_OPTIONS) DpRtLibrary.java
	$(JAVAH) -jni DpRtLibrary
	$(MV) DpRtLibrary.h $(INCDIR)

$(DOCS): $(SRCS)
	$(JAVADOC) -d $(DOCSDIR) $(DOCFLAGS) $(SRCS)

checkout:
	$(CO) $(CO_OPTIONS) $(SRCS)

checkin:
	$(CI) $(SRCS)

clean:
	$(RM) $(RM_OPTIONS) $(OBJS) $(TIDY_OPTIONS) *.fits

tidy:
	$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS) *.fits

#
# $Log: not supported by cvs2svn $
#

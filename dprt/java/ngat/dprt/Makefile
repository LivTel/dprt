# Makefile -*- mode: Fundamental;-*-
# $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/Makefile,v 0.7 2000-07-06 14:35:41 cjm Exp $ 
include ../../Makefile.common
include ../Makefile.common

INCDIR 		= $(DPRT_SRC_HOME)/include
BINDIR 		= $(DPRT_BIN_HOME)/java
DOCSDIR 	= $(DPRT_DOC_HOME)/javadocs
DOCFLAGS 	= -version -author -private
SRCS = DpRtLibrary.java DpRtStatus.java DpRtTCPServer.java DpRtTCPServerConnectionThread.java DpRtConstants.java DpRt.java
OBJS 		= $(SRCS:%.java=$(BINDIR)/%.class)
DOCS 		= $(SRCS:%.java=$(DOCSDIR)/%.html)

top: $(OBJS) $(INCDIR)/DpRtLibrary.h config docs

$(BINDIR)/%.class:%.java
	$(JAVAC) $(JAVAC_OPTIONS) -d $(BINDIR) $<

DpRtTCPServerConnectionThread.class: DpRtConstants.class

$(INCDIR)/DpRtLibrary.h: $(BINDIR)/DpRtLibrary.class
	$(JAVAH) -jni -d $(INCDIR) -classpath $(BINDIR):$(CLASSPATH) DpRtLibrary
	$(TOUCH) $@

config: $(BINDIR)/dprt.properties

$(BINDIR)/dprt.properties: $(HOST).dprt.properties
	$(CP) $(HOST).dprt.properties $(BINDIR)/dprt.properties

docs: $(DOCS)

$(DOCS): $(SRCS)
	$(JAVADOC) -d $(DOCSDIR) $(DOCFLAGS) $(SRCS)

checkout:
	$(CO) $(CO_OPTIONS) $(SRCS)

checkin:
	-$(CI) $(CI_OPTIONS) $(SRCS)

clean:
	-$(RM) $(RM_OPTIONS) $(OBJS) $(TIDY_OPTIONS)

tidy:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)

backup: tidy

#
# $Log: not supported by cvs2svn $
# Revision 0.6  1999/12/10 12:15:51  cjm
# Added backup target.
#
# Revision 0.5  1999/09/08 13:53:31  cjm
# Changed checkin target to accept failure (due to files not being checked out locked).
# Changed checkin target to use CI_OPTIONS.
#
# Revision 0.4  1999/08/24 16:31:32  cjm
# Changed for new directory struct on ltccd1.
# Changed Java compilation so that it uses a generic rather than specific rule.
# Changed Javah command so dependancies are better.
#
# Revision 0.3  1999/06/30 15:44:18  dev
# Deleteing error/log file for tidy target for backup target
#
# Revision 0.2  1999/06/30 15:39:51  dev
# Makefile tidies latex directory for backup
#
# Revision 0.1  1999/06/24 10:56:30  dev
# initial revision
#
#

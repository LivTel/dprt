# Makefile
# $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/Makefile,v 0.10 2004-03-31 08:49:17 cjm Exp $ 
include ../../../../Makefile.common
include ../../../Makefile.common

MAKE 		= make
PACKAGEDIR	= ngat/dprt/
BINDIR 		= $(DPRT_BIN_HOME)/java/$(PACKAGEDIR)
CONFIG_BINDIR 	= $(DPRT_BIN_HOME)/java
DOCSDIR 	= $(DPRT_DOC_HOME)/javadocs/$(PACKAGEDIR)
DOCFLAGS 	= -version -author -private
SRCS 		= DpRtLibraryInterface.java DpRtLibraryNativeException.java \
		DpRtStatus.java DpRtTCPServer.java DpRtTCPServerConnectionThread.java \
		DpRtConstants.java DpRt.java DpRtREBOOTQuitThread.java
OBJS 		= $(SRCS:%.java=$(BINDIR)/%.class)
DOCS 		= $(SRCS:%.java=$(DOCSDIR)/%.html)

CONFIG_SRCS	= ltccd1.livjm.ac.uk.dprt.properties ltccd2.livjm.ac.uk.dprt.properties \
		ltccd3.livjm.ac.uk.dprt.properties \
		ftccd1.livjm.ac.uk.dprt.properties ltobs9.livjm.ac.uk.dprt.properties
CONFIG_OBJS	= $(CONFIG_SRCS:%.properties=$(CONFIG_BINDIR)/%.properties)

DIRS 		= ccs 
#scs supircam ftspec

top: $(OBJS) config
	@for i in $(DIRS); \
	do \
		(echo making in $$i...; cd $$i; $(MAKE) ); \
	done;

$(BINDIR)/%.class:%.java
	$(JAVAC) -sourcepath .:/home/dev/src:$(DPRT_SRC_HOME)/java -classpath \
		$(DPRT_BIN_HOME)/java:$(CLASSPATH)$(JAVAC_OPTIONS) -d $(DPRT_BIN_HOME)/java $<

DpRtTCPServerConnectionThread.class: DpRtConstants.class

config: $(CONFIG_OBJS)

$(CONFIG_BINDIR)/%.properties: %.properties
	$(CP) $< $@

checkout:
	$(CO) $(CO_OPTIONS) $(SRCS)
	@for i in $(DIRS); \
	do \
		(echo checkout in $$i...; cd $$i; $(CO) $(CO_OPTIONS) Makefile; $(MAKE) checkout); \
	done;

checkin:
	-$(CI) $(CI_OPTIONS) $(SRCS)
	-@for i in $(DIRS); \
	do \
		(echo checkin in $$i...; cd $$i; $(MAKE) checkin; $(CI) $(CI_OPTIONS) Makefile); \
	done;

clean:
	-$(RM) $(RM_OPTIONS) $(OBJS) $(TIDY_OPTIONS)
	@for i in $(DIRS); \
	do \
		(echo clean in $$i...; cd $$i; $(MAKE) clean); \
	done;

tidy:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)
	@for i in $(DIRS); \
	do \
		(echo tidy in $$i...; cd $$i; $(MAKE) tidy); \
	done;

backup: tidy
	@for i in $(DIRS); \
	do \
		(echo backup in $$i...; cd $$i; $(MAKE) backup); \
	done;

#
# $Log: not supported by cvs2svn $
# Revision 0.9  2001/05/16 16:15:46  cjm
# Split into libdprt and dprt project sub-directories.
#
# Revision 0.8  2001/03/09 16:22:49  cjm
# Added DpRtREBOOTQuitThread.java source.
#
# Revision 0.7  2000/07/06 14:35:41  cjm
# Added touch to javah command.
#
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

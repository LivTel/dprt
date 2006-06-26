# Makefile
# $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/Makefile,v 0.17 2006-06-26 15:53:26 cjm Exp $ 
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

CONFIG_SRCS	= ltccd1.dprt.properties ltccd2.dprt.properties ltccd3.dprt.properties \
		ftccd1.dprt.properties ftccd2.dprt.properties ltobs9.dprt.properties \
		supircam1.dprt.properties supircam2.dprt.properties \
		ftspec1.dprt.properties ftspec2.dprt.properties \
		spectrograph.dprt.properties nuview1.dprt.properties ratcam1.dprt.properties
CONFIG_OBJS	= $(CONFIG_SRCS:%.properties=$(CONFIG_BINDIR)/%.properties)

DIRS 		= ccs scs supircam ftspec grope

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
# Revision 0.16  2005/11/29 15:42:59  cjm
# Added ratcam1 configs, and grope dprtlibrary.
#
# Revision 0.15  2004/10/13 19:54:18  cjm
# Added supircam2.
#
# Revision 0.14  2004/07/06 18:03:24  cjm
# Added nuview1.dprt.properties.
#
# Revision 0.13  2004/05/24 17:56:40  cjm
# Added ftccd2.
#
# Revision 0.12  2004/04/19 17:58:49  cjm
# Added spectrograph.dprt.properties.
#
# Revision 0.11  2004/04/14 15:44:03  cjm
# Added scs, supircam and ftspec directories, and configs for supircam1 and ftspec1.
#
# Revision 0.10  2004/03/31 08:49:17  cjm
# Repackaged into ngat.dprt.
#
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

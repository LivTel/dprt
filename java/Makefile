# Makefile
# $Header: /space/home/eng/cjm/cvs/dprt/java/Makefile,v 1.2 2004-04-15 13:56:04 cjm Exp $ 
include ../Makefile.common
include ../../Makefile.common

MAKE 		= make
DIRS 		= ngat
JAR_FILE       	= ngat_dprt.jar
LIBDIR		= $(DPRT_BIN_HOME)/java/
JARLIBDIR	= $(LT_BIN_HOME)/javalib

DOCSDIR 	= $(DPRT_DOC_HOME)/javadocs/
DOCFLAGS 	= -version -author -private
JAVASRCS 	= ngat/dprt/*.java ngat/dprt/ccs/*.java ngat/dprt/scs/*.java \
		ngat/dprt/supircam/*.java ngat/dprt/ftspec/*.java 
JAVAPKGS	= ngat.dprt ngat.dprt.ccs ngat.dprt.scs ngat.dprt.supircam ngat.dprt.ftspec
JAVADOCS 	= $(JAVASRCS:%.java=$(DOCSDIR)/%.html)

top: dirs javadocs

dirs:
	@for i in $(DIRS); \
	do \
		(echo making in $$i...; cd $$i; $(MAKE) ); \
	done;

jar: 
	( cd $(LIBDIR); $(JAR) $(JAR_OPTIONS) $(JARLIBDIR)/$(JAR_FILE) ngat )

#
# Do all javadocs at top level, to ensure javadoc creates package links correctly
#
javadocs: $(JAVADOCS)

$(JAVADOCS): $(JAVASRCS)
	$(JAVADOC) -sourcepath /home/dev/src:$(DPRT_SRC_HOME)/java -d $(DOCSDIR) $(DOCFLAGS) \
		-link http://java.sun.com/j2se/1.4.2/docs/api/ $(JAVAPKGS)

checkin:
	-@for i in $(DIRS); \
	do \
		(echo checkin in $$i...; cd $$i; $(MAKE) checkin; $(CI) $(CI_OPTIONS) Makefile); \
	done;

checkout:
	@for i in $(DIRS); \
	do \
		(echo checkout in $$i...; cd $$i; $(CO) $(CO_OPTIONS) Makefile; $(MAKE) checkout); \
	done;

depend:
	echo no depend in ngat directory.

clean:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)
	-$(RM) $(RM_OPTIONS) $(JARLIBDIR)/$(JAR_FILE)
	@for i in $(DIRS); \
	do \
		(echo clean in $$i...; cd $$i; $(MAKE) clean); \
	done;
	$(RM) $(RM_OPTIONS) $(JAVADOCS)
	$(RM) $(RM_OPTIONS) -r $(DOCSDIR)/*

tidy:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)
	@for i in $(DIRS); \
	do \
		(echo tidy in $$i...; cd $$i; $(MAKE) tidy); \
	done;

backup: checkin
	@for i in $(DIRS); \
	do \
		(echo backup in $$i...; cd $$i; $(MAKE) backup); \
	done;

#
# $Log: not supported by cvs2svn $
# Revision 1.1  2004/03/31 08:49:42  cjm
# Initial revision
#
# Revision 1.1  2004/03/29 11:18:06  cjm
# Initial revision
#
#

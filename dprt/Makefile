# Makefile -*- mode: Fundamental;-*-
# $Header: /space/home/eng/cjm/cvs/dprt/Makefile,v 1.2 2001-05-15 16:41:06 cjm Exp $ 

include ../Makefile.common
include Makefile.common

DIRS = java latex

top:
	@for i in $(DIRS); \
	do \
		(echo making in $$i...; cd $$i; $(MAKE) ); \
	done;

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
	echo checkout in c...; cd c; $(MAKE) depend

clean:
	$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)
	@for i in $(DIRS); \
	do \
		(echo clean in $$i...; cd $$i; $(MAKE) clean); \
	done;

tidy:
	$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)
	@for i in $(DIRS); \
	do \
		(echo tidy in $$i...; cd $$i; $(MAKE) tidy); \
	done;

# because of the checkin dependancy, you couldn't call make in a subdirectory in this rule.
# Now that CI_OPTIONS is -u the Makefiles are left checked out unlocked, so you can now.
backup: tidy checkin
	@for i in $(DIRS); \
	do \
		(echo backup in $$i...; cd $$i; $(MAKE) backup); \
	done;
	tar cvf $(BACKUP_DIR)/dprt.tar .
	compress $(BACKUP_DIR)/dprt.tar

# $Log: not supported by cvs2svn $
# Revision 1.1  1999/12/10 12:26:20  cjm
# Initial revision
#

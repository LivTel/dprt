# $Header: /space/home/eng/cjm/cvs/dprt/latex/Makefile,v 0.3 1999-12-10 12:26:02 cjm Exp $

include ../../Makefile.common
include ../Makefile.common

DOCSDIR 	= $(DPRT_DOC_HOME)/latex

TEXSRCS 	= dprt.tex
FIGSRCS 	= dprt_dirtree.fig dprt_program_structure.fig dprt_execution.fig

DVIOBJS		= $(TEXSRCS:%.tex=%.dvi)
PSOBJS		= $(TEXSRCS:%.tex=$(DOCSDIR)/%.ps)

EPSOBJS		= $(FIGSRCS:%.fig=%.eps)

top: $(PSOBJS)

$(DOCSDIR)/%.ps: %.dvi
	$(DVIPS) $< -o
	$(MV) $*.ps $(DOCSDIR)

$(DVIOBJS): $(TEXSRCS) $(EPSOBJS)

%.dvi: %.tex
	$(LATEX) $<
	$(LATEX) $<
	$(LATEX) $<

%.eps: %.fig
	$(FIG2DEV) -L ps $< $@

depend:
	echo "NO depend rule for latex files."

clean:
	-$(RM) $(RM_OPTIONS) $(DVIOBJS) $(PSOBJS) $(EPSOBJS) $(LATEX_TIDY_OPTIONS) $(TIDY_OPTIONS)

tidy:
	-$(RM) $(RM_OPTIONS) $(LATEX_TIDY_OPTIONS) $(TIDY_OPTIONS)

backup: tidy
	-$(RM) $(RM_OPTIONS) $(DVIOBJS) $(EPSOBJS)

checkin:
	-$(CI) $(CI_OPTIONS) $(TEXSRCS) $(FIGSRCS)

checkout:
	-$(CO) $(CO_OPTIONS) $(TEXSRCS) $(FIGSRCS)

#
# $Log: not supported by cvs2svn $
# Revision 0.2  1999/09/08 13:59:10  cjm
# Changed checkin target to use CI_OPTIONS.
#
# Revision 0.1  1999/08/24 15:48:30  cjm
# initial revision.
#
#

# DO NOT DELETE

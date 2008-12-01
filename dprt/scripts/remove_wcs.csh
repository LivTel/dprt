#!/bin/tcsh
# $Header: /space/home/eng/cjm/cvs/dprt/scripts/remove_wcs.csh,v 1.3 2008-12-01 14:41:54 cjm Exp $
# remove_wcs.csh <FITS filename> [<FITS filename> ...]
# This strips out the headers created by pipe_astrometric.csh and restores the 
# file very nearly to a pristine state. It is not quite identical to the 
# original. Keywords may end up in a slightly different order and certain
# keyword comments have been changed. It is however back in a state where
# you can run pipe_astrometric.csh again and should get an indentical result
# Based on Dr. Bobs remove_wcs.csh - now without Minks routines.
# $Id: remove_wcs.csh,v 1.3 2008-12-01 14:41:54 cjm Exp $

if ( $#argv < 1 ) then
    /bin/echo "remove_wcs.csh <FITS filename> [<FITS filename> ...]"
    exit 1
endif

# PATH etc
if ( -d /icc/bin/ccd/misc/${HOSTTYPE} ) then
    set BINDIR = /icc/bin/ccd/misc/${HOSTTYPE}
else if ( -d /dprt/bin/ccd/misc/${HOSTTYPE} ) then
    set BINDIR = /dprt/bin/ccd/misc/${HOSTTYPE}
else if ( -d ~dev/bin/ccd/misc/${HOSTTYPE} ) then
    set BINDIR = ~dev/bin/ccd/misc/${HOSTTYPE}
else
    /bin/echo "remove_wcs.csh : Failed to find a binary dir."
    exit 2
endif
setenv PATH ${PATH}:${BINDIR}
# static or dynamic binaries
set static = ""
#set static = "_static"

foreach cleanfile ( $argv )
    /bin/echo "remove_wcs.csh : Removing WCS from ${cleanfile}."
    set wra = `fits_get_keyword_value${static} ${cleanfile} WRA STRING`
    set fits_status = $status
    if( "${fits_status}" != 0 ) then
	/bin/echo  "remove_wcs.csh : Getting WRA from ${cleanfile} failed."
	exit 3
    endif
    set wdec = `fits_get_keyword_value${static} ${cleanfile} WDEC STRING`
    set fits_status = $status
    if( "${fits_status}" != 0 ) then
	/bin/echo  "remove_wcs.csh : Getting WDEC from ${cleanfile} failed."
	exit 4
    endif
    /bin/echo  "remove_wcs.csh : ${cleanfile} has WRA ${wra} WDEC ${wdec}"
    fits_add_keyword_value${static} ${cleanfile} RA STRING ${wra}
    set fits_status = $status
    if( "${fits_status}" != 0 ) then
	/bin/echo  "remove_wcs.csh : Setting RA in ${cleanfile} failed."
	exit 5
    endif
    fits_add_keyword_value${static} ${cleanfile} DEC STRING ${wdec}
    set fits_status = $status
    if( "${fits_status}" != 0 ) then
	/bin/echo  "remove_wcs.csh : Setting DEC in ${cleanfile} failed."
	exit 6
    endif
    foreach keyword (WRA WDEC PIXSCALE SECPIX CTYPE1 CTYPE2 LONPOLE CRPIX1 CRPIX2 CRVAL1 CRVAL2 CROTA1 CROTA2 CUNIT1 CUNIT2 CDELT1 CDELT2 CD1_1 CD1_2 CD2_1 CD2_2 IMWCS WCSRFCAT WCSIMCAT WCSNREF WCSMATCH WCREFCAT WCSRDRES WCSDELRA WCSDELDE WCSDELRO WCSDELPO WCSSEP WCS_ERR WROTSKY)
	fits_delete_keyword_value${static} ${cleanfile} ${keyword}
	if( "${fits_status}" != 0 ) then
	    /bin/echo  "remove_wcs.csh : Removing ${keyword} in ${cleanfile} failed."
	    exit 7
	endif
    end
end
#
# $Log: not supported by cvs2svn $
# Revision 1.2  2007/09/26 10:07:45  cjm
# Rewritten using my CFITSIO tools, to stop Minkisms.
# Added removal of WROTSKY.
#
# Revision 1.1  2007/09/25 14:48:35  cjm
# Initial revision
#
#


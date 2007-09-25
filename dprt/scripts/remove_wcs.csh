#!/bin/tcsh
# $Header: /space/home/eng/cjm/cvs/dprt/scripts/remove_wcs.csh,v 1.1 2007-09-25 14:48:35 cjm Exp $
# remove_wcs.csh <FITS filename> [<FITS filename> ...]
# This strips out the headers created by pipe_astrometric.csh and restores the 
# file very nearly to a pristine state. It is not quite identical to the 
# original. Keywords may end up in a slightly different order and certain
# keyword comments have been changed. It is however back in a state where
# you can run pipe_astrometric.csh again and should get an indentical result
# $Id: remove_wcs.csh,v 1.1 2007-09-25 14:48:35 cjm Exp $

#MYBIN
if ( -d /icc/wcs_fit/bin/ ) then
    setenv MYBIN /icc/wcs_fit/bin/
else if ( -d /usr/local/bin/wcs/ ) then
    setenv MYBIN /usr/local/bin/wcs/
else
	/bin/echo "remove_wcs.csh : Failed to find a binary dir."
	exit 1
endif

foreach cleanfile ( $argv )
    /bin/echo "remove_wcs.csh : Removing WCS from ${cleanfile}."
    ${MYBIN}sethead $cleanfile   RA=`${MYBIN}gethead $cleanfile WRA` / '[HH:MM:SS.sss] Catalog RA of target'
    ${MYBIN}sethead $cleanfile  DEC=`${MYBIN}gethead $cleanfile WDEC` / '[DD:MM:SS.ss] Catalog DEC of target'
    ${MYBIN}delhead $cleanfile WRA WDEC
    ${MYBIN}delhead $cleanfile PIXSCALE SECPIX
    ${MYBIN}delhead $cleanfile CTYPE1 CTYPE2 LONPOLE CRPIX1 CRPIX2 CRVAL1 CRVAL2
    ${MYBIN}delhead $cleanfile CROTA1 CROTA2 CUNIT1 CUNIT2 CDELT1 CDELT2
    ${MYBIN}delhead $cleanfile CD1_1 CD1_2 CD2_1 CD2_2
    ${MYBIN}delhead $cleanfile IMWCS WCSRFCAT WCSIMCAT WCSNREF WCSMATCH WCREFCAT
    ${MYBIN}delhead $cleanfile WCSRDRES WCSDELRA WCSDELDE WCSDELRO WCSDELPO WCSSEP WCS_ERR
end
#
# $Log: not supported by cvs2svn $
#


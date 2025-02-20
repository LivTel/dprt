#!/bin/tcsh
# To transform the USNOB local raw to the final *.usnob
# scri.csh <arcmin_search> <RA> <DEC> 

if( ${#argv} != 4 ) then
    /bin/echo ""
    /bin/echo "Arguments required: RA(hh:mm:ss.ss) DEC(dd:mm:ss.s)  radius(arcmin)  outfile"
    /bin/echo ""
    exit 1
endif

set RA  = "$1"
set DEC = "$2"
set SID = "$3"
set FIL = "$4"
set i=1

if ( ! ${?MYBIN} ) then
    if ( -d /icc/wcs_fit/bin/ ) then
	setenv MYBIN /icc/wcs_fit/bin/
    else if ( -d /dprt/wcs_fit/bin/ ) then
	setenv MYBIN /dprt/wcs_fit/bin/
    else if ( -d /usr/local/bin/wcs/ ) then
	setenv MYBIN /usr/local/bin/wcs/
    else
	/bin/echo "get_local_usnob.csh : Failed to find a binary dir."
	exit 2
    endif
endif

#
# Which awk/grep shall we use?
# Linux : /bin/awk is good
# Solaris (7/8) : /bin/awk does not have advanced printf functionality (used by rd.awk), /usr/xpg4/bin/awk does
#
set os_name = `/bin/uname`
if ( "${os_name}" == "SunOS" ) then
    set awk_bin = "/usr/xpg4/bin/awk"
    set grep_bin = "/usr/xpg4/bin/grep"
else if ( "${os_name}" == "Linux" ) then
    set awk_bin = "/bin/awk"
    set grep_bin = "/bin/grep"
else
	/bin/echo "get_local_usnob.csh : Unknown OS name : ${os_name}."
	exit 6
endif

# TMP_DIR
if ( -d /home/dev/tmp ) then
	set base_tmp_dir = /home/dev/tmp
    else if ( -d /icc/tmp ) then
	set base_tmp_dir = /icc/tmp
    else if ( -d /dprt/tmp ) then
	set base_tmp_dir = /dprt/tmp
    else
	/bin/echo "get_local_usnob.csh : Failed to find a base tmp dir."
	exit 3
endif
set TMP_DIR = ${base_tmp_dir}/wcs

# If USNOB_LOCAL_DIR is defined, it means that the USNOB local copy is stored therein.
if ( ${?USNOB_LOCAL_DIR} ) then
    setenv RUT ${USNOB_LOCAL_DIR}
else
    /bin/echo "get_local_usnob.csh : Failed to find USNOB_LOCAL_DIR."
    exit 4
endif

if ( ! -d ${RUT} ) then
    /bin/echo "get_local_usnob.csh : USNOB Directory is not correct: ${RUT}"
    exit 5
endif

# Fix DEC so it has a '+'
# usnob1 translates Decs like 5:34:46 as 0! +5:34:46 is OK
/bin/echo ${DEC} | ${grep_bin} -q '[+-]' 
set grep_status = $status
if ( ${grep_status} != 0 ) then
    set DEC = '+'"${DEC}"
endif

/bin/echo "get_local_usnob.csh : ${MYBIN}usnob1 -m 8000 -v -R ${RUT} -s a -b ${SID} -c ${RA} ${DEC} >! ${TMP_DIR}/${FIL:r:t}.ori"
${MYBIN}usnob1 -m 8000 -v -R ${RUT} -s a -b ${SID} -c ${RA} ${DEC} >! ${TMP_DIR}/${FIL:r:t}.ori

setenv N_DET `${awk_bin} '/#--- / {print $2}' ${TMP_DIR}/${FIL:r:t}.ori `

setenv CZZ `/bin/echo "$SID / 60." | /usr/bin/bc -l`

/bin/echo "get_local_usnob.csh : Detected: ${N_DET}"

/bin/echo "#FIELD   = 'LTGRBField'                 /Field Name" >! ${FIL}
/bin/echo "#CAT_ID  = 'USNOB'                      /Catalogue ID" >> ${FIL}  
/bin/echo "#NOBJECTS= ${N_DET}                          /Number of Objects In file" >> ${FIL}
/bin/echo "#EQUINOX = J2000.0 2000.00              /Equinox, Epoch" >> ${FIL}
/bin/echo "#RA      = ${RA}                   /Center RA  [hh:mm:ss]" >> ${FIL} 
/bin/echo "#DEC     = ${DEC}                  /Center DEC [dd:mm:ss]" >> ${FIL}
/bin/echo "#DRA     = ${CZZ}        /Box Width  [deg]" >> ${FIL} 
/bin/echo "#DDEC    = ${CZZ}        /Box Height [deg]" >> ${FIL}
/bin/echo "#MAGNITUD= 'R2'                         /Primary Sort Color" >> ${FIL}       
/bin/echo "#BR_MAG  = 0.00                         /Bright Magnitude Limit [mag]">> ${FIL}
/bin/echo "#FT_MAG  = 100.00                       /Faint Magnitude Limit [mag]" >> ${FIL}
/bin/echo "#COLOR   = 'B2-R2'                      /Color0 minus Color1 (C0-C1)" >> ${FIL}
/bin/echo "#BR_MIN  = -100.00                      /Color Limit: Min. C0-C1 [mag]" >> ${FIL}
/bin/echo "#BR_MAX  = 100.00                       /Color Limit: Max. C0-C1 [mag]"  >> ${FIL}
/bin/echo "#MU_MIN  = 0.00                         /Min. Proper Motion [mas/yr]" >> ${FIL}
/bin/echo "#MU_MAX  = 10000.00                     /Max. Proper Motion [mas/yr]" >> ${FIL}
/bin/echo "#SPOS_MIN= 0.00                         /Min. Position Error [mas]" >> ${FIL}
/bin/echo "#SPOS_MAX= 10000.00                     /Max. Position Error [mas]" >> ${FIL}
/bin/echo "#SMU_MIN = 0.00                         /Min. Proper Motion Error [mas/yr]" >> ${FIL}
/bin/echo "#SMU_MAX = 10000.00                     /Max. Proper Motion Error [mas/yr]" >> ${FIL}
/bin/echo "#SD_MIN  = 0.00                         /Minimum Surface Density [N/deg^2]" >> ${FIL}
/bin/echo "#SD_MAX  = 10000000.00                  /Maximum Surface Density [N/deg^2]" >> ${FIL}
/bin/echo "#SEP_MIN = 0.000000 0.00                /Minimum Separation [deg] & [mag]" >> ${FIL}
/bin/echo "#END" >> ${FIL}                                                                          
/bin/echo "#          RA|          DEC|   MuRA|  MuDEC|     B1|     R1|     B2|     R2|     I2|" >> ${FIL}
/bin/echo "#    hh:mm:ss|     dd:mm:ss| mas/yr| mas/yr|    mag|    mag|    mag|    mag|    mag|" >> ${FIL}
/bin/echo "#           1|            2|      3|      4|      5|      6|      7|      8|      9|" >> ${FIL}


${awk_bin} -f ${MYBIN}rd.awk  ${TMP_DIR}/${FIL:r:t}.ori >> ${FIL}

/usr/bin/test -f ${TMP_DIR}/${FIL:r:t}.ori && /bin/rm -f ${TMP_DIR}/${FIL:r:t}.ori

exit 0
#
# $Log: not supported by cvs2svn $
# Revision 1.5  2008/12/01 14:38:23  cjm
# Added /dprt path.
#
# Revision 1.4  2007/09/26 12:52:51  cjm
# Changed method of determining whether to add a '+' to Dec, so it
# works under Solaris as well.
#
# Revision 1.3  2007/09/26 12:26:31  cjm
# Added awk line to ensure Dec has either a '+' or '-'.
# usnob1 does not understand a Dec without one of them.
#
# Revision 1.2  2007/09/24 15:41:20  cjm
# Rewritten using TMP_DIR, correct awk version for rd.awk.
#
# Revision 1.1  2007/09/24 13:31:23  cjm
# Initial revision
#
#

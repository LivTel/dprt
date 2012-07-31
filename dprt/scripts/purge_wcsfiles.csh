#!/bin/tcsh

#
#
# 2007-01-28 RJS
# This script cleans out the WCS temporary working directory.
# Files which are deleted by this script are originally created by 
# 	run_wcs_pipe.csh and pipe_astrometric.csh - The science data WCS fitting based in Mink's wcstool
#	skycam_wcs fitting for the SkyCam, based on astrometry.net
#
# There are two age limits. 
# Catalogue files get deleted when they excede $PURGE_AGE days
# Verbose debug output from wcstools and all astrometry.net temp files get deleted when $PURGE_DEBUG_AGE days old.
#
# The point of this is that catalogues are worth hanging onto and may get re-used.
# Debug output can ben purged much sooner since it was probably only generated
# during specific testing.
#
#
# The only command line parameter is -debug and is optional
#
# Editable parameters :
# 	PURGE_AGE
# 	PURGE_DEBUG_AGE
#	WCS_TMP_DIR
#	DEBUG
#


#set DEBUG=1
set DEBUG=0
if("$1" == "-debug") set DEBUG=1


# Need to decide which machine we are running on and how it is configured
# The following code is lifted directly from wcs_fit so we make the same
# decisions here as in that script.
if ( -d /home/dev/tmp ) then
	set base_tmp_dir = /home/dev/tmp
else if ( -d /icc/tmp ) then
	set base_tmp_dir = /icc/tmp
else if ( -d /dprt/tmp ) then
	set base_tmp_dir = /dprt/tmp
else if ( -d /data/Dprt ) then
	set base_tmp_dir = /data/Dprt
else
	/bin/echo "`now_date_string` : $procname : Failed to find a base tmp dir." 
	exit 27
endif
set WCS_TMP_DIR = ${base_tmp_dir}/wcs
if (-e $WCS_TMP_DIR) then
  cd $WCS_TMP_DIR
  if ($DEBUG) echo "Entered $WCS_TMP_DIR ready to start scan"
else
  echo "WCS working directory $WCS_TMP_DIR does nor exist. Exiting purge script."
  exit 2
endif



# Delete WCS working files if they are older than $PURGE_AGE days
# Delete debug output if is is older than $PURGE_DEBUG_AGE days
set PURGE_AGE = 365
set PURGE_DEBUG_AGE = 7
@ PURGE_AGE_SEC=$PURGE_AGE * 86400
@ PURGE_DEBUG_AGE_SEC=$PURGE_DEBUG_AGE * 86400

set today_sec=`date +%s`

# -------------------------------------------------------------------------------- 
# First section deletes routine logs, debug output and the temp files which Astrometry.Net
# is rather prone to leaving lyng around.
# If you run WCS in debug mode it leaves all kinds of diagnositc junk behind.
# Astrometry.Net is poor at cleaning up its temp files and frequently leaves them, partiularly when fits fail.
# Delete it all if it is more than PURGE_DEBUG_AGE 
# --------------------------------------------------------------------------------
set count = `ls -1 astnet.tmp.* tmp1.* tmp2.* tmp3.* tmp4.* wcs_out_log.* ?_?_20* |& grep -v "No match" | wc -l`
if ($DEBUG) echo "File count of debug temporary files : $count"
if ($count > 0) then
  foreach fn ( astnet.tmp.* tmp1.* tmp2.* tmp3.* tmp4.* wcs_out_log.* ?_?_20* )
    set fileage=`date +%s -r $fn`
    @ age_sec=$today_sec - $fileage
    if ( $age_sec > $PURGE_DEBUG_AGE_SEC ) then
      rm -f $fn
      if ($DEBUG) echo "Deleting file $fn with age $age_sec"
    else
      if ($DEBUG) echo "Keeping file $fn with age $age_sec"
    endif
  end
endif

# -------------------------------------------------------------------------------- 
# This section is repeat of the above but deletes the USNO-B and 2MASS
# catalogue files retained on disk during normal operation of the WCS scriprts.
# if their age excedes $PURGE_AGE_SEC.
# Note that the WCS script deliberately touches these files whenever it
# uses them so that a file looked at only once per $PURGE_AGE will not
# get deleted. Assuming $PURGE_AGE is set to 1 year, a long term monitoring
# target should not get deleted even if it is only available in one semester.
# --------------------------------------------------------------------------------
set count = `ls -1 *.usnob *.2mass *.starb *.starb.fov *.nomad |& grep -v "No match" | wc -l`
if ($DEBUG) echo "File count : $count"
if ($count > 0) then
  foreach fn ( *.usnob *.2mass *.starb *.starb.fov *.nomad)
    set fileage=`date +%s -r $fn`
    @ age_sec=$today_sec - $fileage
    if ( $age_sec > $PURGE_AGE_SEC ) then
      rm -f $fn
      if ($DEBUG) echo "Deleting file $fn with age $age_sec"
    else
      if ($DEBUG) echo "Keeping file $fn with age $age_sec"
    endif
  end
endif


exit 0



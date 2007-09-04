/*   
    Copyright 2006, Astrophysics Research Institute, Liverpool John Moores University.

    This file is part of DpRt.

    DpRt is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    DpRt is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DpRt; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
// DpRtLibrary.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/ccs/DpRtLibrary.java,v 0.8 2007-09-04 13:01:38 cjm Exp $
package ngat.dprt.ccs;

import java.lang.*;
import java.io.*;

import ngat.dprt.*;
import ngat.fits.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;
import ngat.util.*;
import ngat.util.logging.*;

/**
 * This class supports a JNI interface to the Data Pipeline (Real Time) C library for real time
 * FITS file data reduction, for the CCD Control System (CCS).
 * @author Chris Mottram LJMU
 * @version $Revision: 0.8 $
 */
public class DpRtLibrary implements DpRtLibraryInterface
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtLibrary.java,v 0.8 2007-09-04 13:01:38 cjm Exp $");
	/**
	 * Instance of FITS filename handling code, for lock file/ pipeline processing code.
	 */
	protected FitsFilename fitsFilename = null;
	/**
	 * The logger for this DpRtLibrary.
	 */
	protected Logger logger = null;
	/**
	 * The instance of ngat.util.ExecuteCommand used to run any WCS scripts.
	 */
	protected ExecuteCommand executeCommand = null;
	/**
	 * Copy of the DpRt status object, used for property retrieval.
	 */
	protected DpRtStatus status = null;

// DpRt.h
	/**
	 * Native wrapper to a libdprt routine that initialises the logger reference.
	 */
	private native void initialiseLoggerReference(Logger l);
	/**
	 * Native wrapper to a libdprt routine that finalises the logger reference.
	 */
	private native void finaliseLoggerReference();
	/**
	 * Native wrapper to a libdprt routine that finalizes any global references the C layer uses.
	 */
	private native void DpRt_Finalise_References();
	/**
	 * Native wrapper to a libdprt routine that stores the DpRtStatus reference. This is used
	 * to retrieve properties from the Java property file from the C layer.
	 * @param status The instance of DpRtStatus.
	 */
	private native void DpRt_Set_Status(DpRtStatus status);
	/**
	 * Native wrapper to a libdprt routine that performs any initialisation the C layer needs.
	 */
	private native void DpRt_Initialise() throws DpRtLibraryNativeException;
	/**
	 * Native wrapper to a libdprt routine that performs any finalisation the C layer needs.
	 */
	private native void DpRt_Shutdown() throws DpRtLibraryNativeException;
	/**
	 * Native wrapper to a libdprt routine to reduce an input calibrate FITS image.
	 * @param inputFilename A string giving the location of the input filename.
	 * @param reduceDone A calibrate reduce done object, whose fields should be filled in by the reduction process.
 	 * @return The routine returns true if the JNI calls were completed successfully, and 
	 * 	the done object was suitably filled in. The actual process might have failed, but so long
	 *	as the error number/string was placed into the done object successfully, the routine
 	 * 	returns true.
	 */
	private native boolean DpRt_Calibrate_Reduce(String inputFilename,CALIBRATE_REDUCE_DONE calibrateReduceDone);
	/**
	 * Native wrapper to a libdprt routine to reduce an input expose FITS image.
	 * @param inputFilename A string giving the location of the input filename.
	 * @param reduceDone An expose reduce done object, whose fields should be filled in by the reduction process.
 	 * @return The routine returns true if the JNI calls were completed successfully, and 
	 * 	the done object was suitably filled in. The actual process might have failed, but so long
	 *	as the error number/string was placed into the done object successfully, the routine
 	 * 	returns true.
	 */
	private native boolean DpRt_Expose_Reduce(String inputFilename,EXPOSE_REDUCE_DONE exposeReduceDone);
	/**
	 * Native wrapper to a libdprt routine to create a master bias FITS image.
	 * @param dirname The directory name to look for Bias frames in.
	 * @param makeBiasDone A done object, whose fields should be filled in by the process.
 	 * @return The routine returns true if the JNI calls were completed successfully, and 
	 * 	the done object was suitably filled in. The actual process might have failed, but so long
	 *	as the error number/string was placed into the done object successfully, the routine
 	 * 	returns true.
	 */
	private native boolean DpRt_Make_Master_Bias(String dirname,MAKE_MASTER_BIAS_DONE makeBiasDone);
	/**
	 * Native wrapper to a libdprt routine to create a master flat FITS image.
	 * @param dirname The directory name to look for flat frames in.
	 * @param makeFlatDone A done object, whose fields should be filled in by the process.
 	 * @return The routine returns true if the JNI calls were completed successfully, and 
	 * 	the done object was suitably filled in. The actual process might have failed, but so long
	 *	as the error number/string was placed into the done object successfully, the routine
 	 * 	returns true.
	 */
	private native boolean DpRt_Make_Master_Flat(String dirname,MAKE_MASTER_FLAT_DONE makeFlatDone) ;
	/**
	 * Native wrapper to a libdprt routine to abort the reduction of a FITS image.
	 */
	private native void DpRt_Abort();

	/**
	 * Static code to load libdprt, the shared C library that implements an the real time data reduction.
	 */
	static
	{
		System.loadLibrary("dprt_ccs");
	}

	/**
	 * Constructor. Calls the super class constructor, and initialiseLoggerReference to set
	 * the C layer's reference to the logger (for logging to Java from C).
	 * Fits filename instance is constructed.
	 * @see #initialiseLoggerReference
	 * @see #fitsFilename
	 * @see #logger
	 * @see ngat.util.logging.LogManager#getLogger
	 */
	public DpRtLibrary()
	{
		super();
		initialiseLoggerReference(LogManager.getLogger(this));
		logger = LogManager.getLogger(this);
		fitsFilename = new FitsFilename();
	}

	/**
	 * Finalize method for this class, delete JNI global references, and logger references.
	 * @see #DpRt_Finalise_References
	 * @see #finaliseLoggerReference
	 */
	protected void finalize() throws Throwable
	{
		super.finalize();
		DpRt_Finalise_References();
		finaliseLoggerReference();
	}

// DpRt.h
	/**
	 * Method to set the libraries reference to the DpRt Status object.
	 * This is used when querying properties from the C and Java layer.
	 * This method should be called after DpRtLibrary instance has been constructed, before initialise
	 * (which may wish to query the properties). 
	 * @param status The instance of DpRtStatus to use.
	 * @see #DpRt_Set_Status
	 * @see #initialise
	 * @see #status
	 */
	public void setStatus(DpRtStatus s)
	{
		status = s;
		DpRt_Set_Status(s);
	}

	/**
	 * Method called after construction of the DpRtLibrary instance, to allow the C layer
	 * to perform any initialisation it requires.
	 * @see #DpRt_Initialise
	 */
	public void initialise() throws DpRtLibraryNativeException
	{
		DpRt_Initialise();
	}

	/**
	 * Method called just before stopping the DpRt, to allow the C layer
	 * to perform any finalisation it requires.
	 * @see #DpRt_Shutdown
	 */
	public void shutdown() throws DpRtLibraryNativeException
	{
		DpRt_Shutdown();
	}

	/**
	 * Routine to call to reduce a calibration FITS file. The filename is processed. The resultant data is passed
	 * back to the Java layer in the calibrateReduceDone object, which is then sent over the network to
	 * tell the client the process has been completed.
	 * <p>This method now also calls lockReducedFilename and unlockReducedFilename. These create a lock
	 * file based on the input filename, so that a script monitoring dprt output can tell when we have
	 * finished with a reduced filename.
	 * @param inputFilename The string representation of the filename.
	 * @param calibrateReduceDone The calibrate reduce done object, 
	 * 	that will be filled in with the processed filename and
	 * 	useful data the data pipeline has extracted (peak and mean counts).
	 * @see #lockReducedFilename
	 * @see #unlockReducedFilename
	 */
	public void DpRtCalibrateReduce(String inputFilename,CALIBRATE_REDUCE_DONE calibrateReduceDone) 
	{
		lockReducedFilename(inputFilename);
		// DpRt_Calibrate_Reduce is a Java Native routine that actually does the work.
		// It will return TRUE even when the Data Pipeline failed - the calibrateReduceDone object
		// contains the getSuccessful and getErrorNum routine to determine whether the 
		// Data Pipeline failed.
		// However this routine can also fail in the JNI interface area when copying the parameters/results
		// from C to Java. In this case the routine will return FALSE to inform us not all the
		// fields in reduceDone could be set successfully. We can then take steps to fix this.
		if(DpRt_Calibrate_Reduce(inputFilename,calibrateReduceDone) == false)
		{
			calibrateReduceDone.setSuccessful(false);
			calibrateReduceDone.setErrorNum(1);
			calibrateReduceDone.setErrorString(this.getClass().getName()+
				":DpRtCalibrateReduce:JNI interface failed.");
		}
		unlockReducedFilename(inputFilename);
	}

	/**
	 * Routine to call to reduce an expose FITS file. The filename is processed. The resultant data is passed
	 * back to the Java layer in the exposeReduceDone object, which is then sent over the network to
	 * tell the client the process has been completed.
	 * <p>This method now also calls lockReducedFilename and unlockReducedFilename. These create a lock
	 * file based on the input filename, so that a script monitoring dprt output can tell when we have
	 * finished with a reduced filename.
	 * @param inputFilename The string representation of the filename.
	 * @param wcsFit A boolean, if true invoke something that tries to WCS fit the reduced image.
	 * @param exposeReduceDone The expose reduce done object, 
	 * 	that will be filled in with the processed filename and
	 * 	useful data the data pipeline has extracted (FWHM, Counts and location of brightest object etc).
	 * @see #lockReducedFilename
	 * @see #unlockReducedFilename
	 * @see #getProcessedFilename
	 * @see #wcsFit
	 */
	public void DpRtExposeReduce(String inputFilename,boolean wcsFit,EXPOSE_REDUCE_DONE exposeReduceDone)
	{
		String processedFilename = null;
		String wcsFittedFilename = null;

		lockReducedFilename(inputFilename);
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,this.getClass().getName()+
				   ":DpRtExposeReduce("+inputFilename+").");
		// DpRt_Expose_Reduce is a Java Native routine that actually does the work.
		// It will return TRUE even when the Data Pipeline failed - the exposeReduceDone object
		// contains the getSuccessful and getErrorNum routine to determine whether the 
		// Data Pipeline failed.
		// However this routine can also fail in the JNI interface area when copying the parameters/results
		// from C to Java. In this case the routine will return FALSE to inform us not all the
		// fields in exposeReduceDone could be set successfully. We can then take steps to fix this.
		if(DpRt_Expose_Reduce(inputFilename,exposeReduceDone) == false)
		{
			exposeReduceDone.setSuccessful(false);
			exposeReduceDone.setErrorNum(1);
			exposeReduceDone.setErrorString(this.getClass().getName()+
				":DpRtExposeReduce:JNI interface failed.");
			// unlock and return on error
			unlockReducedFilename(inputFilename);
			return;
		}
		// wcsFit if true
		if(wcsFit)
		{
			try
			{
				logger.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,this.getClass().getName()+
					   ":DpRtExposeReduce: Attempting wcsFit.");
				// Get L1 pipeline filename
				processedFilename = getProcessedFilename(inputFilename,
								  FitsFilename.PIPELINE_PROCESSING_FLAG_REAL_TIME);
				// call wcs Fit command
				wcsFittedFilename = wcsFit(processedFilename);
				// reset returned filename to WCS fitted one
				exposeReduceDone.setFilename(wcsFittedFilename);
				logger.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,this.getClass().getName()+
					   ":DpRtExposeReduce: Finished wcsFit.");
			}
			catch(Exception e)
			{
				logger.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,this.getClass().getName()+
					   ":DpRtExposeReduce: wcsFit failed : "+e);
				logger.dumpStack(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,e);
				exposeReduceDone.setSuccessful(false);
				exposeReduceDone.setErrorNum(1);
				exposeReduceDone.setErrorString(this.getClass().getName()+
								":DpRtExposeReduce:wcsFit failed.");
				// unlock and return on error
				unlockReducedFilename(inputFilename);
				return;
			}
		}
		else
		{
			logger.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,this.getClass().getName()+
				   ":DpRtExposeReduce:wcsFit was false: Not performing wcsFit.");
		}
		unlockReducedFilename(inputFilename);
	}

	/**
	 * Routine to call to create a master bias FITS file. Any Bias frames in the directory specified are processed.
	 * @param dirname A string containing the directory path to look for Bias frames in.
	 * @param makeBiasDone The done object, that will be filled in.
	 */
	public void DpRtMakeMasterBias(String dirname,MAKE_MASTER_BIAS_DONE makeBiasDone)
	{
		// DpRt_Make_Master_Bias is a Java Native routine that actually does the work.
		// It will return TRUE even when the Data Pipeline failed - the makeBiasDone object
		// contains the getSuccessful and getErrorNum routine to determine whether the 
		// Data Pipeline failed.
		// However this routine can also fail in the JNI interface area when copying the parameters/results
		// from C to Java. In this case the routine will return FALSE to inform us not all the
		// fields in makeBiasDone could be set successfully. We can then take steps to fix this.
		if(DpRt_Make_Master_Bias(dirname,makeBiasDone) == false)
		{
			makeBiasDone.setSuccessful(false);
			makeBiasDone.setErrorNum(1);
			makeBiasDone.setErrorString(this.getClass().getName()+
				":DpRtMakeMasterBias:JNI interface failed.");
		}
	}

	/**
	 * Routine to call to create a master flat FITS file. Any Flat frames in the directory specified are processed.
	 * @param dirname A string containing the directory path to look for Flat frames in.
	 * @param makeFlatDone The done object, that will be filled in.
	 */
	public void DpRtMakeMasterFlat(String dirname,MAKE_MASTER_FLAT_DONE makeFlatDone)
	{
		// DpRt_Make_Master_Flat is a Java Native routine that actually does the work.
		// It will return TRUE even when the Data Pipeline failed - the makeFlatDone object
		// contains the getSuccessful and getErrorNum routine to determine whether the 
		// Data Pipeline failed.
		// However this routine can also fail in the JNI interface area when copying the parameters/results
		// from C to Java. In this case the routine will return FALSE to inform us not all the
		// fields in makeFlatDone could be set successfully. We can then take steps to fix this.
		if(DpRt_Make_Master_Flat(dirname,makeFlatDone) == false)
		{
			makeFlatDone.setSuccessful(false);
			makeFlatDone.setErrorNum(1);
			makeFlatDone.setErrorString(this.getClass().getName()+
				":DpRtMakeMasterFlat:JNI interface failed.");
		}
	}

	/**
	 * Routine to abort a reduction. This is done by calling an underlying C routine to set a variable.
	 * The variable should be checked at regular intervals by the Data Pipeline process.
	 * If a command script is running (for WCS fitting, for instance), this is also aborted.
	 * @see #executeCommand
	 * @see ngat.util.ExecuteCommand#abort
	 */
	public void DpRtAbort()
	{
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+":DpRtAbort: Started.");
		DpRt_Abort();
		if(executeCommand != null)
		{
			logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":DpRtAbort: Aborting running command.");
			executeCommand.abort();
		}
	}

	/**
	 * Method to create a lock file based on the unreduced filename. Any present reduction flag
	 * is changed to PIPELINE_PROCESSING_FLAG_REAL_TIME, and the '.fits' extension changed to .lock.
	 * A blank filename is then generated.
	 * @param unreducedFilename A string containing the unreduced FITS filename. The lock file produced
	 *        should have the real time pipeline processing flag set, and have extension '.lock'.
	 * @see #getLockFilename
	 * @see #logger
	 * @see ngat.fits.FitsFilename#PIPELINE_PROCESSING_FLAG_REAL_TIME
	 * @see ngat.dprt.DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES
	 */
	protected void lockReducedFilename(String unreducedFilename)
	{
		File file = null;
		String lockFilename = null;
		boolean retval;

		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":lockReducedFilename: Generating lock filename based on "+unreducedFilename+".");
		// get lock filename
		lockFilename = getLockFilename(unreducedFilename);
		if(lockFilename != null)
		{
			file = new File(lockFilename);
			try
			{
				// NB see Java documentation, which states:
				// this method should not be used for file-locking
				retval = file.createNewFile();
				logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
					   ":lockReducedFilename: created lock : "+file+
					   " : successfully created : "+retval+".");
			}
			catch(IOException e)
			{
				logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
					   ":getLockFilename: Failed to create lock file "+file+" : "+e);
				logger.dumpStack(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,e);
			}
		}
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":lockReducedFilename: finished.");
	}

	/**
	 * Method to delete a previously created lock file based on the unreduced filename. Any present reduction flag
	 * is changed to PIPELINE_PROCESSING_FLAG_REAL_TIME, and the '.fits' extension changed to .lock.
	 * A blank filename is then generated.
	 * @param unreducedFilename A string containing the unreduced FITS filename. The lock file produced
	 *        should have the real time pipeline processing flag set, and have extension '.lock'.
	 * @see #getLockFilename
	 * @see #logger
	 * @see ngat.fits.FitsFilename#PIPELINE_PROCESSING_FLAG_REAL_TIME
	 * @see ngat.dprt.DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES
	 */
	protected void unlockReducedFilename(String unreducedFilename)
	{
		File file = null;
		String lockFilename = null;
		boolean retval;

		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":unlockReducedFilename: Generating lock filename based on "+unreducedFilename+".");
		// get lock filename
		lockFilename = getLockFilename(unreducedFilename);
		if(lockFilename != null)
		{
			file = new File(lockFilename);
			retval = file.delete();
			// Safety check in case of shutdown?
			file.deleteOnExit();
			logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":unlockReducedFilename: lock : "+file+
				   " : successfully deleted : "+retval+".");
		}
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":unlockReducedFilename: finished.");
	}

	/**
	 * Method to create a lock filename based on the unreduced filename. Any present reduction flag
	 * is changed to PIPELINE_PROCESSING_FLAG_REAL_TIME, and the '.fits' extension changed to .lock.
	 * @param unreducedFilename A string containing the unreduced FITS filename. 
	 * @return A String containing the lock filename. This
	 *        should have the real time pipeline processing flag set, and have extension '.lock'.
	 *        This method can return 'null', if a parse error occurs.
	 * @see #fitsFilename
	 * @see #logger
	 * @see ngat.fits.FitsFilename#PIPELINE_PROCESSING_FLAG_REAL_TIME
	 * @see ngat.dprt.DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES
	 */
	protected String getLockFilename(String unreducedFilename)
	{
		String lockFilename = null;

		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":getLockFilename: Generating lock filename based on "+unreducedFilename+".");
		try
		{
			fitsFilename.parse(unreducedFilename);
			fitsFilename.setPipelineProcessing(FitsFilename.PIPELINE_PROCESSING_FLAG_REAL_TIME);
			fitsFilename.setFileExtension("lock");
		}
		catch(Exception e)
		{
			logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":getLockFilename: Failed to parse "+unreducedFilename+" : "+e);
			logger.dumpStack(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,e);
			// NB lock filename not created!
			return null;
		}
		lockFilename = fitsFilename.getFilename();
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES,this.getClass().getName()+
				   ":getLockFilename: lock filename : "+lockFilename+".");
		return lockFilename;
	}

	/**
	 * Method to create a processed filename based on the unreduced filename. 
	 * @param unreducedFilename A string containing the unreduced FITS filename. 
	 * @param pipelineProcessingFlag Which processing flag to create the new filename for. Should be one of:
	 *       FitsFilename.PIPELINE_PROCESSING_FLAG_REAL_TIME, FitsFilename.PIPELINE_PROCESSING_FLAG_OFF_LINE
	 * @return A String containing the processed filename. This
	 *        should have a pipeline processing flag set.
	 *        This method can return 'null', if a parse error occurs.
	 * @exception Exception Thrown if the unreducedFilename cannot be parsed, or the pipelineProcessingFlag
	 *           is an illegal value.
	 * @see #fitsFilename
	 * @see #logger
	 * @see ngat.fits.FitsFilename#PIPELINE_PROCESSING_FLAG_REAL_TIME
	 * @see ngat.fits.FitsFilename#PIPELINE_PROCESSING_FLAG_OFF_LINE
	 * @see ngat.dprt.DpRtConstants.DPRT_LOG_LEVEL_LOCKFILES
	 */
	protected String getProcessedFilename(String unreducedFilename,String pipelineProcessingFlag) throws Exception
	{
		String processedFilename = null;

		logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
			   ":getProcessedFilename: Generating processed filename based on "+
			   unreducedFilename+".");
		fitsFilename.parse(unreducedFilename);
		fitsFilename.setPipelineProcessing(pipelineProcessingFlag);
		processedFilename = fitsFilename.getFilename();
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
				   ":getProcessedFilename: processed filename : "+processedFilename+".");
		return processedFilename;
	}

	/**
	 * Do WCS fitting on the reduced filename.
	 * @param reducedFilename The L1 (debiased/darked/flat-fielded) filename.
	 * @return The L2 (WCS fitted) FITS filename.
	 * @exception Exception Thrown if the WCS fit fails (the script fails). Or getProcessedFilename fails.
	 * @see #getProcessedFilename
	 * @see #runScript
	 * @see ngat.fits.FitsFilename#PIPELINE_PROCESSING_FLAG_OFF_LINE
	 */
	protected String wcsFit(String reducedFilename) throws Exception
	{
		String commandLine = null;
		String scriptName = null;
		String wcsFittedFilename = null;

		// Get L2 pipeline filename
		wcsFittedFilename = getProcessedFilename(reducedFilename,
							 FitsFilename.PIPELINE_PROCESSING_FLAG_OFF_LINE);
		// get script name
		scriptName = status.getProperty("dprt.wcs_fit.script_filename");
		if(scriptName == null)
		{
			throw new NullPointerException(this.getClass().getName()+":wcsFit:script filename was null.");
		}
		// construct command line
		commandLine = new String(scriptName+" "+reducedFilename+" "+wcsFittedFilename);
		// run script
		// If script fails, or returns non-zero, an exception is throw by runScript, that is propogated
		// upwards.
		runScript(commandLine);
		// return fitted filename
		return wcsFittedFilename;
	}

	/**
	 * Run a script. A new instance of executeCommand is created and the script executed. Any errors
	 * are thrown as an exception.
	 * @param commandLine The command line of the script to run.
	 * @exception Exception A rethrow of the exception generated by ExecuteCommand, or by getExitValue not being 0.
	 * @see #executeCommand
	 */
	protected void runScript(String commandLine) throws Exception
	{
		executeCommand = new ExecuteCommand(commandLine);
		//executeCommand.setCommandString(commandLine);
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
				   ":runScript: About to run: "+commandLine+".");
		executeCommand.run();
		if(executeCommand.getException() != null)
		{
			logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
				   ":runScript: Command returned exception: "+executeCommand.getException()+".");
			logger.dumpStack(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,executeCommand.getException());
			throw executeCommand.getException();
		}
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
				   ":runScript: Command returned output: "+executeCommand.getOutputString()+".");
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
				   ":runScript: Command returned error: "+executeCommand.getErrorString()+".");
		logger.log(DpRtConstants.DPRT_LOG_LEVEL_SCRIPT,this.getClass().getName()+
				   ":runScript: Command returned exit value: "+executeCommand.getExitValue()+".");
		if(executeCommand.getExitValue() != 0)
		{
			throw new Exception(this.getClass().getName()+":runScript:exit value was not zero:"+
					    executeCommand.getExitValue()+":error string:"+
					    executeCommand.getErrorString()+".");
		}
		// reset this instance of executeCommand to null.
		// This means it's abort method will not be called if DpRtAbort is called.
		executeCommand = null;
	}
}
//
// $Log: not supported by cvs2svn $
// Revision 0.7  2006/05/16 17:10:38  cjm
// gnuify: Added GNU General Public License.
//
// Revision 0.6  2005/03/31 13:19:16  cjm
// Added lockReducedFilename and unlockReducedFilename methods.
// Called when reducing calibration and exposure frames, to create a lock file
// around files that are being reduced. This is so another process
// can be monitoring the directory and transfer completely reduced FITS images elsewhere.
//
// Revision 0.5  2004/03/31 08:45:30  cjm
// Repackaged into ngat.dprt.ccs.
// DpRtLibrary now ccs specific. C library now called dprt_ccs.
// Logger reference now generated from instance (i.e. ngat.dprt.ccs.DpRtLibrary.
//
// Revision 0.4  2004/01/30 17:01:00  cjm
// Added logging support from C layer back up to Java.
//
// Revision 0.3  2002/11/26 18:49:10  cjm
// Added DpRtMakeMasterFlat, DpRtMakeMasterBias.
// Added Exception throwing.
//
// Revision 0.2  2002/05/20 14:15:42  cjm
// Added setStatus/initialise/finalize.
//
// Revision 0.1  1999/06/24 10:54:52  dev
// initial revision
//
//

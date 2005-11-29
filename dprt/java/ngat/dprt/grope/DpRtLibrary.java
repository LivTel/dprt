// DpRtLibrary.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/grope/DpRtLibrary.java,v 1.1 2005-11-29 15:42:16 cjm Exp $
package ngat.dprt.grope;

import java.lang.*;
import java.io.*;

import ngat.dprt.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;
import ngat.util.logging.*;

/**
 * This class supports a JNI interface to the Data Pipeline (Real Time) C library for real time
 * FITS file data reduction, for the Ringo Star/Grope Polarimeter.
 * @author Chris Mottram LJMU
 * @version $Revision: 1.1 $
 */
public class DpRtLibrary implements DpRtLibraryInterface
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtLibrary.java,v 1.1 2005-11-29 15:42:16 cjm Exp $");

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
		System.loadLibrary("dprt_grope");
	}

	/**
	 * Constructor. Calls the super class constructor, and initialiseLoggerReference to set
	 * the C layer's reference to the logger (for logging to Java from C).
	 * @see #initialiseLoggerReference
	 * @see ngat.util.logging.LogManager#getLogger
	 */
	public DpRtLibrary()
	{
		super();
		initialiseLoggerReference(LogManager.getLogger(this));
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
	 * This is used when querying properties from the C layer.
	 * This method should be called after DpRtLibrary instance has been constructed, before initialise
	 * (which may wish to query the properties). 
	 * @param status The instance of DpRtStatus to use.
	 * @see #DpRt_Set_Status
	 * @see #initialise
	 */
	public void setStatus(DpRtStatus status)
	{
		DpRt_Set_Status(status);
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
	 * @param inputFilename The string representation of the filename.
	 * @param calibrateReduceDone The calibrate reduce done object, 
	 * 	that will be filled in with the processed filename and
	 * 	useful data the data pipeline has extracted (peak and mean counts).
	 */
	public void DpRtCalibrateReduce(String inputFilename,CALIBRATE_REDUCE_DONE calibrateReduceDone) 
	{
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
	}

	/**
	 * Routine to call to reduce an expose FITS file. The filename is processed. The resultant data is passed
	 * back to the Java layer in the exposeReduceDone object, which is then sent over the network to
	 * tell the client the process has been completed.
	 * @param inputFilename The string representation of the filename.
	 * @param exposeReduceDone The expose reduce done object, 
	 * 	that will be filled in with the processed filename and
	 * 	useful data the data pipeline has extracted (FWHM, Counts and location of brightest object etc).
	 */
	public void DpRtExposeReduce(String inputFilename,EXPOSE_REDUCE_DONE exposeReduceDone)
	{
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
		}
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
	 */
	public void DpRtAbort()
	{
		DpRt_Abort();
	}
}
//
// $Log: not supported by cvs2svn $
// Revision 1.1  2004/04/14 15:21:09  cjm
// Initial revision
//
//

// DpRtLibrary.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/ccs/DpRtLibrary.java,v 0.2 2002-05-20 14:15:42 cjm Exp $
import java.lang.*;
import java.io.*;

import ngat.message.base.*;
import ngat.message.INST_DP.*;

/**
 * This class supports a JNI interface to the Data Pipeline (Real Time) C library for real time
 * FITS file data reduction.
 * @author Lee Howells LJMU
 * @version $Revision: 0.2 $
 */
class DpRtLibrary
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtLibrary.java,v 0.2 2002-05-20 14:15:42 cjm Exp $");

// DpRt.h
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
	private native void DpRt_Initialise();
	/**
	 * Native wrapper to a libdprt routine to reduce an input calibrate FITS image.
	 * @param inputFilename A string giving the location of the input filename.
	 * @param reduceDone A calibrate reduce done object, whose fields should be filled in by the reduction process.
	 */
	private native boolean DpRt_Calibrate_Reduce(String inputFilename,CALIBRATE_REDUCE_DONE calibrateReduceDone);
	/**
	 * Native wrapper to a libdprt routine to reduce an input expose FITS image.
	 * @param inputFilename A string giving the location of the input filename.
	 * @param reduceDone An expose reduce done object, whose fields should be filled in by the reduction process.
	 */
	private native boolean DpRt_Expose_Reduce(String inputFilename,EXPOSE_REDUCE_DONE exposeReduceDone);

	/**
	 * Native wrapper to a libdprt routine to abort the reduction of a FITS image.
	 */
	private native void DpRt_Abort();

	/**
	 * Static code to load libdprt, the shared C library that implements an the real time data reduction.
	 */
	static
	{
		System.loadLibrary("dprt");
	}

	/**
	 * Constructor.
	 */
	public DpRtLibrary()
	{
		super();
	}

	/**
	 * Finalize method for this class, delete JNI global references.
	 * @see #finaliseReferences
	 */
	protected void finalize() throws Throwable
	{
		super.finalize();
		DpRt_Finalise_References();
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
	public void initialise()
	{
		DpRt_Initialise();
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
// Revision 0.1  1999/06/24 10:54:52  dev
// initial revision
//
//

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
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/ioi/DpRtLibraryScript.java,v 1.1 2025-02-03 13:15:51 cjm Exp $
package ngat.dprt.ioi;

import java.lang.*;
import java.io.*;

import ngat.dprt.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;
import ngat.util.logging.*;

/**
 * This class supports a mechanism for calling a command line script to process data.
 * @author Chris Mottram LJMU
 * @version $Revision: 1.1 $
 */
public class DpRtLibraryScript implements DpRtLibraryInterface
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtLibraryScript.java,v 1.1 2025-02-03 13:15:51 cjm Exp $");
	/**
	 * A reference to the DpRt's status object. Can be used to query properties from the DpRt's property file.
	 */
	protected DpRtStatus status = null;

	/**
	 * Constructor. Calls the super class constructor.
	 * @see ngat.util.logging.LogManager#getLogger
	 */
	public DpRtLibraryScript()
	{
		super();
		//LogManager.getLogger(this);
	}

	/**
	 * Finalize method for this class.
	 */
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	/**
	 * Method to set the libraries reference to the DpRt Status object.
	 * This is used when querying properties from the C layer.
	 * This method should be called after DpRtLibrary instance has been constructed, before initialise
	 * (which may wish to query the properties). 
	 * @param status The instance of DpRtStatus to use.
	 * @see #initialise
	 * @see #status
	 */
	public void setStatus(DpRtStatus status)
	{
		this.status = status;
	}

	/**
	 * Method called after construction of the DpRtLibrary instance, to perform any initialisation it requires.
	 */
	public void initialise() throws DpRtLibraryNativeException
	{
		// retrieve scripts using status?
	}

	/**
	 * Method called just before stopping the DpRt, to perform any finalisation it requires.
	 */
	public void shutdown() throws DpRtLibraryNativeException
	{
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
		// call script
		//calibrateReduceDone.setMeanCounts(float in);
		//calibrateReduceDone.setPeakCounts(float in);
		//calibrateReduceDone.setFilename(String s);
		calibrateReduceDone.setSuccessful(false);
		calibrateReduceDone.setErrorNum(1);
		calibrateReduceDone.setErrorString(this.getClass().getName()+
						   ":DpRtCalibrateReduce:JNI interface failed.");
	}

	/**
	 * Routine to call to reduce an expose FITS file. The filename is processed. The resultant data is passed
	 * back to the Java layer in the exposeReduceDone object, which is then sent over the network to
	 * tell the client the process has been completed.
	 * @param inputFilename The string representation of the filename.
	 * @param wcsFit A boolean, if true invoke something that tries to WCS fit the reduced image.
	 * @param exposeReduceDone The expose reduce done object, 
	 * 	that will be filled in with the processed filename and
	 * 	useful data the data pipeline has extracted (FWHM, Counts and location of brightest object etc).
	 */
	public void DpRtExposeReduce(String inputFilename,boolean wcsFit,EXPOSE_REDUCE_DONE exposeReduceDone)
	{
		// call script
		//exposeReduceDone.setCounts(float in);
		//exposeReduceDone.setPhotometricity(float in); 
		//exposeReduceDone.setSaturation(boolean in);
		//exposeReduceDone.setSeeing(float in);
		//exposeReduceDone.setSkyBrightness(float in);
		//exposeReduceDone.setXpix(float in);
		//exposeReduceDone.setYpix(float in);
		//exposeReduceDone.setFilename(String s);
		exposeReduceDone.setSuccessful(false);
		exposeReduceDone.setErrorNum(1);
		exposeReduceDone.setErrorString(this.getClass().getName()+
						   ":DpRtCalibrateReduce:JNI interface failed.");

	}

	/**
	 * Routine to call to create a master bias FITS file. 
	 * @param dirname A string containing the directory path to look for Bias frames in.
	 * @param makeBiasDone The done object, that will be filled in.
	 */
	public void DpRtMakeMasterBias(String dirname,MAKE_MASTER_BIAS_DONE makeBiasDone)
	{
		makeBiasDone.setSuccessful(false);
		makeBiasDone.setErrorNum(1);
		makeBiasDone.setErrorString(this.getClass().getName()+
					    ":DpRtMakeMasterBias:JNI interface failed.");
	}

	/**
	 * Routine to call to create a master flat FITS file. Any Flat frames in the directory specified are processed.
	 * @param dirname A string containing the directory path to look for Flat frames in.
	 * @param makeFlatDone The done object, that will be filled in.
	 */
	public void DpRtMakeMasterFlat(String dirname,MAKE_MASTER_FLAT_DONE makeFlatDone)
	{
		makeFlatDone.setSuccessful(false);
		makeFlatDone.setErrorNum(1);
		makeFlatDone.setErrorString(this.getClass().getName()+
					    ":DpRtMakeMasterFlat:JNI interface failed.");
	}

	/**
	 * Routine to abort a reduction. This should kill the running script if possible.
	 */
	public void DpRtAbort()
	{
	}
}
//
// $Log: not supported by cvs2svn $
//

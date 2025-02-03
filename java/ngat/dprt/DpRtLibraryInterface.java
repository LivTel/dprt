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
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtLibraryInterface.java,v 1.3 2007-08-21 15:21:14 cjm Exp $
package ngat.dprt;

import java.lang.*;
import java.io.*;

import ngat.message.base.*;
import ngat.message.INST_DP.*;
import ngat.util.logging.*;

/**
 * This class supports a generic JNI interface to the Data Pipeline (Real Time) C library for real time
 * FITS file data reduction.
 * Classes implementing this interface link to the specific DpRt C library for a particular instrument.
 * @author Chris Mottram LJMU
 * @version $Revision: 1.3 $
 */
public interface DpRtLibraryInterface
{
	/**
	 * Method to set the libraries reference to the DpRt Status object.
	 * This method should be called after DpRtLibrary instance has been constructed, before initialise
	 * (which may wish to query the properties via the status object). 
	 * @param status The instance of DpRtStatus to use.
	 * @see #initialise
	 */
	public void setStatus(DpRtStatus status);
	/**
	 * Method called after construction of the DpRtLibrary instanceto perform any initialisation required.
	 */
	public void initialise() throws DpRtLibraryNativeException;
	/**
	 * Method called just before stopping the DpRt, to perform any finalisation required.
	 */
	public void shutdown() throws DpRtLibraryNativeException;
	/**
	 * Routine to call to reduce a calibration FITS file. The filename is processed. The resultant data is passed
	 * back to the Java layer in the calibrateReduceDone object, which is then sent over the network to
	 * tell the client the process has been completed.
	 * @param inputFilename The string representation of the filename.
	 * @param calibrateReduceDone The calibrate reduce done object, 
	 * 	that will be filled in with the processed filename and
	 * 	useful data the data pipeline has extracted (peak and mean counts).
	 */
	public void DpRtCalibrateReduce(String inputFilename,CALIBRATE_REDUCE_DONE calibrateReduceDone);
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
	public void DpRtExposeReduce(String inputFilename,boolean wcsFit,EXPOSE_REDUCE_DONE exposeReduceDone);
	/**
	 * Routine to call to create a master bias FITS file. Any Bias frames in the directory specified are processed.
	 * @param dirname A string containing the directory path to look for Bias frames in.
	 * @param makeBiasDone The done object, that will be filled in.
	 */
	public void DpRtMakeMasterBias(String dirname,MAKE_MASTER_BIAS_DONE makeBiasDone);
	/**
	 * Routine to call to create a master flat FITS file. Any Flat frames in the directory specified are processed.
	 * @param dirname A string containing the directory path to look for Flat frames in.
	 * @param makeFlatDone The done object, that will be filled in.
	 */
	public void DpRtMakeMasterFlat(String dirname,MAKE_MASTER_FLAT_DONE makeFlatDone);
	/**
	 * Routine to abort a reduction.
	 */
	public void DpRtAbort();
}
//
// $Log: not supported by cvs2svn $
// Revision 1.2  2006/05/16 17:09:35  cjm
// gnuify: Added GNU General Public License.
//
// Revision 1.1  2004/03/31 08:40:01  cjm
// Initial revision
//
//

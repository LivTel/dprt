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
// DpRtLibraryNativeException.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtLibraryNativeException.java,v 1.3 2006-05-16 17:09:36 cjm Exp $
package ngat.dprt;

/**
 * This class extends Exception. Objects of this class are thrown when the underlying C code in DpRtLibrary produces an
 * error. The individual parts of the error generated are stored in the exception.
 * The JNI interface itself can also generate these exceptions.
 * @author Chris Mottram
 * @version $Revision: 1.3 $
 */
public class DpRtLibraryNativeException extends Exception
{
	/**
	 * Revision Control System id string, showing the version of the Class
	 */
	public final static String RCSID = new String("$Id: DpRtLibraryNativeException.java,v 1.3 2006-05-16 17:09:36 cjm Exp $");
	/**
	 * The current value of the error number in the DpRtLibrary.c module.
	 */
	protected int DpRtLibraryErrorNumber = 0;
	/**
	 * The error string from the DpRtLibrary.c module.
	 */
	private String DpRtLibraryErrorString = null;
	/**
	 * The current value of the error number in the dprt.c module.
	 */
	protected int DpRtErrorNumber = 0;
	/**
	 * The error string from the dprt.c module.
	 */
	private String DpRtErrorString = null;

	/**
	 * Constructor for the exception.
	 * @param dprtLibraryErrorNumber The error number from DpRtLibrary.c.
	 * @param dprtLibraryErrorString The error string from DpRtLibrary.c.
	 * @param dprtErrorNumber The error number from dprt.c.
	 * @param dprtErrorString The error string from dprt.c.
	 * @see #DpRtLibraryErrorNumber
	 * @see #DpRtLibraryErrorString
	 * @see #DpRtErrorNumber
	 * @see #DpRtErrorString
	 */
	public DpRtLibraryNativeException(int dprtLibraryErrorNumber,String dprtLibraryErrorString,
						int dprtErrorNumber,String dprtErrorString)
	{
		super();
		this.DpRtLibraryErrorString = dprtLibraryErrorString;
		this.DpRtLibraryErrorNumber = dprtLibraryErrorNumber;
		this.DpRtErrorString = dprtErrorString;
		this.DpRtErrorNumber = dprtErrorNumber;
	}

	/**
	 * Retrieve routine for the error number for the DpRtLibrary module.
	 * @return Returns the DpRtLibraryErrorNumber supplied for this exception.
	 * @see #DpRtLibraryErrorNumber
	 */
	public int getDpRtLibraryErrorNumber()
	{
		return DpRtLibraryErrorNumber;
	}

	/**
	 * Retrieve routine for the DpRtLibrary error string supplied to the exception.
	 * @return Returns the DpRtLibraryErrorString for this exception.
	 * @see #DpRtLibraryErrorString
	 */
	public String getDpRtLibraryErrorString()
	{
		return DpRtLibraryErrorString;
	}

	/**
	 * Retrieve routine for the error number for the dprt.c module.
	 * @return Returns the DpRtErrorNumber supplied for this exception.
	 * @see #DpRtErrorNumber
	 */
	public int getDpRtErrorNumber()
	{
		return DpRtErrorNumber;
	}

	/**
	 * Retrieve routine for the error string supplied to the exception from the dprt.c module.
	 * @return Returns the DpRtErrorString for this exception.
	 * @see #DpRtErrorString
	 */
	public String getDpRtErrorString()
	{
		return DpRtErrorString;
	}

	/**
	 * Method overridding the default toString method. 
	 * It returns a new string, comprising the class name, error numbers and error strings.
	 * @see #DpRtLibraryErrorNumber
	 * @see #DpRtLibraryErrorString
	 * @see #DpRtErrorNumber
	 * @see #DpRtErrorString
	 */
	public String toString()
	{
		return new String(this.getClass().getName()+"\n"+
			"DpRtLibrary("+DpRtLibraryErrorNumber+"):"+DpRtLibraryErrorString+"\n"+
			"DpRt("+DpRtErrorNumber+"):"+DpRtErrorString+"\n");
	}
}
//
// $Log: not supported by cvs2svn $
// Revision 1.2  2004/03/31 08:40:01  cjm
// Repackaged into ngat.dprt.
//
// Revision 1.1  2004/03/30 09:22:23  cjm
// Initial revision
//
//

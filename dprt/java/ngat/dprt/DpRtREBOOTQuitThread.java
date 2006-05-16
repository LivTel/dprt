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
// DpRtREBOOTQuitThread.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtREBOOTQuitThread.java,v 1.3 2006-05-16 17:09:37 cjm Exp $
package ngat.dprt;

import java.lang.*;
import java.io.*;

/**
 * This class is a thread that is started when the DpRt is to terminate.
 * A thread is passed in, which must terminate before System.exit is called.
 * This is used in, for instance, the REBOOTImplementation, so that the 
 * REBOOT's DONE mesage is returned to the client before the DpRt is terminated.
 * @author Chris Mottram
 * @version $Revision: 1.3 $
 */
public class DpRtREBOOTQuitThread extends Thread
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtREBOOTQuitThread.java,v 1.3 2006-05-16 17:09:37 cjm Exp $");
	/**
	 * The Thread, that has to terminatre before this thread calls System.exit
	 */
	private Thread waitThread = null;
	/**
	 * Field holding the instance of the dprt currently executing, used to access error handling routines etc.
	 */
	private DpRt dprt = null;

	/**
	 * The constructor.
	 * @param name The name of the thread.
	 */
	public DpRtREBOOTQuitThread(String name)
	{
		super(name);
	}

	/**
	 * Routine to set this objects pointer to the dprt object.
	 * @param c The dprt object.
	 */
	public void setDpRt(DpRt c)
	{
		this.dprt = c;
	}

	/**
	 * Method to set a thread, such that this thread will not call System.exit until
	 * that thread has terminated.
	 * @param t The thread to wait for.
	 * @see #waitThread
	 */
	public void setWaitThread(Thread t)
	{
		waitThread = t;
	}

	/**
	 * Run method, called when the thread is started.
	 * If the waitThread is non-null, we try to wait until it has terminated.
	 * System.exit(0) is then called.
	 * @see #waitThread
	 */
	public void run()
	{
		if(waitThread != null)
		{
			try
			{
				waitThread.join();
			}
			catch (InterruptedException e)
			{
				dprt.error(this.getClass().getName()+":run:"+e);
			}
		}
		System.exit(0);
	}
}
//
// $Log: not supported by cvs2svn $
// Revision 1.2  2004/03/31 08:40:01  cjm
// Repackaged into ngat.dprt.
//
// Revision 1.1  2001/03/09 16:22:11  cjm
// Initial revision
//
//

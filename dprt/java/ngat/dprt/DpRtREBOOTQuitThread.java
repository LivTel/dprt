// DpRtREBOOTQuitThread.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtREBOOTQuitThread.java,v 1.2 2004-03-31 08:40:01 cjm Exp $
package ngat.dprt;

import java.lang.*;
import java.io.*;

/**
 * This class is a thread that is started when the DpRt is to terminate.
 * A thread is passed in, which must terminate before System.exit is called.
 * This is used in, for instance, the REBOOTImplementation, so that the 
 * REBOOT's DONE mesage is returned to the client before the DpRt is terminated.
 * @author Chris Mottram
 * @version $Revision: 1.2 $
 */
public class DpRtREBOOTQuitThread extends Thread
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtREBOOTQuitThread.java,v 1.2 2004-03-31 08:40:01 cjm Exp $");
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
// Revision 1.1  2001/03/09 16:22:11  cjm
// Initial revision
//
//

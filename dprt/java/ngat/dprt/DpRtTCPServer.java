// DpRtTCPServer.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtTCPServer.java,v 0.2 2004-01-30 17:01:00 cjm Exp $
import java.lang.*;
import java.io.*;
import java.net.*;

import ngat.net.*;

/**
 * This class extends the TCPServer class for the DpRt application.
 * @author Chris Mottram, LJMU
 * @version $Revision: 0.2 $
 */
public class DpRtTCPServer extends TCPServer
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtTCPServer.java,v 0.2 2004-01-30 17:01:00 cjm Exp $");
	/**
	 * Field holding the instance of the dprt currently executing, so we can pass this to spawned threads.
	 */
	private DpRt dprt = null;

	/**
	 * The constructor.
	 */
	public DpRtTCPServer(String name,int portNumber)
	{
		super(name,portNumber);
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
	 * This routine spawns threads to handle connection to the server. This routine
	 * spawns <a href="DpRtTCPServerConnectionThread.html">DpRtTCPServerConnectionThread</a> threads.
	 * The routine also sets the new threads priority to higher than normal. This makes the thread
	 * reading it's command a priority so we can quickly determine whether the thread should
	 * continue to execute at a higher priority.
	 */
	public void startConnectionThread(Socket connectionSocket)
	{
		DpRtTCPServerConnectionThread thread = null;

		thread = new DpRtTCPServerConnectionThread(connectionSocket);
		thread.setDpRt(dprt);
		thread.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_INTERRUPT);
		thread.start();
	}

}

//
// $Log: not supported by cvs2svn $
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//

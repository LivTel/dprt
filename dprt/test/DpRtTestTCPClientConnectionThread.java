// DpRtTestTCPClientConnectionThread.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/test/DpRtTestTCPClientConnectionThread.java,v 1.1 2001-08-10 13:07:02 cjm Exp $

import java.lang.*;
import java.io.*;
import java.net.*;

import ngat.net.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;

/**
 * The DpRtTestTCPClientConnectionThread extends TCPClientConnectionThreadMA. 
 * It implements the generic JMS command protocol.
 * @author Chris Mottram
 * @version $Revision: 1.1 $
 * @see ngat.net.TCPClientConnectionThreadMA
 */
public class DpRtTestTCPClientConnectionThread extends TCPClientConnectionThreadMA
{
	/**
	 * A constructor for this class. Currently just calls the parent class's constructor.
	 */
	public DpRtTestTCPClientConnectionThread(InetAddress address,int portNumber,COMMAND c)
	{
		super(address,portNumber,c);
	}

	/**
	 * This routine processes the acknowledge object returned by the server. Currently
	 * prints out a message, giving the time to completion if the acknowledge was not null.
	 */
	protected void processAcknowledge()
	{
		if(acknowledge == null)
		{
			System.err.println(this.getClass().getName()+":processAcknowledge:"+
				command.getClass().getName()+":acknowledge was null.");
			return;
		}
		System.err.println(this.getClass().getName()+":processAcknowledge:"+
			command.getClass().getName()+":time:"+acknowledge.getTimeToComplete());
	}

	/**
	 * This routine processes the done object returned by the server. Currently prints out
	 * the basic return values in done.
	 */
	protected void processDone()
	{

		if(done == null)
		{
			System.err.println(this.getClass().getName()+":processDone:"+
				command.getClass().getName()+":done was null.");
			return;
		}
		System.err.println(this.getClass().getName()+":processDone:"+
			command.getClass().getName()+":done has"+
			"\n\terror Number:"+done.getErrorNum()+
			"\n\terror String:"+done.getErrorString()+
			"\n\tsuccessful:"+done.getSuccessful());
	}
}

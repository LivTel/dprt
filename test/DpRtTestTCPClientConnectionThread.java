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
// DpRtTestTCPClientConnectionThread.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/test/DpRtTestTCPClientConnectionThread.java,v 1.3 2006-05-16 16:55:37 cjm Exp $

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
 * @version $Revision: 1.3 $
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
//
// $Log: not supported by cvs2svn $
// Revision 1.2  2001/08/10 13:07:42  cjm
// Added log comment.
//
//

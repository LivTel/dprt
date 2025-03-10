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
// DpRtTCPServerConnectionThread.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtTCPServerConnectionThread.java,v 0.14 2007-10-16 13:56:59 cjm Exp $
package ngat.dprt;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import ngat.net.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;

/**
 * This class extends the TCPServerConnectionThread class for the DpRt application.
 * @author Chris Mottram, LJMU
 * @version $Revision: 0.14 $
 */
public class DpRtTCPServerConnectionThread extends TCPServerConnectionThread
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtTCPServerConnectionThread.java,v 0.14 2007-10-16 13:56:59 cjm Exp $");
	/**
	 * Default time taken to respond to a command. This is a class-wide field.
	 */
	private static int defaultAcknowledgeTime = 60*1000;
	/**
	 * Time taken to respond to a command that includes wcs fitting. This is a class-wide field.
	 */
	private static int wcsFitAcknowledgeTime = 90*1000;
	/**
	 * The DpRt object.
	 */
	private DpRt dprt = null;
	/**
	 * Variable used to track whether we should continue processing the command this process is meant to
	 * process. If the CCS has sent an ABORT message this variable is set to true using
	 * <a href="#abortProcessCommand">abortProcessCommand</a>, and then the 
	 * <a href="#processCommand">processCommand</a> routine should tidy up and return to stop this thread.
	 * @see #abortProcessCommand
	 * @see #processCommand
	 */
	private boolean abortProcessCommand = false;

	/**
	 * Constructor of the thread. This just calls the superclass constructors.
	 * @param connectionSocket The socket the thread is to communicate with.
	 */
	public DpRtTCPServerConnectionThread(Socket connectionSocket)
	{
		super(connectionSocket);
	}

	/**
	 * Class method to set the value of <a href="#defaultAcknowledgeTime">defaultAcknowledgeTime</a>. 
	 * @param m The time, in milliseconds.
	 * @see #defaultAcknowledgeTime
	 */
	public static void setDefaultAcknowledgeTime(int m)
	{
		defaultAcknowledgeTime = m;
	}

	/**
	 * Class method to set the value of <a href="#wcsFitAcknowledgeTime">wcsFitAcknowledgeTime</a>. 
	 * @param m The time, in milliseconds.
	 * @see #wcsFitAcknowledgeTime
	 */
	public static void setWcsFitAcknowledgeTime(int m)
	{
		wcsFitAcknowledgeTime = m;
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
	 * Routine called by another thread to stop this
	 * thread implementing a command it has been sent. This variable should cause the processCommand
	 * method to return as soon as possible. The processCommand should still create a COMMAND_DONE
	 * object and fill it in with a suitable abort message. The processCommand should also undo any
	 * operation it has half completed .
	 * The rest of this thread's run method should then execute
	 * to send the DONE message back to the client.
	 * @see #abortProcessCommand
	 */
	public void setAbortProcessCommand()
	{
		abortProcessCommand = true;
	}

	/**
	 * This method is called after the clients command is read over the socket. It allows us to
	 * initialise this threads response to a command. This method changes the threads priority now 
	 * that the command's class is known, if it is a sub-class of INTERRUPT the priority is higher.<br>
	 * @see ngat.message.INST_DP.INTERRUPT
	 * @see Thread#setPriority
	 */
	protected void init()
	{
	// set the threads priority
		if(command instanceof INTERRUPT)
			this.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_INTERRUPT);
		else
			this.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_NORM);
	}

	/**
	 * This method calculates the time it will take for the command to complete and is called
	 * from the classes inherited run method. 
	 * @return An instance of a (sub)class of ngat.message.base.ACK is returned, with the timeToComplete
	 * 	field set to the time the command will take to process.
	 * @see ngat.message.base.ACK
	 * @see ngat.message.base.ACK#setTimeToComplete
	 * @see #defaultAcknowledgeTime
	 * @see #wcsFitAcknowledgeTime
	 */
	protected ACK calculateAcknowledgeTime()
	{
		ACK acknowledge = null;
		int milliseconds;

	// setup return object
		acknowledge = new ACK(command.getId());
	// calculate acknowledge time
		if((command instanceof ABORT))
		{
			milliseconds = defaultAcknowledgeTime;
		}
		else if((command instanceof REBOOT))
		{
			milliseconds = defaultAcknowledgeTime;
		}
		else if((command instanceof STOP))
		{
			milliseconds = defaultAcknowledgeTime;
		}
		else if((command instanceof CALIBRATE_REDUCE))
		{
			milliseconds = defaultAcknowledgeTime;
		}
		else if((command instanceof EXPOSE_REDUCE))
		{
			EXPOSE_REDUCE exposeReduceCommand = (EXPOSE_REDUCE)command;

			if(exposeReduceCommand.getWcsFit())
				milliseconds = wcsFitAcknowledgeTime;
			else
				milliseconds = defaultAcknowledgeTime;
		}
		else if((command instanceof MAKE_MASTER_BIAS))
		{
			milliseconds = defaultAcknowledgeTime;
		}
		else if((command instanceof MAKE_MASTER_FLAT))
		{
			milliseconds = defaultAcknowledgeTime;
		}
		else
		{
			milliseconds = defaultAcknowledgeTime;
		}
	// setup return acknowledgments time to complete.
		acknowledge.setTimeToComplete(milliseconds);
		return acknowledge;
	}

	/**
	 * This method overrides the processCommand method in the ngat.net.TCPServerConnectionThread class.
	 * It is called from the inherited run method. It is responsible for performing the commands
	 * sent to it by the CCS. It should also construct the done object to describe the results of the command.
	 * @see DpRtLibraryInterface#DpRtAbort
	 * @see DpRt#close
	 * @see DpRtLibraryInterface#DpRtCalibrateReduce
	 * @see DpRtLibraryInterface#DpRtExposeReduce
	 * @see DpRtLibraryInterface#DpRtMakeMasterBias
	 * @see DpRtREBOOTQuitThread
	 */
	protected void processCommand()
	{
	// setup a generic done object until the command specific one is constructed.
		done = new COMMAND_DONE(command.getId());

		if(command == null)
		{
			dprt.error("processCommand:command was null.");
			done.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_COMMAND_NULL);
			done.setErrorString("processCommand:command was null.");
			done.setSuccessful(false);
			return;
		}
		dprt.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,"Command:"+command.getClass().getName()+" Started.");
		if(dprt == null)
		{
			dprt.error("processCommand:dprt was null.");
			done.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_DPRT_NULL);
			done.setErrorString("processCommand:dprt was null.");
			done.setSuccessful(false);
			return;
		}
		if(!dprt.getStatus().commandCanBeRun((INST_TO_DP)command))
		{
			DpRtTCPServerConnectionThread thread = null;
			INST_TO_DP currentCommand = null;
			String commandNameString = null;

			thread = dprt.getStatus().getCurrentThread();
			if(thread != null)
				currentCommand = (INST_TO_DP)thread.getCommand();
			if(currentCommand != null)
				commandNameString = currentCommand.getClass().getName();
			else
				commandNameString = new String("Not Known");
			String s = new String("processCommand:command '"+command.getClass().getName()+
				"'could not be run:Command '"+
				commandNameString+"' already running.");
			done.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_ILLEGAL_STATE);
			done.setErrorString(s);
			done.setSuccessful(false);
			dprt.error(s);
			return;
		}
	// This initial test says interupt class commands should not become current command.
	// This class of commands probably want to see what the current command is anyway.
		if(!(command instanceof INTERRUPT))
		{
			dprt.getStatus().setCurrentThread(this);
		}
	// setup return object.
		if(command instanceof ABORT)
		{
			ABORT_DONE abortDone = new ABORT_DONE(command.getId());
			DpRtTCPServerConnectionThread thread = null;

		// tell the thread itself to abort at a suitable point
			thread = (DpRtTCPServerConnectionThread)dprt.getStatus().getCurrentThread();
			if(thread != null)
				thread.setAbortProcessCommand();
			dprt.getDpRtLibrary().DpRtAbort();
			done = abortDone;
			abortDone.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_NO_ERROR);
			abortDone.setErrorString("");
			abortDone.setSuccessful(true);
		}
		if(command instanceof STOP)
		{
			// do nothing 
			STOP_DONE stopDone = new STOP_DONE(command.getId());

			done = stopDone;
			stopDone.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_NO_ERROR);
			stopDone.setErrorString("");
			stopDone.setSuccessful(true);
		}
		if(command instanceof REBOOT)
		{
			REBOOT_DONE rebootDone = new REBOOT_DONE(command.getId());
			DpRtREBOOTQuitThread quitThread = null;

		// Setting up this done object is a bit pointless, we never get chance to
		// return this to the Ccs as we quit.
			done = rebootDone;
			rebootDone.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_NO_ERROR);
			rebootDone.setErrorString("");
			rebootDone.setSuccessful(true);
		// close the server to initiate a DpRt shutdown.
		// Ignore the reboot level parameter...
			dprt.close();
		// don't quit here, DpRt.run terminates and DpRt.main recognizes we have called dprt.close.
			quitThread = new DpRtREBOOTQuitThread("quit:"+command.getId());
			quitThread.setDpRt(dprt);
			quitThread.setWaitThread(this);
			quitThread.start();
		}
		if(command instanceof CALIBRATE_REDUCE)
		{
			CALIBRATE_REDUCE calibrateReduceCommand = (CALIBRATE_REDUCE)command;
			CALIBRATE_REDUCE_DONE calibrateReduceDone = new CALIBRATE_REDUCE_DONE(command.getId());

			done = calibrateReduceDone;
			if(testAbort() == true)
				return;
			dprt.getDpRtLibrary().DpRtCalibrateReduce(calibrateReduceCommand.getFilename(),
				calibrateReduceDone);
			if(calibrateReduceDone.getSuccessful() == false)
			{
				dprt.error(this.getClass().getName()+":run:"+command.getClass().getName()+":"+
					calibrateReduceDone.getErrorNum()+":"+calibrateReduceDone.getErrorString());
			}
		}
		if(command instanceof EXPOSE_REDUCE)
		{
			EXPOSE_REDUCE exposeReduceCommand = (EXPOSE_REDUCE)command;
			EXPOSE_REDUCE_DONE exposeReduceDone = new EXPOSE_REDUCE_DONE(command.getId());

			done = exposeReduceDone;
			if(testAbort() == true)
				return;
			dprt.getDpRtLibrary().DpRtExposeReduce(exposeReduceCommand.getFilename(),
							       exposeReduceCommand.getWcsFit(),exposeReduceDone);
			if(exposeReduceDone.getSuccessful() == false)
			{
				dprt.error(this.getClass().getName()+":run:"+command.getClass().getName()+":"+
					exposeReduceDone.getErrorNum()+":"+exposeReduceDone.getErrorString());
			}
		}
		if(command instanceof MAKE_MASTER_BIAS)
		{
			MAKE_MASTER_BIAS makeMasterBiasCommand = (MAKE_MASTER_BIAS)command;
			MAKE_MASTER_BIAS_DONE makeMasterBiasDone = new MAKE_MASTER_BIAS_DONE(command.getId());

			done = makeMasterBiasDone;
			if(testAbort() == true)
				return;
			dprt.getDpRtLibrary().DpRtMakeMasterBias(makeMasterBiasCommand.getDirname(),
					makeMasterBiasDone);
			if(makeMasterBiasDone.getSuccessful() == false)
			{
				dprt.error(this.getClass().getName()+":run:"+command.getClass().getName()+":"+
					makeMasterBiasDone.getErrorNum()+":"+makeMasterBiasDone.getErrorString());
			}
		}
		if(command instanceof MAKE_MASTER_FLAT)
		{
			MAKE_MASTER_FLAT makeMasterFlatCommand = (MAKE_MASTER_FLAT)command;
			MAKE_MASTER_FLAT_DONE makeMasterFlatDone = new MAKE_MASTER_FLAT_DONE(command.getId());

			done = makeMasterFlatDone;
			if(testAbort() == true)
				return;
			dprt.getDpRtLibrary().DpRtMakeMasterFlat(makeMasterFlatCommand.getDirname(),
					makeMasterFlatDone);
			if(makeMasterFlatDone.getSuccessful() == false)
			{
				dprt.error(this.getClass().getName()+":run:"+command.getClass().getName()+":"+
					makeMasterFlatDone.getErrorNum()+":"+makeMasterFlatDone.getErrorString());
			}
		}
		if(!(command instanceof INTERRUPT))
		{
			dprt.getStatus().setCurrentThread(null);
		}
		dprt.log(DpRtConstants.DPRT_LOG_LEVEL_COMMANDS,"Command:"+command.getClass().getName()+" Completed.");
		dprt.log(DpRtConstants.DPRT_LOG_LEVEL_REPLIES,"Done:"+done.getClass().getName()+
			":successful:"+done.getSuccessful()+
			":error number:"+done.getErrorNum()+":error string:"+done.getErrorString());
	}

	/**
	 * This routine tests the current status of the <a href="#abortProcessCommand">abortProcessCommand</a>
	 * to see whether the operation is to be terminated. This routine should not be called until the
	 * done object has been set. If testAbort returns true, the run routine should return, AFTER
	 * tidying up (e.g. deleting temporary files).
	 * @return The routine returns a boolean to indicate whether the operation has been aborted or not.
	 * @see #abortProcessCommand
	 */
	public boolean testAbort()
	{
		if(abortProcessCommand)
		{
			String s = new String("Command "+command.getClass().getName()+
					" Operation Aborted.");
			dprt.error(s);
			done.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_ABORT);
			done.setErrorString(s);
			done.setSuccessful(false);
		}
		return abortProcessCommand;
	}
}

//
// $Log: not supported by cvs2svn $
// Revision 0.13  2007/08/21 15:23:02  cjm
// Added wcsFit parameter to DpRtExposeReduce DpRtLibrary call.
//
// Revision 0.12  2006/05/16 17:09:39  cjm
// gnuify: Added GNU General Public License.
//
// Revision 0.11  2004/03/31 08:40:01  cjm
// Repackaged into ngat.dprt.
// Also comments changed to new interface.
//
// Revision 0.10  2004/02/12 13:34:21  cjm
// Changed all acknowledge times to use defaultAcknowledge time.
//
// Revision 0.9  2004/01/30 17:01:00  cjm
// Changed to new log method.
// Added Done logging
//
// Revision 0.8  2002/11/26 18:49:10  cjm
// Added handling of MAKE_MASTER_BIAS/MAKE_MASTER_FLAT.
//
// Revision 0.7  2001/03/09 17:44:53  cjm
// Updated REBOOT return code.
//
// Revision 0.6  2000/08/01 14:29:16  cjm
// Added REBOOT code.
//
// Revision 0.5  1999/11/01 14:31:59  cjm
// Updated calculateAcknowledgeTime javadoc comment.
//
// Revision 0.4  1999/11/01 14:25:32  cjm
// Moved setting priority into init method.
// Changed calculateAcknowledgeTime method. Now returns ACK as per ngat.net.TCPServerConnectionThread.
//
// Revision 0.3  1999/06/30 15:06:13  dev
// backup
//
// Revision 0.2  1999/06/24 11:26:22  dev
// "Backup"
//
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//

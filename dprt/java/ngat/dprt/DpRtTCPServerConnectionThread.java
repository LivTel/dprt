// DpRtTCPServerConnectionThread.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtTCPServerConnectionThread.java,v 0.3 1999-06-30 15:06:13 dev Exp $
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import ngat.net.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;

/**
 * This class extends the TCPServerConnectionThread class for the DpRt application.
 * @author Lee Howells
 * @version $Revision: 0.3 $
 */
public class DpRtTCPServerConnectionThread extends TCPServerConnectionThread
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtTCPServerConnectionThread.java,v 0.3 1999-06-30 15:06:13 dev Exp $");
	/**
	 * Default time taken to respond to a command. This is a class-wide field.
	 */
	private static int defaultAcknowledgeTime = 60*1000;
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
	 * @see #defaultAcknowledgeTime
	 */
	public static void setDefaultAcknowledgeTime(int m)
	{
		defaultAcknowledgeTime = m;
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
	 * This method calculates the time it will take for the command to complete and is called
	 * from the classes inherited run method. It also changes the threads priority now that the command's
	 * class is known, if it is a sub-class of INTERRUPT the priority is higher.
	 */
	protected int calculateAcknowledgeTime()
	{
		int time;

	// set the threads priority
		if(command instanceof INTERRUPT)
			this.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_INTERRUPT);
		else
			this.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_NORM);
	// calculate acknowledge time
		if((command instanceof ABORT))
		{
			time = defaultAcknowledgeTime;
		}
		else if((command instanceof REBOOT))
		{
			time = defaultAcknowledgeTime;
		}
		else if((command instanceof STOP))
		{
			time = defaultAcknowledgeTime;
		}
		else if((command instanceof CALIBRATE_REDUCE))
		{
			time = 60*1000;
		}
		else if((command instanceof EXPOSE_REDUCE))
		{
			time = 60*1000;
		}
		else
		{
			time = defaultAcknowledgeTime;
		}
		return time;
	}

	/**
	 * This method overrides the processCommand method in the ngat.net.TCPServerConnectionThread class.
	 * It is called from the inherited run method. It is responsible for performing the commands
	 * sent to it by the CCS. It should also construct the done object to describe the results of the command.
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
			// do nothing 
			REBOOT_DONE rebootDone = new REBOOT_DONE(command.getId());

			done = rebootDone;
			rebootDone.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_NO_ERROR);
			rebootDone.setErrorString("");
			rebootDone.setSuccessful(true);
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
			dprt.getDpRtLibrary().DpRtExposeReduce(exposeReduceCommand.getFilename(),exposeReduceDone);
			if(exposeReduceDone.getSuccessful() == false)
			{
				dprt.error(this.getClass().getName()+":run:"+command.getClass().getName()+":"+
					exposeReduceDone.getErrorNum()+":"+exposeReduceDone.getErrorString());
			}
		}
		if(!(command instanceof INTERRUPT))
		{
			dprt.getStatus().setCurrentThread(null);
		}
		if(dprt.getStatus().getLogLevel() > 0)
			dprt.log("Command:"+command.getClass().getName()+" Completed.");
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
// Revision 0.2  1999/06/24 11:26:22  dev
// "Backup"
//
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//

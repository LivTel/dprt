// DpRtTCPServerConnectionThread.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtTCPServerConnectionThread.java,v 0.1 1999-06-21 15:49:42 dev Exp $
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import ngat.net.*;
import ngat.message.base.*;
import ngat.message.INST_DP.*;
import ngat.phase2.*;

/**
 * This class extends the TCPServerConnectionThread class for the DpRt application.
 * @author Lee Howells
 * @version $Revision: 0.1 $
 */
public class DpRtTCPServerConnectionThread extends TCPServerConnectionThread
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtTCPServerConnectionThread.java,v 0.1 1999-06-21 15:49:42 dev Exp $");
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
		else if((command instanceof REDUCE))
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
	// diddly This should not always be set for abort get status etc.
	// This initial test says interupt class commands should not become current command.
	// This class of commands probably want to see what the current command is anyway.
		if(!(command instanceof INTERRUPT))
		{
			dprt.getStatus().setCurrentThread(this);
		}
	// setup return object.
		if(command instanceof REDUCE)
		{
			// do nothing 
			REDUCE_DONE processDone = new REDUCE_DONE(command.getId());

			done = processDone;
			processDone.setErrorNum(DpRtConstants.DPRT_ERROR_CODE_NO_ERROR);
			processDone.setErrorString("");
			processDone.setSuccessful(true);
		}


		if(!(command instanceof INTERRUPT))
		{
			dprt.getStatus().setCurrentThread(null);
		}
		if(dprt.getStatus().getLogLevel() > 0)
			dprt.log("Command:"+command.getClass().getName()+" Completed.");
	}
}

//
// $Log: not supported by cvs2svn $
//

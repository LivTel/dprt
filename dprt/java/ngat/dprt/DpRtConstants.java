// DpRtConstants.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtConstants.java,v 0.2 1999-06-24 11:26:22 dev Exp $
import java.lang.*;
import java.io.*;

/**
 * This class holds some constant values for the DpRt program. Currently, this consists of error codes.
 * @author Lee Howells
 * @version $Revision: 0.2 $
 */
public class DpRtConstants
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtConstants.java,v 0.2 1999-06-24 11:26:22 dev Exp $");

	/**
	 * Error code. No error.
	 */
	public final static int DPRT_ERROR_CODE_NO_ERROR 	= 0;
	/**
	 * Error code. Command passed to thread was null.
	 */
	public final static int DPRT_ERROR_CODE_COMMAND_NULL 	= 1;
	/**
	 * Error code. Threads dprt pointer was null.
	 */
	public final static int DPRT_ERROR_CODE_DPRT_NULL 	= 2;
	/**
	 * Error code. Command passed to DPRT could not be run in current state.
	 */
	public final static int DPRT_ERROR_CODE_ILLEGAL_STATE 	= 3;
	/**
	 * Error code. The processing of a command was stopped by an ABORT message being sent to the DPRT.
	 */
	public final static int DPRT_ERROR_CODE_ABORT	 	= 4;

	/**
	 * Thread priority level. This is for the server thread. Currently this has the highest priority,
	 * so that new connections are always immediately accepted.
	 */
	public final static int DPRT_THREAD_PRIORITY_SERVER	= Thread.NORM_PRIORITY+2;
	/**
	 * Thread priority level. This is for server connection threads dealing with sub-classes of the INTERRUPT
	 * class. Currently these have a higher ppriority than other server connection threads,
	 * so that INTERRUPT commands are always responded to even when another command is being dealt with.
	 */
	public final static int DPRT_THREAD_PRIORITY_INTERRUPT	= Thread.NORM_PRIORITY+1;
	/**
	 * Thread priority level. This is for most server connection threads. Currently this has a normal priority.
	 */
	public final static int DPRT_THREAD_PRIORITY_NORM	= Thread.NORM_PRIORITY;
}

//
// $Log: not supported by cvs2svn $
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//

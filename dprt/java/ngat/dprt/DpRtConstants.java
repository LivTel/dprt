// DpRtConstants.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtConstants.java,v 0.5 2005-03-31 13:19:37 cjm Exp $
package ngat.dprt;

import java.lang.*;
import java.io.*;

/**
 * This class holds some constant values for the DpRt program. Currently, this consists of error codes.
 * @author Chris Mottram, LJMU
 * @version $Revision: 0.5 $
 */
public class DpRtConstants
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtConstants.java,v 0.5 2005-03-31 13:19:37 cjm Exp $");

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
	 * Logging level. Don't do any logging.
	 */
	public final static int DPRT_LOG_LEVEL_NONE 			= 0;

	/* Logging bits. 
	** Note the DpRt java layer reserves bits 0-7 inclusive.
	** Note libdprt_object reserves bits 16-23 inclusive.
	** Note libdprt_rjs reserves bits 8-15 inclusive (currently unused).
	*/
	/**
	 * Logging level. Log Commands messages received/sent.
	 */
	public final static int DPRT_LOG_LEVEL_COMMANDS 		= (1<<0);
	/**
	 * Logging level. Log Commands message replies received/sent.
	 */
	public final static int DPRT_LOG_LEVEL_REPLIES 			= (1<<1);
	/**
	 * Logging level. Log lock files information.
	 */
	public final static int DPRT_LOG_LEVEL_LOCKFILES       		= (1<<2);
	/**
	 * Logging level. Log if any logging is turned on.
	 * Note libdprt_object reserves bits 16-24.
	 */
	public final static int DPRT_LOG_LEVEL_ALL 			= (DPRT_LOG_LEVEL_COMMANDS|
		DPRT_LOG_LEVEL_REPLIES|DPRT_LOG_LEVEL_LOCKFILES);
	/**
	 * Logging level used by the error logger. We want to log all errors,
	 * hence this value should be used for all errors.
	 */
	public final static int DPRT_LOG_LEVEL_ERROR			= 1;

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
// Revision 0.4  2004/03/31 08:40:01  cjm
// Repackaged into ngat.dprt.
//
// Revision 0.3  2004/01/30 17:01:00  cjm
// Added log levels.
//
// Revision 0.2  1999/06/24 11:26:22  dev
// "Backup"
//
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//

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
// SendDpRtCommand.java
// $Header: /space/home/eng/cjm/cvs/dprt/test/SendDpRtCommand.java,v 1.5 2007-09-03 13:39:56 cjm Exp $

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import ngat.message.INST_DP.*;
import ngat.util.*;

/**
 * This class send a DpRt command to the DpRt. 
 * @author Chris Mottram
 * @version $Revision: 1.5 $
 */
public class SendDpRtCommand
{
	/**
	 * The default port number to send ISS commands to.
	 */
	static final int DEFAULT_DPRT_PORT_NUMBER = 6880;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * This value means no command has been selected.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_NONE = 0;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_CALIBRATE_REDUCE = 1;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_EXPOSE_REDUCE = 2;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_MAKE_MASTER_BIAS = 3;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_MAKE_MASTER_FLAT = 4;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_ABORT = 5;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_REBOOT = 6;
	/**
	 * Type (class) of command that can be sent to the DpRt.
	 * @see #commandType
	 */
	static final int COMMAND_TYPE_STOP = 7;
	/**
	 * The ip address to send the messages to, this should be the machine the DpRt is on.
	 */
	private InetAddress address = null;
	/**
	 * The port number to send commands from the file to the DpRt.
	 */
	private int dprtPortNumber = DEFAULT_DPRT_PORT_NUMBER;
	/**
	 * The stream to write error messages to - defaults to System.err.
	 */
	private PrintStream errorStream = System.err;
	/**
	 * Which command to send to the DpRt.
	 * This is one of COMMAND_TYPE_CALIBRATE_REDUCE, COMMAND_TYPE_EXPOSE_REDUCE, 
	 * COMMAND_TYPE_MAKE_MASTER_BIAS, COMMAND_TYPE_MAKE_MASTER_FLAT, COMMAND_TYPE_ABORT,
	 * COMMAND_TYPE_REBOOT, COMMAND_TYPE_STOP
	 * Defaults to COMMAND_TYPE_NONE, which should cause this program to return an error.
	 * @see #COMMAND_TYPE_CALIBRATE_REDUCE
	 * @see #COMMAND_TYPE_EXPOSE_REDUCE
	 * @see #COMMAND_TYPE_MAKE_MASTER_BIAS
	 * @see #COMMAND_TYPE_MAKE_MASTER_FLAT
	 * @see #COMMAND_TYPE_ABORT
	 * @see #COMMAND_TYPE_REBOOT
	 * @see #COMMAND_TYPE_STOP
	 * @see #COMMAND_TYPE_NONE
	 */
	private int commandType = 0;
	/**
	 * Filename to send to the data pipeline.
	 */
	private String filename = null;
	/**
	 * Directory name to send to the data pipeline.
	 */
	private String dirname = null;
	/**
	 * If we are sending a REBOOT command, this defines the level of reboot.
	 */
	private int rebootLevel = 1;
	/**
	 * If we are sending an EXPOSE_REDUCE, whether to set the wcsFit flag to true.
	 */
	private boolean wcsFit = false;

	/**
	 * This is the initialisation routine.
	 */
	private void init()
	{
	}


	/**
	 * This routine creates a DpRt command. 
	 * @return An instance of a sub-class of DP_TO_INST.
	 * @exception Exception Thrown if commandType does not specify a command to create.
	 * @see #commandType
	 * @see #filename
	 * @see #rebootLevel
	 * @see #wcsFit
	 */
	private INST_TO_DP createCommand() throws Exception
	{
		INST_TO_DP command = null;

		switch(commandType)
		{
			case COMMAND_TYPE_CALIBRATE_REDUCE:
				CALIBRATE_REDUCE calibrateReduce = null;

				calibrateReduce = new CALIBRATE_REDUCE("SendDpRtCommand");
				calibrateReduce.setFilename(filename);
				command = (INST_TO_DP)calibrateReduce;
				break;
			case COMMAND_TYPE_EXPOSE_REDUCE:
				EXPOSE_REDUCE exposeReduce = null;

				exposeReduce = new EXPOSE_REDUCE("SendDpRtCommand");
				exposeReduce.setFilename(filename);
				exposeReduce.setWcsFit(wcsFit);
				command = (INST_TO_DP)exposeReduce;
				break;
			case COMMAND_TYPE_MAKE_MASTER_BIAS:
				MAKE_MASTER_BIAS makeMasterBias = null;

				makeMasterBias = new MAKE_MASTER_BIAS("SendDpRtCommand");
				makeMasterBias.setDirname(dirname);
				command = (INST_TO_DP)makeMasterBias;
				break;
			case COMMAND_TYPE_MAKE_MASTER_FLAT:
				MAKE_MASTER_FLAT makeMasterFlat = null;

				makeMasterFlat = new MAKE_MASTER_FLAT("SendDpRtCommand");
				makeMasterFlat.setDirname(dirname);
				command = (INST_TO_DP)makeMasterFlat;
				break;
			case COMMAND_TYPE_ABORT:
				ABORT abortCommand = null;

				abortCommand = new ABORT("SendDpRtCommand");
				command = (INST_TO_DP)abortCommand;
				break;
			case COMMAND_TYPE_REBOOT:
				REBOOT rebootCommand = null;

				rebootCommand = new REBOOT("SendDpRtCommand");
				rebootCommand.setLevel(rebootLevel);
				command = (INST_TO_DP)rebootCommand;
				break;
			case COMMAND_TYPE_STOP:
				STOP stopCommand = null;

				stopCommand = new STOP("SendDpRtCommand");
				command = (INST_TO_DP)stopCommand;
				break;
			case COMMAND_TYPE_NONE:
			default:
				throw new Exception("Illegal command type:"+commandType);
		}		
		return command;
	}

	/**
	 * This is the run routine. It calls createCommand and sends the resulting command instance to the DpRt 
	 * using a DpRtTestTCPClientConnectionThread, and awaiting the thread termination to signify message
	 * completion.  getThreadResult is then called to print out the result.
	 * @return The routine returns true if the command succeeded, false if it failed.
	 * @exception Exception Thrown if an exception occurs.
	 * @see #createCommand
	 * @see DpRtTestTCPClientConnectionThread
	 * @see #getThreadResult
	 */
	private boolean run() throws Exception
	{
		INST_TO_DP dprtCommand = null;
		DpRtTestTCPClientConnectionThread thread = null;
		boolean retval;

		dprtCommand = (INST_TO_DP)(createCommand());
		thread = new DpRtTestTCPClientConnectionThread(address,dprtPortNumber,dprtCommand);
		thread.start();
		while(thread.isAlive())
		{
			try
			{
				thread.join();
			}
			catch(InterruptedException e)
			{
				System.err.println("run:join interrupted:"+e);
			}
		}// end while isAlive
		retval = getThreadResult(thread);
		return retval;
	}

	/**
	 * Find out the completion status of the thread and print out the final status of some variables.
	 * @param thread The Thread to print some information for.
	 * @return The routine returns true if the thread completed successfully,
	 * 	false if some error occured.
	 */
	private boolean getThreadResult(DpRtTestTCPClientConnectionThread thread)
	{
		boolean retval;

		if(thread.getAcknowledge() == null)
			System.err.println("Acknowledge was null");
		else
			System.err.println("Acknowledge with timeToComplete:"+
				thread.getAcknowledge().getTimeToComplete());
		if(thread.getDone() == null)
		{
			System.out.println("Done was null");
			retval = false;
		}
		else
		{
			if(thread.getDone().getSuccessful())
			{
				System.out.println("Done was successful");
				if(thread.getDone() instanceof REDUCE_DONE)
				{
					System.out.println("\tFilename:"+
						((REDUCE_DONE)(thread.getDone())).getFilename());
				}
				if(thread.getDone() instanceof CALIBRATE_REDUCE_DONE)
				{
					CALIBRATE_REDUCE_DONE calibrateReduceDone = (CALIBRATE_REDUCE_DONE)
						(thread.getDone());

					System.out.println("\tMean Counts:"+calibrateReduceDone.getMeanCounts());
					System.out.println("\tPeak Counts:"+calibrateReduceDone.getPeakCounts());
				}
				if(thread.getDone() instanceof EXPOSE_REDUCE_DONE)
				{
					EXPOSE_REDUCE_DONE exposeReduceDone = (EXPOSE_REDUCE_DONE)(thread.getDone());

					System.out.println("\tCounts:"+exposeReduceDone.getCounts());
					System.out.println("\tSeeing:"+exposeReduceDone.getSeeing());
					System.out.println("\tX Pix:"+exposeReduceDone.getXpix());
					System.out.println("\tY Pix:"+exposeReduceDone.getYpix());
					System.out.println("\tPhotometricity:"+exposeReduceDone.getPhotometricity());
					System.out.println("\tSky Brightness:"+exposeReduceDone.getSkyBrightness());
					System.out.println("\tSaturated:"+exposeReduceDone.getSaturation());
				}
				// No test for MAKE_MASTER_BIAS_DONE and MAKE_MASTER_FLAT_DONE,
				// they return no extra data.
				retval = true;
			}
			else
			{
				System.out.println("Done returned error("+thread.getDone().getErrorNum()+
					"): "+thread.getDone().getErrorString());
				retval = false;
			}
		}
		return retval;
	}

	/**
	 * This routine parses arguments passed into SendDpRtCommand.
	 * @see #commandType
	 * @see #dirname
	 * @see #filename
	 * @see #rebootLevel
	 * @see #dprtPortNumber
	 * @see #address
	 * @see #help
	 * @see #wcsFit
	 * @see #COMMAND_TYPE_CALIBRATE_REDUCE
	 * @see #COMMAND_TYPE_EXPOSE_REDUCE
	 * @see #COMMAND_TYPE_ABORT
	 * @see #COMMAND_TYPE_REBOOT
	 * @see #COMMAND_TYPE_STOP
	 */
	private void parseArgs(String[] args)
	{
		for(int i = 0; i < args.length;i++)
		{
			if(args[i].equals("-a")||args[i].equals("-abort"))
			{
					commandType = COMMAND_TYPE_ABORT;
			}
			else if(args[i].equals("-c")||args[i].equals("-calibrate"))
			{
				if((i+1)< args.length)
				{
					commandType = COMMAND_TYPE_CALIBRATE_REDUCE;
					filename = args[i+1];
					i++;
				}
				else
					errorStream.println("-calibrate requires an argument.");
			}
			else if(args[i].equals("-d")||args[i].equals("-dprtport"))
			{
				if((i+1)< args.length)
				{
					dprtPortNumber = Integer.parseInt(args[i+1]);
					i++;
				}
				else
					errorStream.println("-dprtport requires a port number");
			}
			else if(args[i].equals("-e")||args[i].equals("-expose"))
			{
				if((i+1)< args.length)
				{
					commandType = COMMAND_TYPE_EXPOSE_REDUCE;
					filename = args[i+1];
					i++;
				}
				else
					errorStream.println("-expose requires an argument.");
			}
			else if(args[i].equals("-h")||args[i].equals("-help"))
			{
				help();
				System.exit(0);
			}
			else if(args[i].equals("-ip")||args[i].equals("-address"))
			{
				if((i+1)< args.length)
				{
					try
					{
						address = InetAddress.getByName(args[i+1]);
					}
					catch(UnknownHostException e)
					{
						System.err.println(this.getClass().getName()+":illegal address:"+
							args[i+1]+":"+e);
					}
					i++;
				}
				else
					errorStream.println("-address requires an address");
			}
			else if(args[i].equals("-mb")||args[i].equals("-make_master_bias"))
			{
				if((i+1)< args.length)
				{
					commandType = COMMAND_TYPE_MAKE_MASTER_BIAS;
					dirname = args[i+1];
					i++;
				}
				else
					errorStream.println("-make_master_bias requires an argument.");
			}
			else if(args[i].equals("-mf")||args[i].equals("-make_master_flat"))
			{
				if((i+1)< args.length)
				{
					commandType = COMMAND_TYPE_MAKE_MASTER_FLAT;
					dirname = args[i+1];
					i++;
				}
				else
					errorStream.println("-make_master_flat requires an argument.");
			}
			else if(args[i].equals("-r")||args[i].equals("-reboot"))
			{
				if((i+1)< args.length)
				{
					commandType = COMMAND_TYPE_REBOOT;
					rebootLevel = Integer.parseInt(args[i+1]);
					i++;
				}
				else
					errorStream.println("-reboot requires an argument.");
			}
			else if(args[i].equals("-s")||args[i].equals("-stop"))
			{
					commandType = COMMAND_TYPE_STOP;
			}
			else if(args[i].equals("-w")||args[i].equals("-wcs_fit"))
			{
					wcsFit = true;
			}
			else
				System.out.println(this.getClass().getName()+":Option not supported:"+args[i]);
		}
	}

	/**
	 * Help message routine.
	 */
	private void help()
	{
		System.out.println(this.getClass().getName()+" Help:");
		System.out.println("Options are:");
		System.out.println("\t-a[bort] - Send abort command.");
		System.out.println("\t-c[alibrate] <filename> - Send calibrate reduce command.");
		System.out.println("\t-d[prtport] <port number> - Port to send commands to.");
		System.out.println("\t-e[xpose] <filename> - Send expose reduce command.");
		System.out.println("\t-[ip]|[address] <address> - Address to send commands to.");
		System.out.println("\t-[make_master_bias]|[mb] <directory> - Send make master bias command.");
		System.out.println("\t-[make_master_flat]|[mf] <directory> - Send make master flat command.");
		System.out.println("\t-r[eboot] <level> - Send reboot command.");
		System.out.println("\t-s[top] - Send stop command.");
		System.out.println("\t-w[cs_fit] - (Expose Reduce command only) Set WCS Fit flag.");
		System.out.println("The default DpRt port is "+DEFAULT_DPRT_PORT_NUMBER+".");
	}

	/**
	 * The main routine, called when SendDpRtCommand is executed. This initialises the object, parses
	 * it's arguments, opens the filename, runs the run routine, and then closes the file.
	 * @see #parseArgs
	 * @see #init
	 * @see #run
	 */
	public static void main(String[] args)
	{
		boolean retval;
		SendDpRtCommand command = new SendDpRtCommand();

		command.parseArgs(args);
		command.init();
		if(command.address == null)
		{
			System.err.println("No DpRt Address Specified.");
			command.help();
			System.exit(1);
		}
		try
		{
			retval = command.run();
		}
		catch (Exception e)
		{
			retval = false;
			System.err.println("run failed:"+e);

		}
		if(retval)
			System.exit(0);
		else
			System.exit(2);
	}
}
//
// $Log: not supported by cvs2svn $
// Revision 1.4  2006/05/16 16:55:38  cjm
// gnuify: Added GNU General Public License.
//
// Revision 1.3  2003/06/06 12:59:31  cjm
// backup.
//
// Revision 1.2  2002/05/20 16:38:37  cjm
// Added extra parameters to EXPOSE_REDUCE_DONE prints.
//
// Revision 1.1  2001/08/10 13:06:58  cjm
// Initial revision
//
//

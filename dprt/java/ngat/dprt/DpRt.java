// DpRt.java -*- mode: Fundamental;-*-
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRt.java,v 0.6 2001-05-17 10:18:24 cjm Exp $

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import ngat.net.*;
import ngat.util.*;
import ngat.ccd.*;

/**
 * This class is the start point for the Data Pipeline (Real TIme Module).
 * @author Lee Howells
 * @version $Revision: 0.6 $
 */
public class DpRt
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRt.java,v 0.6 2001-05-17 10:18:24 cjm Exp $");
	/**
	 * The minimum port number.
	 */
	static final int MINIMUM_PORT_NUMBER = 1025;
	/**
	 * The maximum port number.
	 */
	static final int MAXIMUM_PORT_NUMBER = 65535;
	/**
	 * The dprt library Java Interface. The dprt library contains all the C data reduction routines. 
	 */
	private DpRtLibrary libdprt = null;
	/**
	 * The server class that listens for connections.
	 */
	private DpRtTCPServer server = null;
	/**
	 * DpRt status object.
	 */
	private DpRtStatus status = null;
	/**
	 * The port number to listen for connections from clients on.
	 */
	private int serverPortNumber = 0;
	/**
	 * The stream to write error messages to - defaults to System.err.
	 */
	private PrintWriter errorStream = new PrintWriter(System.err,true);
	/**
	 * The stream to write log messages to - defaults to System.out.
	 */
	private PrintWriter logStream = new PrintWriter(System.out,true);
	/**
	 * Do we want to start a thread monitor?
	 */
	private boolean threadMonitor = false;
	/**
	 * The thread monitor window.
	 */
	private ThreadMonitorFrame threadMonitorFrame = null;

	/**
	 * This is the initialisation routine. This creates the <a href="#status">status</a> object. 
	 * The properties for the application are loaded into the status object. 
	 * The error and log files are opened (if this fails, errors default to System.err, logs to System.out).
	 * @see #status
	 * @see #errorStream
	 * @see #logStream
	 * @exception FileNotFoundException Thrown if the property file cannot be found.
	 * @exception IOException Thrown if the property file cannot be accessed and the properties cannot
	 * 	be loaded for some reason.
	 * @exception NumberFormatException Thrown if getting a port number from the configuration file that
	 * 	is not a valid integer.
	 */
	private void init() throws FileNotFoundException,IOException,NumberFormatException
	{
		String filename = null;
		FileOutputStream fos = null;
		int time;

	// create status object and load ccs properties into it
		status = new DpRtStatus();
		try
		{
			status.load();
		}
		catch(FileNotFoundException e)
		{
			error(this.getClass().getName()+":init:loading properties:"+e);
			throw e;
		}
		catch(IOException e)
		{
			error(this.getClass().getName()+":init:loading properties:"+e);
			throw e;
		}
	// change errorStream to files defined in loaded properties
		filename = status.getProperty("dprt.file.error");
		if((filename != null)&&(filename.length()>0))
		{
			try
			{
				fos = new FileOutputStream(filename,true);
			}
			catch(IOException e)
			{
				error(this.getClass().getName()+":init:"+e);
				fos = null;
			}
			if(fos != null)
			{
				// deprecated statement.
				// This is the obly way to set System error stream for runtime (JVM) errors.
				System.setErr(new PrintStream(fos));
				errorStream = new PrintWriter(fos,true);
			}
		}
	// change logStream to files defined in loaded properties
		filename = status.getProperty("dprt.file.log");
		if((filename != null)&&(filename.length()>0))
		{
			try
			{
				fos = new FileOutputStream(filename,true);
			}
			catch(IOException e)
			{
				error(this.getClass().getName()+":init:"+e);
				fos = null;
			}
			if(fos != null)
				logStream = new PrintWriter(fos,true);
		}
	// initialise dprt library
		libdprt = new DpRtLibrary();
	// initialise port numbers from properties file
		try
		{
			serverPortNumber = status.getPropertyInteger("dprt.net.default_DPRT_port_number");
		}
		catch(NumberFormatException e)
		{
			error(this.getClass().getName()+":init:initialsing port number:"+e);
			throw e;
		}
	// initialise default connection response times from properties file
		try
		{
			time = status.getPropertyInteger("dprt.server_connection.default_acknowledge_time");
			DpRtTCPServerConnectionThread.setDefaultAcknowledgeTime(time);
		}
		catch(NumberFormatException e)
		{
			error(this.getClass().getName()+":init:initialsing server connection thread times:"+e);
			// don't throw the error - failing to get this property is not 'vital' to dprt.
		}		
	}

	/**
	 * This is the run routine. It starts a new server to handle incoming requests, and waits for the
	 * server to terminate. A thread monitor is also started if it was requested from the command line.
	 * @see #server
	 * @see #threadMonitor
	 */
	private void run()
	{
		int threadMonitorUpdateTime;

		server = new DpRtTCPServer("DPRT",serverPortNumber);
		server.setDpRt(this);
		server.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_SERVER);
		server.setErrorStream(errorStream);
		if(status.getLogLevel()>0)
		{
			logStream.println(this.getClass().getName()+":run:server started at:"+new Date().toString());
			logStream.println(this.getClass().getName()+":run:server started on port:"+serverPortNumber);
		}
		if(threadMonitor)
		{
			threadMonitorFrame = new ThreadMonitorFrame(this.getClass().getName());
			threadMonitorFrame.pack();
			threadMonitorFrame.setVisible(true);
			try
			{
				threadMonitorUpdateTime = status.getPropertyInteger("dprt.thread_monitor.update_time");
			}
			catch(NumberFormatException e)
			{
				error(this.getClass().getName()+":run:getting thread monitor update time:"+e);
				threadMonitorUpdateTime  = 1000;
			}
			threadMonitorFrame.getThreadMonitor().setUpdateTime(threadMonitorUpdateTime);
		}
		server.start();
		try
		{
			server.join();
		}
		catch(InterruptedException e)
		{
			errorStream.println(this.getClass().getName()+":run:"+e);
		}
	}

	/**
	 * Routine to be called at the end of execution of DpRt to close down communications.
	 * Currently closes DpRtTCPServer.
	 */
	public void close()
	{
		server.close();
	}

	/**
	 * Get Socket Server instance.
	 * @return The server instance.
	 * @see #server
	 */
	public DpRtTCPServer getServer()
	{
		return server;
	}

	/**
	 * Get the libdprt instance.
	 * @return The library instance.
	 * @see #libdprt
	 */
	public DpRtLibrary getDpRtLibrary()
	{
		return libdprt;
	}

	/**
	 * Get status instance.
	 * @return The status instance.
	 */
	public DpRtStatus getStatus()
	{
		return status;
	}

	/**
	 * Get the error stream DPRT is writing errors to.
	 * @return The error stream.
	 */
	public PrintWriter getErrorStream()
	{
		return errorStream;
	}


	/**
	 * Routine to write the string, terminted with a new-line, to the current log-file.
	 * @param s The string to write.
	 * @see #logStream
	 */
	public void log(String s)
	{
		synchronized(logStream)
		{
			logStream.println(s);
		}
	}

	/**
	 * Routine to write the string, terminted with a new-line, to the current error-file.
	 * @param s The string to write.
	 * @see #errorStream
	 */
	public void error(String s)
	{
		synchronized(errorStream)
		{
			errorStream.println(s);
		}
	}

	/**
	 * This routine parses arguments passed into DpRt.
	 * @see #serverPortNumber
	 * @see #threadMonitor
	 * @see #help
	 */
	private void parseArgs(String[] args)
	{
		for(int i = 0; i < args.length;i++)
		{
			if(args[i].equals("-l")||args[i].equals("-log"))
			{
				if((i+1)< args.length)
				{
					status.setLogLevel(Integer.parseInt(args[i+1]));
					i++;
				}
				else
					errorStream.println("-log requires a log level");
			}
			else if(args[i].equals("-s")||args[i].equals("-serverport"))
			{
				if((i+1)< args.length)
				{
					serverPortNumber = Integer.parseInt(args[i+1]);
					i++;
				}
				else
					errorStream.println("-serverport requires a port number");
			}
			else if(args[i].equals("-t")||args[i].equals("-threadmonitor"))
			{
				threadMonitor = true;
			}
			else if(args[i].equals("-h")||args[i].equals("-help"))
			{
				help();
				System.exit(0);
			}
			else
				errorStream.println(this.getClass().getName()+"'"+args[i]+"' not a recognised option");
		}
	}

	/**
	 * Routine that checks whether the arguments loaded from the property files and set using the arguments
	 * are sensible.
	 * @see #serverPortNumber
	 * @exception Exception Thrown when an argument is not acceptable.
	 */
	private void checkArgs() throws Exception
	{
		if((serverPortNumber < MINIMUM_PORT_NUMBER)||(serverPortNumber > MAXIMUM_PORT_NUMBER))
		{
			throw new Exception("Server Port Number '"+serverPortNumber+"' out of range.");
		}
	}

	/**
	 * Help message routine.
	 */
	private void help()
	{
		System.out.println(this.getClass().getName()+" Help:");
		System.out.println("DpRt is the 'Data Pipeline', which process FITS files passed to it.");
		System.out.println("Options are:");
		System.out.println("\t-s[erverport] <port number> - Port to wait for client connections on.");
		System.out.println("\t-l[og] <log level> - log level.");
		System.out.println("\t-t[hreadmonitor] - show thread monitor.");
	}

	/**
	 * The main routine, called when DpRt is executed.
	 */
	public static void main(String[] args)
	{
		DpRt dprt = new DpRt();
		try
		{
			dprt.init();
		}
		catch(Exception e)
		{
			System.exit(1);
		}
		dprt.parseArgs(args);
		try
		{
			dprt.checkArgs();
		}
		catch(Exception e)
		{
			dprt.errorStream.println(e.toString());
			System.exit(1);
		}
		dprt.run();
	// If we reach this stage, then the TCPServer socket thread has terminated.
	// This is either because a REBOOT has been requested in which case TCPServer.quit is true,
	// or because of some error in the TCPServer (socket in use etc.) in which case TCPServer.quit is false.
		if(dprt.server.getQuit() == false)
			System.exit(1);
	}
}

//
// $Log: not supported by cvs2svn $
// Revision 0.5  2001/03/09 16:23:23  cjm
// Changed quit code.
//
// Revision 0.4  2000/08/01 14:28:44  cjm
// Changed exit code depending on how server thread was terminated.
//
// Revision 0.3  1999/10/22 17:06:53  cjm
// Added ngat.ccd to import so DpRt compiles.
//
// Revision 0.2  1999/06/24 11:26:22  dev
// "Backup"
//
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//
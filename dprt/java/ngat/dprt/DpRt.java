// DpRt.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRt.java,v 0.10 2004-03-04 18:54:39 cjm Exp $

import java.lang.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import ngat.net.*;
import ngat.util.*;
import ngat.util.logging.*;

/**
 * This class is the start point for the Data Pipeline (Real Time Module).
 * @author Chris Mottram, LJMU
 * @version $Revision: 0.10 $
 */
public class DpRt
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRt.java,v 0.10 2004-03-04 18:54:39 cjm Exp $");
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
	 * The logging logger.
	 */
	protected Logger logLogger = null;
	/**
	 * The error logger.
	 */
	protected Logger errorLogger = null;
	/**
	 * The filter used to filter messages sent to the logging logger.
	 * @see #logLogger
	 */
	protected BitFieldLogFilter logFilter = null;
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
	 * The error and log files are initialised using <b>initLoggers</b>.
	 * @see #status
	 * @exception FileNotFoundException Thrown if the property file cannot be found.
	 * @exception IOException Thrown if the property file cannot be accessed and the properties cannot
	 * 	be loaded for some reason.
	 * @exception NumberFormatException Thrown if getting a port number from the configuration file that
	 * 	is not a valid integer.
	 * @exception DpRtLibraryNativeException Thrown if the C library initialisation fails.
	 * @see #initLoggers
	 */
	private void init() throws FileNotFoundException,IOException,NumberFormatException,DpRtLibraryNativeException
	{
		int time;

	// create status object and load properties into it
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
	// Logging
		initLoggers();
	// initialise dprt library
		libdprt = new DpRtLibrary();
		libdprt.setStatus(status);
		try
		{
			libdprt.initialise();
		}
		catch(DpRtLibraryNativeException e)
		{
			error(this.getClass().getName()+":init:initialsing C library:"+e);
			throw e;
		}
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
	 * Initialise log handlers. Called from init only, not re-configured on a REDATUM level reboot.
	 * @see #init
	 * @see #initLogHandlers
	 * @see #copyLogHandlers
	 * @see #errorLogger
	 * @see #logLogger
	 * @see #logFilter
	 */
	protected void initLoggers()
	{
	// errorLogger setup
		errorLogger = LogManager.getLogger("error");
		initLogHandlers(errorLogger);
		errorLogger.setLogLevel(Logging.ALL);
	// ngat.net error loggers
		copyLogHandlers(errorLogger,LogManager.getLogger("ngat.net.TCPServer"),null);
		copyLogHandlers(errorLogger,LogManager.getLogger("ngat.net.TCPServerConnectionThread"),null);
		copyLogHandlers(errorLogger,LogManager.getLogger("ngat.net.TCPClientConnectionThreadMA"),null);
	// logLogger setup
		logLogger = LogManager.getLogger("log");
		initLogHandlers(logLogger);
		logLogger.setLogLevel(Logging.ALL);
		logFilter = new BitFieldLogFilter(status.getLogLevel());
		logLogger.setFilter(logFilter);
	// DpRtLibrary logging logger
		copyLogHandlers(logLogger,LogManager.getLogger("DpRtLibrary"),logFilter);
	}

	/**
	 * Method to create and add all the handlers for the specified logger.
	 * These handlers are in the status properties:
	 * "dprt.log."+l.getName()+".handler."+index+".name" retrieves the relevant class name
	 * for each handler.
	 * @param l The logger.
	 * @see #initFileLogHandler
	 * @see #initConsoleLogHandler
	 */
	protected void initLogHandlers(Logger l)
	{
		LogHandler handler = null;
		String handlerName = null;
		int index = 0;

		do
		{
			handlerName = status.getProperty("dprt.log."+l.getName()+".handler."+index+".name");
			if(handlerName != null)
			{
				try
				{
					handler = null;
					if(handlerName.equals("ngat.util.logging.FileLogHandler"))
					{
						handler = initFileLogHandler(l,index);
					}
					else if(handlerName.equals("ngat.util.logging.ConsoleLogHandler"))
					{
						handler = initConsoleLogHandler(l,index);
					}
					else if(handlerName.equals("ngat.util.logging.MulticastLogHandler"))
					{
						handler = initMulticastLogHandler(l,index);
					}
					else if(handlerName.equals("ngat.util.logging.MulticastLogRelay"))
					{
						handler = initMulticastLogRelay(l,index);
					}
					else
					{
						error("initLogHandlers:Unknown handler:"+handlerName);
					}
					if(handler != null)
					{
						handler.setLogLevel(Logging.ALL);
						l.addHandler(handler);
					}
				}
				catch(Exception e)
				{
					error("initLogHandlers:Adding Handler failed:",e);
				}
				index++;
			}
		}
		while(handlerName != null);
	}

	/**
	 * Routine to add a FileLogHandler to the specified logger.
	 * The parameters to the constructor are stored in the status properties:
	 * <ul>
	 * <li><b>param.0</b> is the filename.
	 * <li><b>param.1</b> is the formatter class name.
	 * <li><b>param.2</b> is the record limit in each file.
	 * <li><b>param.3</b> is the start index for file suffixes.
	 * <li><b>param.4</b> is the end index for file suffixes.
	 * <li><b>param.5</b> is a boolean saying whether to append to files.
	 * </ul>
	 * If there are 3 parameters, we create a time period file log handler with parameters:
	 * <ul>
	 * <li><b>param.0</b> is the filename.
	 * <li><b>param.1</b> is the formatter class name.
	 * <li><b>param.2</b> is the time period, either 'HOURLY_ROTATION','DAILY_ROTATION' or 'WEEKLY_ROTATION'.
	 * </ul>
	 * @param l The logger to add the handler to.
	 * @param index The index in the property file of the handler we are adding.
	 * @return A LogHandler of the relevant class is returned, if no exception occurs.
	 * @exception NumberFormatException Thrown if the numeric parameters in the properties
	 * 	file are not valid numbers.
	 * @exception FileNotFoundException Thrown if the specified filename is not valid in some way.
	 * @see #status
	 * @see #initLogFormatter
	 * @see SupIRCamStatus#getProperty
	 * @see SupIRCamStatus#getPropertyInteger
	 * @see SupIRCamStatus#getPropertyBoolean
	 * @see SupIRCamStatus#propertyContainsKey
	 * @see SupIRCamStatus#getPropertyLogHandlerTimePeriod
	 */
	protected LogHandler initFileLogHandler(Logger l,int index) throws NumberFormatException,
		FileNotFoundException
	{
		LogFormatter formatter = null;
		LogHandler handler = null;
		String fileName;
		int recordLimit,fileStart,fileLimit,timePeriod;
		boolean append;

		fileName = status.getProperty("dprt.log."+l.getName()+".handler."+index+".param.0");
		formatter = initLogFormatter("dprt.log."+l.getName()+".handler."+index+".param.1");
		// if we have more then 3 parameters, we are using a recordLimit FileLogHandler
		// rather than a time period log handler.
		if(status.propertyContainsKey("dprt.log."+l.getName()+".handler."+index+".param.3"))
		{
			recordLimit = status.getPropertyInteger("dprt.log."+l.getName()+".handler."+index+".param.2");
			fileStart = status.getPropertyInteger("dprt.log."+l.getName()+".handler."+index+".param.3");
			fileLimit = status.getPropertyInteger("dprt.log."+l.getName()+".handler."+index+".param.4");
			append = status.getPropertyBoolean("dprt.log."+l.getName()+".handler."+index+".param.5");
			handler = new FileLogHandler(fileName,formatter,recordLimit,fileStart,fileLimit,append);
		}
		else
		{
			// This is a time period log handler.
			timePeriod = status.getPropertyLogHandlerTimePeriod("dprt.log."+l.getName()+".handler."+
									    index+".param.2");
			handler = new FileLogHandler(fileName,formatter,timePeriod);
		}
		return handler;
	}

	/**
	 * Routine to add a MulticastLogHandler to the specified logger.
	 * The parameters to the constructor are stored in the status properties:
	 * <ul>
	 * <li>param.0 is the multicast group name i.e. "228.0.0.1".
	 * <li>param.1 is the port number i.e. 5000.
	 * <li>param.2 is the formatter class name.
	 * </ul>
	 * @param l The logger to add the handler to.
	 * @param index The index in the property file of the handler we are adding.
	 * @return A LogHandler of the relevant class is returned, if no exception occurs.
	 * @exception IOException Thrown if the multicast socket cannot be created for some reason.
	 */
	protected LogHandler initMulticastLogHandler(Logger l,int index) throws IOException
	{
		LogFormatter formatter = null;
		LogHandler handler = null;
		String groupName = null;
		int portNumber;

		groupName = status.getProperty("dprt.log."+l.getName()+".handler."+index+".param.0");
		portNumber = status.getPropertyInteger("dprt.log."+l.getName()+".handler."+index+".param.1");
		formatter = initLogFormatter("dprt.log."+l.getName()+".handler."+index+".param.2");
		handler = new MulticastLogHandler(groupName,portNumber,formatter);
		return handler;
	}

	/**
	 * Routine to add a MulticastLogRelay to the specified logger.
	 * The parameters to the constructor are stored in the status properties:
	 * <ul>
	 * <li>param.0 is the multicast group name i.e. "228.0.0.1".
	 * <li>param.1 is the port number i.e. 5000.
	 * </ul>
	 * @param l The logger to add the handler to.
	 * @param index The index in the property file of the handler we are adding.
	 * @return A LogHandler of the relevant class is returned, if no exception occurs.
	 * @exception IOException Thrown if the multicast socket cannot be created for some reason.
	 */
	protected LogHandler initMulticastLogRelay(Logger l,int index) throws IOException
	{
		LogHandler handler = null;
		String groupName = null;
		int portNumber;

		groupName = status.getProperty("dprt.log."+l.getName()+".handler."+index+".param.0");
		portNumber = status.getPropertyInteger("dprt.log."+l.getName()+".handler."+index+".param.1");
		handler = new MulticastLogRelay(groupName,portNumber);
		return handler;
	}

	/**
	 * Routine to add a ConsoleLogHandler to the specified logger.
	 * The parameters to the constructor are stored in the status properties:
	 * <ul>
	 * <li>param.0 is the formatter class name.
	 * </ul>
	 * @param l The logger to add the handler to.
	 * @param index The index in the property file of the handler we are adding.
	 * @return A LogHandler of class FileLogHandler is returned, if no exception occurs.
	 */
	protected LogHandler initConsoleLogHandler(Logger l,int index)
	{
		LogFormatter formatter = null;
		LogHandler handler = null;

		formatter = initLogFormatter("dprt.log."+l.getName()+".handler."+index+".param.0");
		handler = new ConsoleLogHandler(formatter);
		return handler;
	}

	/**
	 * Method to create an instance of a LogFormatter, given a property name
	 * to retrieve it's details from. If the property does not exist, or the class does not exist
	 * or an instance cannot be instansiated we try to return a ngat.util.logging.BogstanLogFormatter.
	 * @param propertyName A property name, present in the status's properties, 
	 * 	which has a value of a valid LogFormatter sub-class name. i.e.
	 * 	<pre>dprt.log.log.handler.0.param.1 =ngat.util.logging.BogstanLogFormatter</pre>
	 * @return An instance of LogFormatter is returned.
	 */
	protected LogFormatter initLogFormatter(String propertyName)
	{
		LogFormatter formatter = null;
		String formatterName = null;
		Class formatterClass = null;

		formatterName = status.getProperty(propertyName);
		if(formatterName == null)
		{
			error("initLogFormatter:NULL formatter for:"+propertyName);
			formatterName = "ngat.util.logging.BogstanLogFormatter";
		}
		try
		{
			formatterClass = Class.forName(formatterName);
		}
		catch(ClassNotFoundException e)
		{
			error("initLogFormatter:Unknown class formatter:"+formatterName+
				" from property "+propertyName);
			formatterClass = BogstanLogFormatter.class;
		}
		try
		{
			formatter = (LogFormatter)formatterClass.newInstance();
		}
		catch(Exception e)
		{
			error("initLogFormatter:Cannot create instance of formatter:"+formatterName+
				" from property "+propertyName);
			formatter = (LogFormatter)new BogstanLogFormatter();
		}
	// set better date format if formatter allows this.
	// Note we really need LogFormatter to generically allow us to do this
		if(formatter instanceof BogstanLogFormatter)
		{
			BogstanLogFormatter blf = (BogstanLogFormatter)formatter;

			blf.setDateFormat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z"));
		}
		if(formatter instanceof SimpleLogFormatter)
		{
			SimpleLogFormatter slf = (SimpleLogFormatter)formatter;

			slf.setDateFormat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z"));
		}
		return formatter;
	}

	/**
	 * Method to copy handlers from one logger to another.
	 * @param inputLogger The logger to copy handlers from.
	 * @param outputLogger The logger to copy handlers to.
	 * @param lf The log filter to apply to the output logger. If this is null, the filter is not set.
	 */
	protected void copyLogHandlers(Logger inputLogger,Logger outputLogger,LogFilter lf)
	{
		LogHandler handlerList[] = null;
		LogHandler handler = null;

		handlerList = inputLogger.getHandlers();
		for(int i = 0; i < handlerList.length; i++)
		{
			handler = handlerList[i];
			outputLogger.addHandler(handler);
		}
		outputLogger.setLogLevel(inputLogger.getLogLevel());
		if(lf != null)
			outputLogger.setFilter(lf);
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
		Date nowDate = null;

		server = new DpRtTCPServer("DPRT",serverPortNumber);
		server.setDpRt(this);
		server.setPriority(DpRtConstants.DPRT_THREAD_PRIORITY_SERVER);
		nowDate = new Date();
		log(DpRtConstants.DPRT_LOG_LEVEL_ALL,this.getClass().getName()+":run:server started at:"+
		    nowDate.toString());
		log(DpRtConstants.DPRT_LOG_LEVEL_ALL,this.getClass().getName()+":run:server started on port:"+
		    serverPortNumber);
		error(this.getClass().getName()+":run:server started at:"+nowDate.toString());
		error(this.getClass().getName()+":run:server started on port:"+serverPortNumber);
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
			error(this.getClass().getName()+":run:"+e);
		}
	}

	/**
	 * Routine to be called at the end of execution of DpRt to close down communications.
	 * Closes DpRtTCPServer, and calls the DprtLibrary instance shutdown method. If the shutdown method
 	 * fails, the error method is called.
	 * @see #error
	 * @see DpRtTCPServer#close
	 * @see DpRtLibrary#shutdown
	 */
	public void close()
	{
		server.close();
		try
		{
			libdprt.shutdown();
		}
		catch(DpRtLibraryNativeException e)
		{
			error(this.getClass().getName()+":close:shutting down C library:"+e);
		}
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
	 * Method to set the level of logging filtered by the log level filter.
	 * @param level An integer, used as a bit-field. Each bit set will allow
	 *      any messages with that level bit set to be logged. e.g. 0 logs no messages,
	 *      127 logs any messages with one of the first 8 bits set.
	 * @see #logFilter
	 */
	public void setLogLevelFilter(int level)
	{
		logFilter.setLevel(level);
	}

	/**
	 * Routine to write the string to the relevant logger. If the relevant logger has not been
	 * created yet the error gets written to System.out.
	 * @param level The level of logging this message belongs to.
	 * @param s The string to write.
	 * @see #logLogger
	 */
	public void log(int level,String s)
	{
		if(logLogger != null)
			logLogger.log(level,s);
		else
		{
			if((status.getLogLevel()&level) > 0)
				System.out.println(s);
		}
	}

	/**
	 * Routine to write the string to the relevant logger. If the relevant logger has not been
	 * created yet the error gets written to System.err.
	 * @param s The string to write.
	 * @see #errorLogger
	 * @see DpRtConstants#DPRT_LOG_LEVEL_ERROR
	 */
	public void error(String s)
	{
		if(errorLogger != null)
			errorLogger.log(DpRtConstants.DPRT_LOG_LEVEL_ERROR,s);
		else
			System.err.println(s);
	}

	/**
	 * Routine to write the string to the relevant logger. If the relevant logger has not been
	 * created yet the error gets written to System.err.
	 * @param s The string to write.
	 * @param e An exception that caused the error to occur.
	 * @see #errorLogger
	 * @see DpRtConstants#DPRT_LOG_LEVEL_ERROR
	 */
	public void error(String s,Exception e)
	{
		if(errorLogger != null)
		{
			errorLogger.log(DpRtConstants.DPRT_LOG_LEVEL_ERROR,s,e);
			errorLogger.dumpStack(DpRtConstants.DPRT_LOG_LEVEL_ERROR,e);
		}
		else
			System.err.println(s+e);
	}

	/**
	 * This routine parses arguments passed into DpRt.
	 * @see #serverPortNumber
	 * @see #threadMonitor
	 * @see #help
	 */
	private void parseArgs(String[] args)
	{
		int logLevel;

		for(int i = 0; i < args.length;i++)
		{
			if(args[i].equals("-l")||args[i].equals("-log"))
			{
				logLevel = Integer.parseInt(args[i+1]);
				if((i+1)< args.length)
				{
					status.setLogLevel(logLevel);
					setLogLevelFilter(logLevel);
					i++;
				}
				else
					System.err.println("-log requires a log level");
			}
			else if(args[i].equals("-s")||args[i].equals("-serverport"))
			{
				if((i+1)< args.length)
				{
					serverPortNumber = Integer.parseInt(args[i+1]);
					i++;
				}
				else
					System.err.println("-serverport requires a port number");
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
				System.err.println(this.getClass().getName()+"'"+args[i]+"' not a recognised option");
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
			dprt.error("main:checkArgs failed",e);
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
// Revision 0.9  2004/01/30 17:01:00  cjm
// Changed to new logging code (ngat.util.logging).
// Deleted old errorStream.
//
// Revision 0.8  2002/11/26 18:49:10  cjm
// Added exception handling for initialise.
// Added shutdown call.
//
// Revision 0.7  2002/05/20 14:14:39  cjm
// Added setStatus call/initialise call.
//
// Revision 0.6  2001/05/17 10:18:24  cjm
// Fixed Exception catching bug when getting threadMonitorUpdateTime from status.
//
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

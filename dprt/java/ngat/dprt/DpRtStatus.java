// DpRtStatus.java
// $Header: /space/home/eng/cjm/cvs/dprt/java/ngat/dprt/DpRtStatus.java,v 0.4 2004-03-31 08:40:01 cjm Exp $
package ngat.dprt;

import java.lang.*;
import java.io.*;
import java.util.*;

import ngat.message.INST_DP.*;
import ngat.util.logging.FileLogHandler;

/**
 * This class holds status information for the DpRt program.
 * @author Chris Mottram
 * @version $Revision: 0.4 $
 */
public class DpRtStatus
{
	/**
	 * Revision Control System id string, showing the version of the Class.
	 */
	public final static String RCSID = new String("$Id: DpRtStatus.java,v 0.4 2004-03-31 08:40:01 cjm Exp $");
	/**
	 * File name containing properties for dprt.
	 */
	private final static String DEFAULT_PROPERTY_FILE_NAME = "./dprt.properties";

	/**
	 * The logging level.
	 */
	private int logLevel = 0;
	/**
	 * The current thread that the DpRt is using to process. This does not get set for
	 * commands that can be sent while others are in operation, such as Abort and get status comamnds.
	 * This can be null when no command is currently being processed.
	 */
	private DpRtTCPServerConnectionThread currentThread = null;
	/**
	 * A list of properties held in the properties file. This contains configuration information in ccs
	 * that needs to be changed irregularily.
	 */
	private Properties properties = null;

	/**
	 * The load method for the class. This loads the property file from disc.
	 * @see #properties
	 * @see #load(java.lang.String)
	 */
	public void load() throws FileNotFoundException,IOException
	{
		load(DEFAULT_PROPERTY_FILE_NAME);
	}

	/**
	 * The load method for the class. This loads the property file from disc.
	 * @see #properties
	 */
	public void load(String filename) throws FileNotFoundException,IOException
	{
		FileInputStream fileInputStream = null;
		
		properties = new Properties();
		fileInputStream = new FileInputStream(filename);
		properties.load(fileInputStream);
		fileInputStream.close();
	}

	/**
	 * Set the logging level for DpRt.
	 * @param level The level of logging.
	 */
	public synchronized void setLogLevel(int level)
	{
		logLevel = level;
	}

	/**
	 * Get the logging level for DpRt.
	 * @return The current log level.
	 */	
	public synchronized int getLogLevel()
	{
		return logLevel;
	}

	/**
	 * Set the thread that is currently executing the current command.
	 * @param thread The thread that is currently executing.
	 */
	public synchronized void setCurrentThread(DpRtTCPServerConnectionThread thread)
	{
		currentThread = thread;
	}

	/**
	 * Get the the thread the DpRt is currently executing to process the 
	 * current command.
	 * @return The thread currently being executed.
	 */
	public synchronized DpRtTCPServerConnectionThread getCurrentThread()
	{
		return currentThread;
	}

	/**
	 * This routine determines whether a command can be run given the current status of the DpRt.
	 * If the DpRt is currently not running a command the command can be run.
	 * If the command is getting status we can generally run this whilst other things are going on.
	 * If the command is a reboot or abort command that tells the DpRt to stop what it is going this is
	 * generally allowed to be run, otherwise we couldn't stop execution of exposures mid-exposure.
	 * @param command The command we want to run.
	 * @return Whether the command can be run given the current status of the system.
	 */
	public synchronized boolean commandCanBeRun(INST_TO_DP command)
	{
		INST_TO_DP currentCommand = null;

		if(currentThread == null)
			return true;
		currentCommand = (INST_TO_DP)currentThread.getCommand();
		if(currentCommand == null)
			return true;
		if(command instanceof INTERRUPT)
			return true;
		return false;
	}

	/**
	 * Method to return whether the loaded properties contain the specified keyword.
	 * Calls the proprties object containsKey method. Note assumes the properties object has been initialised.
	 * @param p The property key we wish to test exists.
	 * @return The method returnd true if the specified key is a key in out list of properties,
	 *         otherwise it returns false.
	 * @see #properties
	 */
	public boolean propertyContainsKey(String p)
	{
		return properties.containsKey(p);
	}

	/**
	 * Routine to get a properties value, given a key. Just calls the properties object getProperty routine.
	 * @param p The property key we want the value for.
	 * @return The properties value, as a string object.
	 * @see #properties
	 */
	public String getProperty(String p)
	{
		return properties.getProperty(p);
	}

	/**
	 * Routine to get a properties value, given a key. The value must be a valid integer, else a 
	 * NumberFormatException is thrown.
	 * @param p The property key we want the value for.
	 * @return The properties value, as an integer.
	 * @exception NumberFormatException If the properties value string is not a valid integer, this
	 * 	exception will be thrown when the Integer.parseInt routine is called.
	 * @see #properties
	 */
	public int getPropertyInteger(String p) throws NumberFormatException
	{
		String valueString = null;
		int returnValue = 0;

		valueString = properties.getProperty(p);
		returnValue = Integer.parseInt(valueString);
		return returnValue;
	}

	/**
	 * Routine to get a properties value, given a key. The value must be a valid double, else a 
	 * NumberFormatException is thrown.
	 * @param p The property key we want the value for.
	 * @return The properties value, as an double.
	 * @exception NumberFormatException If the properties value string is not a valid double, this
	 * 	exception will be thrown when the Double.valueOf routine is called.
	 * @see #properties
	 */
	public double getPropertyDouble(String p) throws NumberFormatException
	{
		String valueString = null;
		Double returnValue = null;

		valueString = properties.getProperty(p);
		returnValue = Double.valueOf(valueString);
		return returnValue.doubleValue();
	}

	/**
	 * Routine to get a properties boolean value, given a key. The properties value should be either 
	 * "true" or "false".
	 * Boolean.valueOf is used to convert the string to a boolean value.
	 * @param p The property key we want the boolean value for.
	 * @return The properties value, as an boolean.
	 * @see #properties
	 */
	public boolean getPropertyBoolean(String p)
	{
		String valueString = null;
		Boolean b = null;

		valueString = properties.getProperty(p);
		b = Boolean.valueOf(valueString);
		return b.booleanValue();
	}

	/**
	 * Routine to get an integer representing a ngat.util.logging.FileLogHandler time period.
	 * The value of the specified property should contain either:'HOURLY_ROTATION', 'DAILY_ROTATION' or
	 * 'WEEKLY_ROTATION'.
	 * @param p The property key we want the time period value for.
	 * @return The properties value, as an FileLogHandler time period (actually an integer).
	 * @exception NullPointerException If the properties value string is null an exception is thrown.
	 * @exception IllegalArgumentException If the properties value string is not a valid time period,
	 *            an exception is thrown.
	 * @see #properties
	 */
	public int getPropertyLogHandlerTimePeriod(String p) throws NullPointerException, IllegalArgumentException
	{
		String valueString = null;
		int timePeriod = 0;
 
		valueString = properties.getProperty(p);
		if(valueString == null)
		{
			throw new NullPointerException(this.getClass().getName()+
						       ":getPropertyLogHandlerTimePeriod:keyword:"+
						       p+":Value was null.");
		}
		if(valueString.equals("HOURLY_ROTATION"))
			timePeriod = FileLogHandler.HOURLY_ROTATION;
		else if(valueString.equals("DAILY_ROTATION"))
			timePeriod = FileLogHandler.DAILY_ROTATION;
		else if(valueString.equals("WEEKLY_ROTATION"))
			timePeriod = FileLogHandler.WEEKLY_ROTATION;
		else
		{
			throw new IllegalArgumentException(this.getClass().getName()+
							   ":getPropertyLogHandlerTimePeriod:keyword:"+
							   p+":Illegal value:"+valueString+".");
		}
		return timePeriod;
	}
}

//
// $Log: not supported by cvs2svn $
// Revision 0.3  2004/03/04 18:53:30  cjm
// Removed Fundamental mode.
// Added propertyContainsKey and getPropertyLogHandlerTimePeriod methods.
//
// Revision 0.2  1999/06/24 11:26:22  dev
// "Backup"
//
// Revision 0.1  1999/06/21 15:49:42  dev
// initial revision
//
//


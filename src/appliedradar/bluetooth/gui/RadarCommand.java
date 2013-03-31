package appliedradar.bluetooth.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;


/**
 * Use to get and set radar parameters in Settings Activity and to parse string values to
 * an ArrayList with type Double values.
 */
public class RadarCommand {
	// for debugging
	private static final String TAG = "RadarCommand";
		
	private ArrayList<Double> arrayValues;
	
	// default radar settings
	public static String CAPTURE_TIME;
	public static String BANDWIDTH;
	public static String RAMP_TIME;
	public static int CURRENT_STATE;

	
	/** Used for testing ... doesn't do anything fun */
	public String readCommand(String returnCommand) {
		return returnCommand;
	}

	/**
	 * The string of collected data recieved by the device from the radar kit
	 * @param toParse	The string of data to parse into an ArrayList of type Double
	 * @return arrayValues	Array of doubles with each element representing one sample of data
	 */
	public ArrayList<Double> parseCommand (String toParse) {
		try {
			List<String> data = Arrays.asList(toParse.split("\\s*,\\s*"));
			convertToDouble(data);
		} catch (RuntimeException e) {
			Log.e(TAG, "error in CSV syntax");
		}
		return arrayValues;
	}
	
	/**
	 * more converting... probably very redundant process - will fix soon :)
	 */
	private void convertToDouble (List<String> stringList) {
		arrayValues = new ArrayList<Double>();
		double value;
		int elements = stringList.size();
		for (int i=0; i<elements; i++) {
			value = Double.parseDouble(stringList.get(i));
			arrayValues.add(value);
		}
	}
	
	
	/** Command to START collecting data - used Main Activity */
	public String startCollect() {
		return "FREQ:SWEEP:RUN$\n";
	}
	
	/** Command to KILL data collection - used Main Activity */
	public String stopCollect() {
		return "FREQ:SWEEP:KILL$\n";
	}
	
	/** Command to get current ramp time setting */
	public String getRampTime() {
		return "FMCW:SWEEP:RAMPTIME?$\n";
	}

	/** Command to set the ramp time to something other than the default */
	public String setRampTime(int input) { 
		return "FMCW:SWEEP:RAMPTIME " + String.valueOf(input) + "$\n";	// user input
	}

	
	/** 
	 * NO LONGER A COMMAND
	 */
	public String getBandwidth() {
		return "FMCW:LFMBW?$\n";
	}
	
	/** 
	 * NO LONGER A COMMAND
	 */
	public String setBandwidth(int input) {
		return "FMCW:LFMBW " + String.valueOf(input) + "$\n";	// user input
	}
	
	/** 
	 * NO LONGER A COMMAND
	 * Command to get current capture time setting
	 */
	public String getCaptureTime() {
		return "FMCW:CAPTURETIME?$\n";
	}

	/** 
	 * NO LONGER A COMMAND
	 * command to set current capture time setting
	 */
	public String setCaptureTime(int input) {
		return "FMCW:CAPTURETIME " + String.valueOf(input) + "$\n";	// user input
	}
	
	/** 
	 * NO LONGER A COMMAND
	 */
	public String readCurrentState(int state) {
		return "SYST:READSTATE? " + state + "$\n";
	}
	
} // end of RadarCommand

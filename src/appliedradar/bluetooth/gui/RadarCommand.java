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
		for (int i = 0; i < elements; i++) {
			value = Double.parseDouble(stringList.get(i));
			arrayValues.add(value);
		}
	}


	/** Command to get current starting freq */
	public String getstartfreq() {
		return "FREQ:SWEEP:START?$";
	}

	/** Command to set current starting freq */
	public String setstartfreq(int input) {
		return "FREQ:SWEEP:START " + String.valueOf(input) + "$";
	}

	/** Command to get current stoping freq */
	public String getstopfreq() {
		return "FREQ:SWEEP:STOP?$";
	}

	/** Command to set current stoping freq */
	public String setstopfreq(int input) {
		return "FREQ:SWEEP:STOP " + String.valueOf(input) + "$";
	}

	/** Command to get current sweep type */
	public String getsweeptype() {
		return "FREQ:SWEEP:TYPE?$";
	}

	/** Command to set current sweep type */
	public String setsweeptype(int input) {
		return "FREQ:SWEEP:TYPE " + String.valueOf(input) + "$";
	}

	/** Command to get current ramp time setting */
	public String getRampTime() {
		return "FREQ:SWEEP:RAMPTIME?$";
	}

	/** Command to set the ramp time to something other than the default */
	public String setRampTime(int input) { 
		return "FREQ:SWEEP:RAMPTIME " + String.valueOf(input) + "$";// user input
	}

	/** Command to set the ramp time to something other than the default */
	public String setRampTime(String input) { 
		return "FREQ:SWEEP:RAMPTIME " + input + "$";// user input
	}
	/** Command to START collecting data - used Main Activity */
	public String startCollect() {
		return "syst:capt 512$";
	}

	/** Command to KILL data collection - used Main Activity */
	public String stopCollect() {
		return "FREQ:SWEEP:KILL$";
	}

	/** Command to get current ref div */
	public String getrefdiv() {
		return "FREQ:REF:DIV?$";
	}

	/** Command to set current ref div */
	public String setrefdiv(int input) {
		return "FREQ:REF:DIV " + String.valueOf(input) + "$";
	}

	/** Command to trigger */
	public String trigger(int input) {
		return "FREQ:SWEEP:TRIGGER$";
	}

} // end of RadarCommand

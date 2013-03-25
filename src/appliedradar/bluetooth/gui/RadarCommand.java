package appliedradar.bluetooth.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadarCommand {

	// default radar settings
	public static String CAPTURE_TIME;
	public static String BANDWIDTH;
	public static String RAMP_TIME;
	public static int CURRENT_STATE;
	
	private ArrayList<Double> arrayValues;
	
	public String readCommand(String returnCommand) {
		return returnCommand;
	}

	/**
	 * Returns the integer values of the String list as an ArrayList<Integer> type
	 * 
	 * @param	 stringList		List of data strings to be converted to integer values
	 * @return	rawDataArray	Array of integer values to be used for processing data
	 */
/*	public ArrayList<Integer> convertToIntegerList(List<String> stringList) {
		ArrayList<Integer> rawDataArray = new ArrayList<Integer>();
		int value;
		int elements = stringList.size();
		for (int i=0; i<elements; i++) {
			value = Integer.parseInt(stringList.get(i));
			rawDataArray.add(value);
		}
		return rawDataArray;
	}*/
	
	
	public ArrayList<Double> parseCommand (String toParse) {
		List<String> data = Arrays.asList(toParse.split("\\s*,\\s*"));
		convertToDouble(data);
		return arrayValues;
	}
	
	private void convertToDouble (List<String> stringList) {
		arrayValues = new ArrayList<Double>();
		double value;
		int elements = stringList.size();
		for (int i=0; i<elements; i++) {
			value = Double.parseDouble(stringList.get(i));
			arrayValues.add(value);
		}
	}
	
	public String startCollect() {
		return "FREQ:SWEEP:RUN$\n";
	}
	
	public String stopCollect() {
		return "FREQ:SWEEP:KILL$\n";
	}
	
	// command to get current ramp time setting
	public String getRampTime() {
		String rampTime = "FMCW:SWEEP:RAMPTIME?$\n";
		return rampTime;
	}

	public String setRampTime(int input) {
		String newRampTime = "FMCW:SWEEP:RAMPTIME " + String.valueOf(input)
				+ "$\n";
		return newRampTime;
	}

	
	// NO LONGER A COMMAND
	// command to get current capture time setting
	public String getCaptureTime() {
		String captureTime = "FMCW:CAPTURETIME?$\n";
		return captureTime;
	}

	// NO LONGER A COMMAND
	public String getBandwidth() {
		String bandwidthSetting = "FMCW:LFMBW?$\n";
		return bandwidthSetting;
	}

	// NO LONGER A COMMAND
	// input from user is saved as a string to send to Radar
	public String setCaptureTime(int input) {
		String newCaptureTime = "FMCW:CAPTURETIME " + String.valueOf(input)
				+ "$\n";
		return newCaptureTime;
	}

	// NO LONGER A COMMAND
	public String setBandwidth(int input) {
		String newBw = "FMCW:LFMBW " + String.valueOf(input) + "$\n"; // user
																		// input
		return newBw;
	}

	// NO LONGER A COMMAND
	public String readCurrentState(int state) {
		String readState = "SYST:READSTATE? " + state + "$\n";
		return readState;
	}
}

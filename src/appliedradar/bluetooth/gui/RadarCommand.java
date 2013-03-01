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

	public String readCommand(String returnCommand) {
		return returnCommand;
	}

	public List<String> parceCommand(String returnCommand) {
		List<String> data = null;
		data = Arrays.asList(returnCommand.split("\\s*,\\s*"));
		return data;
	}
	
	// MAY NOT EVEN WORK.... I DON'T KNOW!
	// HAVEN'T TESTED IT!
	// may consider changing access-modifier to private and call this method from List<String> parceCommand
	/**
	 * Returns the integer values of the String list as an ArrayList<Integer> type
	 * 
	 * @param	 stringList		List of data strings to be converted to integer values
	 * @return	rawDataArray	Array of integer values to be used for processing data
	 */
	public ArrayList<Integer> convertList(List<String> stringList) {
		ArrayList<Integer> rawDataArray = null;
		int value;
		int elements = stringList.size();
		for (int i=0; i<elements; i++) {
			value = Integer.parseInt(stringList.get(i));
			rawDataArray.add(i, value);
		}
		return rawDataArray;
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

	public String resetKit() {
		String debug = "*RST$\n";
		return debug;
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

	/*
	 * public List<String> parceCommand(String returnCommand) { // String
	 * valuesReturned = Integer.parseInt(returnCommand); // int messageLength =
	 * returnCommand.length(); // int endIndex = messageLength - 3; // String
	 * parsedString = returnCommand.substring(0, endIndex);
	 * 
	 * 
	 * List<String> items = null; int semicolon = returnCommand.indexOf(';'); if
	 * (semicolon != -1) { items = Arrays.asList(returnCommand.split(";")); }
	 * items.add(0, "fish tacos"); return items; }
	 */

	/*
	 * public String parceCommand(String returnCommand, boolean queriesState) {
	 * String newCommand; if(queriesState) { List<String> items =
	 * Arrays.asList(returnCommand.split("\\s*,\\s*")); newCommand =
	 * items.get(1) + " " + items.get(2) + " " + items.get(3); return
	 * newCommand; } else { return returnCommand; } }
	 */

}

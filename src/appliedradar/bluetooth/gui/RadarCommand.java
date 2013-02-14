package appliedradar.bluetooth.gui;

import java.util.Arrays;
import java.util.List;


public class RadarCommand {

	//default radar settings
	public static String CAPTURE_TIME;
	public static String BANDWIDTH;
	public static String RAMP_TIME;
	public static int CURRENT_STATE;
	boolean queriesState;
	
	//Constructor
//	public RadarCommand(){}
	
	public String readCurrentState(int state) {
		String readState = "SYST:READSTATE? " + state + "\n";
		queriesState = true;
		return readState;
	}
	
	public String parceCommand(String returnCommand) {
		return returnCommand;
	}
	
/*	public List<String> parceCommand(String returnCommand) {	
	//	String valuesReturned = Integer.parseInt(returnCommand);
//		int messageLength = returnCommand.length();
//		int endIndex = messageLength - 3;
//		String parsedString = returnCommand.substring(0, endIndex);


		List<String> items = null;
		int semicolon = returnCommand.indexOf(';');
		if (semicolon != -1) {
			items = Arrays.asList(returnCommand.split(";"));
		}
		items.add(0, "fish tacos");
		return items;
	}*/
	
	public String parceCommand(String returnCommand, boolean queriesState) {
		String newCommand;
		if(queriesState) {
			List<String> items = Arrays.asList(returnCommand.split("\\s*,\\s*"));	
			newCommand = items.get(1) + " " + items.get(2) + " " + items.get(3);
			return newCommand;
		}
		else {
			return returnCommand;
		}
	}
	
	// command to get current capture time setting
	public String getCurrentCaptureTime() {
//		queriesState = false;
		String captureTime = "*idn?\r\n";
		return captureTime;
	}
	
	public String getCurrentBandwidth() {	
		// the following string was successfully able to send to another device using Bluetooth
		//String bandwidthSetting = "did you get this message?";
		queriesState = false;
		// \n is sent are end of command for the PIC to understand command
		String bandwidthSetting = "FMCW:LFMBW? \n";
		return bandwidthSetting;
	}
	
	// command to get current ramp time setting
	public String getCurrentRampTime() {
		queriesState = false;
		String rampTime = "FMCW:RAMPTIME? \n";
		return rampTime;
	}
	
	
	// input from user is saved as a string to send to Radar
	public String setCaptureTime(int input) {
		queriesState = false;
		String newCaptureTime = "FMCW:CAPTURETIME " + String.valueOf(input) + "\n";
		return newCaptureTime;
	}
	
	public String setBandwidth(int input) {
		queriesState = false;
		// input must be
		String newBw = "FMCW:LFMBW " + String.valueOf(input) + "\n";	//user input
		
		return newBw;
	}
	
	public String setRampTime(int input) {
		queriesState = false;
		String newRampTime =  "FMCW:RAMPTIME " + String.valueOf(input) + "\n";
		return newRampTime;
	}
}

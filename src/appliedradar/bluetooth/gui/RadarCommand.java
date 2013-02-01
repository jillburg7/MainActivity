package appliedradar.bluetooth.gui;

import java.util.Arrays;
import java.util.List;


public class RadarCommand {
	boolean queriesState;
	
	public String readCurrentState(int state) {
		String readState = "SYST:READSTATE? " + state + "\n";
		queriesState = true;
		return readState;
	}
	
/*	public String parceCommand(String returnCommand, boolean readState) {	
	//	int valuesReturned = Integer.parseInt(returnCommand);
		return returnCommand;
	}*/
	
	public String parceCommand(String returnCommand, boolean queriesState) {
		String newCommand;
		if(queriesState) {
			List<String> items = Arrays.asList(returnCommand.split("\\s*,\\s*"));	
			newCommand = items.get(1);
			return newCommand;
		}
		else {
			return returnCommand;
		}
	}
	
	// command to get current capture time setting
	public String getCurrentCaptureTime() {
		queriesState = false;
		String captureTime = "FMCW:CAPTURETIME? \n";
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

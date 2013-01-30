package appliedradar.bluetooth.gui;


public class RadarCommand {

	public String readCurrentState(int state) {
		String readState = "SYST:READSTATE?" + state + "\n";
		return readState;
	}
	
	public String getCurrentBandwidth() {	
		// the following string was successfully able to send to another device using Bluetooth
		//String bandwidthSetting = "did you get this message?";
	
		// \n is sent are end of command for the PIC to understand command
		String bandwidthSetting = "FMCW:LFMBW? \n";
		return bandwidthSetting;
	}
	
	// send command to radar to get current capture time setting
	public String getCurrentCaptureTime() {
		String captureTime = "FMCW:CAPTURETIME? \n";
		return captureTime;
	}
	
	// send command to radar to get current ramp time setting
	public String getCurrentRampTime() {
		String rampTime = "FMCW:RAMPTIME? \n";
		return rampTime;
	}
	
	public String parceCommand(String returnCommand) {
		
		int commaIndex = returnCommand.indexOf(',');
		
		

		
		return returnCommand;
		
	}
}

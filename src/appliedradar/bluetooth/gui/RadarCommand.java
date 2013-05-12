package appliedradar.bluetooth.gui;



/**
 * Use to get and set radar parameters in Settings Activity and to parse string values to
 * an ArrayList with type Double values.
 */
public class RadarCommand {

	/** Command to get current starting freq */
	public String getStartFreq() {
		return "FREQ:SWEEP:START?$";
	}

	/** Command to set current starting freq */
	public String setStartFreq(int input) {
		return "FREQ:SWEEP:START " + String.valueOf(input) + "$";
	}

	/** Command to get current stoping freq */
	public String getStopFreq() {
		return "FREQ:SWEEP:STOP?$";
	}

	/** Command to set current stoping freq */
	public String setStopFreq(int input) {
		return "FREQ:SWEEP:STOP " + String.valueOf(input) + "$";
	}

	/** Command to get current sweep type */
	public String getSweepType() {
		return "FREQ:SWEEP:TYPE?$";
	}

	/** Command to set current sweep type */
	public String setSweepType(int input) {
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
		return "syst:capt 1024$";
	}

	/** Command to KILL data collection - used Main Activity */
	public String stopCollect() {
		return "FREQ:SWEEP:KILL$";
	}

	/** Command to get current ref div */
	public String getRefDiv() {
		return "FREQ:REF:DIV?$";
	}

	/** Command to set current ref div */
	public String setRefDiv(int input) {
		return "FREQ:REF:DIV " + String.valueOf(input) + "$";
	}

	/** Command to trigger */
	public String trigger(int input) {
		return "FREQ:SWEEP:TRIGGER$";
	}

} // end of RadarCommand

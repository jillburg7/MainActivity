package appliedradar.bluetooth.gui;


/**
 * - Search for the maximum value in the FFT array
 * - Find the INDEX* holding the maximum (don't need the actual maximum value)
 * 		INDEX = sampleNumber
 * 
 * sampleNumber -> hertz:
 * 		sampleNumber * ((samplesPerSecond/2)/fftDataLength)
 * 
 * - samplesPerSecond = 44100 
 * 		
 * hertz -> meters:
 *  	(speedOfLight * (hertz - zeroMeterValue))/(2*(speedOfRadar))
 *  
 * - C_LIGHT <-- speedOfLight ~ 2.9979 * 10^8 (meters/second)
 * - speedOfRadar ~ 196000000/0.016 (Hz/second)    --> as of 3/19/13
 * 	- user preference
 * 
 * ramprate -> chirp slope -> df/dt
 * - zeroMeterValue = ?
 *  
 *  meters -> feet:
 *  	meters * (3.28084) = feet
 */
public class RangeCalc {

	/**
	 * Speed of light constant
	 */
	private final double C_LIGHT = 2.99792458e8;
	
	/**
	 * Speed of radar
	 *  --> Likely to change!
	 */
	private final double SPEED_OF_RADAR = 196000000/0.016;
	
	/** 
	 * Holds the array of FFT data in Main Activity
	 */
	private double[] fftData;
	
	/**
	 * The index pointing to the maximum value in FFT data
	 */
	private int indexMaxValue = 0;
	
	// Constructor of class RangeCalc
	public RangeCalc(double[] fftData) {
		this.fftData = fftData;
		searchMax();
		convertToHertz();
	}
	
	/**
	 * Finds the maximum value in the FFT data array.
	 * The index of the array holding the max value is stored in the indexMaxValue variable.
	 */
	private void searchMax() {
		double max = 0;
		for(double d : fftData) {
			if(d > max) 
				max = d;
			for(int x = 0; x < fftData.length; x++) {
				if(fftData[x] == max)
					indexMaxValue = x;
			}
		}
	}
	
	/**
	 * sampleNumber -> hertz:
	 * 		sampleNumber * ((44100/2)/28224)
	 */
	private void convertToHertz() {
		int samples = 44100;
		int someImportantNum = 28224;
		double hertz = indexMaxValue * ((samples/2)/someImportantNum);
		convertToMeters(hertz);
	}
	
	/**
	 * Convert hertz -> meters:
	 * (speedOfLight * (hertz - zeroMeterValue))/(2*(speedOfRadar))
	 * 
	 * - C_LIGHT = speedOfLight
	 * - speedOfRadar ~ 196000000/0.016 (Hz/second)    --> as of 3/19/13
	 * - zeroMeterValue = ?
	 */
	private void convertToMeters(double hertz) {
		double zeroMeterValue = 0;
		double rangeMeters = (C_LIGHT * (hertz - zeroMeterValue))/(2*SPEED_OF_RADAR);
		
	}
	
	/**
	 * meters -> feet:
	 * 		meters * (3.28084) = feet
	 */
	private void convertToFeet() {
		
	}
}

package appliedradar.bluetooth.gui;

/**
 * - Change FFT array parameters X axis from samples to range
 * 
 * samples -> hertz:
 *  //   0 length(fftdata) 
 * 	//	samples * ((samplesPerSecond/2)/fftDataLength)
 * 
 * 		((samplesPerSecond/2)/fftDataLength)
 * 
 * - samplesPerSecond = 44100 
 * 		
 * hertz -> meters:
 *  	(speedOfLight * (hertz - zeroMeterValue))/(2*(df_dt))
 *  
 * - C_LIGHT <-- speedOfLight ~ 2.9979 * 10^8 (meters/second)
 * - df/dt ~ 196000000/0.016 (Hz/second)    --> as of 3/19/13
 * 		- user preference
 * --> NO SUCH THING AS SPEED OF RADAR:
 * 		-> USE INSTEAD: ramprate -> chirp slope -> df/dt
 * 
 * - zeroMeterValue = ?
 *  
 *  meters -> feet:
 *  	meters * (3.28084) = feet
 */
public class RangeCalc {
	
	/**
	 * Ramprate (df/dt)
	 *  --> Default value or user specified!
	 */
	private double df_dt = 196000000/0.016;
	
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
		int samplesPerSecond = 22000;
		int fftDataLength = fftData.length;
		double hertz = ((samplesPerSecond/2)/fftDataLength);
		convertToMeters(hertz);
	}
	
	/**
	 * Convert hertz -> meters:
	 * (speedOfLight * (hertz - zeroMeterValue))/(2*(speedOfRadar))
	 * 
	 * - C_LIGHT = speedOfLight
	 * - speedOfRadar ~ 196000000/0.016 (Hz/second)    --> as of 3/19/13
	 * - zeroMeterValue = 125 Hz
	 */
	private void convertToMeters(double hertz) {
		// Speed of light constant
		final double C_LIGHT = 2.99792458e8;
		double zeroMeterValue = 125;
		
		double rangeMeters = (C_LIGHT * (hertz - zeroMeterValue))/(2*df_dt);
		
	}
	
	/**
	 * meters -> feet:
	 * 		meters * (3.28084) = feet
	 */
	private void convertToFeet(double meters) {
		double feet = meters * 3.28084;
	}
	
	
	public void freqAxis() {
		double[] freqAxis = new double[10];
	}
	
}

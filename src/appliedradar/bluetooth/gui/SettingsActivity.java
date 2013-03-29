package appliedradar.bluetooth.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnSeekBarChangeListener {
	// Debug-use
	private static final String TAG = "SettingsActivity";

	
	public static final String EXTRA_RADAR_COMMAND = "default_capture_time";  

	public String mCaptureTime;
	public String mBandwidth;
	public String mRampTime;

	SeekBar mSeekBar;
	TextView mProgressText;
	TextView mBandwidthText;

	public RadarCommand myCommand = new RadarCommand();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		addPreferencesFromResource(R.xml.preferences);

		setContentView(R.layout.activity_settings);
		

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setOnSeekBarChangeListener(this);
		mProgressText = (TextView)findViewById(R.id.progress);
		
	//	String bw = myCommand.getBandwidth();
	//	String bw = "2.400";
	//	mBandwidthText.setText(bw + " kHz");
		mBandwidthText = (TextView)findViewById(R.id.bandwidth);

//		mCaptureTime = getString(R.string.currentCaptureTime, "some_number_here");
//		((TextView)findViewById(R.id.currentCapture)).setText(mCaptureTime);

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		//        mProgressText.setText(progress + " " + 
		//                getString(R.string.seekbar_from_touch) + "=" + fromTouch);
		mProgressText.setText(progress + " milliseconds");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
			
			finish();
		}
		return super.onOptionsItemSelected(item);
	}



	public void getRampTime(View view) {
//		String currentValues = myCommand.getCurrentCaptureTime();
		Log.i(TAG, "Pressed 'Ramp Time' button");
		String rampTime = "FREQ:SWEEP:RAMPTIME?$\n";
		Intent intent2 = new Intent();
		intent2.putExtra(EXTRA_RADAR_COMMAND, rampTime);
		setResult(Activity.RESULT_OK, intent2);
		finish();
	}
	
	public void getSweepType(View view) {
		Log.i(TAG, "Pressed sweep type button");
		String sweepType = "FREQ:SWEEP:TYPE?$\n";
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, sweepType);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	
	
	// button to read State, test puruposes ONLY
	public void readState(View view) {
		String currentState = myCommand.readCurrentState(0);
		Log.i(TAG, "Pressed read state button");
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, currentState);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
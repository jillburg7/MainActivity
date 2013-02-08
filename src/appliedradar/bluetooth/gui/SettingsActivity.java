package appliedradar.bluetooth.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnSeekBarChangeListener {

	public static final String DEFAULT_BW =  "default_bandwidth";
	public static final String READ_STATE = "read_state #";
	
	SeekBar mSeekBar;
	TextView mProgressText;
	
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
	}
	
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
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	public MainActivity mSendMessage = null;
	public String mCaptureTime;
	public String mBandwidth;
	public String mRampTime;
	
	// button created to send string message to another device using Bluetooth
	// debugging purposes only; will remove once SettingsActivity is set up and functional
//	public void bandwidth(View view) {
//		mBandwidth = getCurrentBandwidth();
//		Intent intent = new Intent();
//		intent.putExtra(DEFAULT_BW, mBandwidth);
//		
//		setResult(Activity.RESULT_OK, intent);
//		finish();
//	}
	
	public RadarCommand myCommand = new RadarCommand();
	
	// button to read State, test puruposes ONLY
	public void readState(View view) {
		String currentState = myCommand.readCurrentState(0);
		Intent intent = new Intent();
		intent.putExtra(READ_STATE, currentState);
		
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	StringBuilder builder = new StringBuilder();
//	builder.append("ValueGoesHere");

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
//	textView.setText(builder.toString());
	
}
/*	// send command to radar to get current bandwidth setting
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
	}*/

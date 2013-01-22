package appliedradar.bluetooth.gui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends Activity {

	public MainActivity mSendMessage = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		addPreferencesFromResource(R.xml.preferences);
		
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
	
	public String mCaptureTime;
	public String mBandwidth;
	public String mRampTime;
	
	public void bandwidth(View view) {
		mBandwidth = getCurrentBandwidth();
		mSendMessage = new MainActivity();
		mSendMessage.sendMessage(mBandwidth);
	}
	
	public String getCurrentBandwidth() {
		String bandwidthSetting = "9";
//		mSendMessage = new MainActivity();
//		mSendMessage.sendMessage("bandwidth");
		
		// send command to radar to get current bandwidth setting
		
		return bandwidthSetting;
	}
	
	public void getCurrentCaptureTime() {
		String captureTime = null;
		
		// send command to radar to get current capture time setting
		
		mCaptureTime = captureTime;
	}
	
	public void getCurrentRampTime() {
		String rampTime = null;
		
		// send command to radar to get current ramp time setting
		
		mRampTime = rampTime;
	}
}

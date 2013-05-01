package appliedradar.bluetooth.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
	DialogFragment jiggs;

	// Dialog display
	private TextView mDialogDisplay;

	public RadarCommand myCommand = new RadarCommand();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		addPreferencesFromResource(R.xml.preferences);

		setContentView(R.layout.activity_settings);

		mDialogDisplay = (TextView) findViewById(R.id.textView);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setOnSeekBarChangeListener(this);
		mProgressText = (TextView)findViewById(R.id.progress);

//		mBandwidthText = (TextView)findViewById(R.id.bandwidth);

		updateDisplay();

	}

	/**
	 * Dialog Fragments for user input
	 */
	public static class MyAlertDialogFragment extends DialogFragment {

	    public static MyAlertDialogFragment newInstance(int title) {
	        MyAlertDialogFragment frag = new MyAlertDialogFragment();
	        Bundle args = new Bundle();
	        args.putInt("title", title);
	        frag.setArguments(args);
	  
	        return frag;
	    }
	    
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	// Set an EditText view to get user input 
	    	final EditText input = new EditText(this.getActivity());
//	    	input.setInputType(numberDecimal);
	        int title = getArguments().getInt("title");
	        
	        return new AlertDialog.Builder(getActivity())
	                .setIcon(R.drawable.ramp)
	                .setTitle(title)
	                .setPositiveButton(R.string.alert_dialog_ok,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            ((SettingsActivity)getActivity()).doPositiveClick(input);
	                        }
	                    }
	                )
	                .setNegativeButton(R.string.alert_dialog_cancel,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            ((SettingsActivity)getActivity()).doNegativeClick();
	                        }
	                    }
	                )
	                .setView(input)
	                .create();
	    }
	}
	
	
	void showDialog() {
	    DialogFragment newFragment = MyAlertDialogFragment.newInstance(
	            R.string.alert_dialog_two_buttons_title);
	    newFragment.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick(EditText input) {
	    // Do stuff here.
	    Log.i("FragmentAlertDialog", "Positive click!");
	    Editable value = input.getText();
	    mRampTime = value.toString();
	    updateDisplay();
	}

	public void doNegativeClick() {
	    // Do other stuff here.
	    Log.i("FragmentAlertDialog", "Negative click!");
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


	private void updateDisplay() {
		mDialogDisplay.setText(new StringBuilder().append(mRampTime));
	}

	public void updateTextView() {
		final TextView defaults = (TextView)findViewById(R.id.textView);
		defaults.setText(String.valueOf(MainActivity.chardefs));
	}
	
	
	/**
	 * Parameters
	 * @param view
	 */
	public void getRampTime(View view) {
		//			String currentValues = myCommand.getCurrentCaptureTime();
		Log.i(TAG, "Pressed 'Ramp Time' button");
		String rampTime = "FREQ:SWEEP:RAMPTIME?$";
		Intent intent2 = new Intent();
		intent2.putExtra(EXTRA_RADAR_COMMAND, rampTime);
		setResult(Activity.RESULT_OK, intent2);
		finish();
	}
	
	public void send(View view) {
//		int input = Integer.parseInt(mRampTime);
//		myCommand.setRampTime(input);
		Intent intent = new Intent();
//		intent2.putExtra(EXTRA_RADAR_COMMAND, myCommand.setRampTime(input));
		intent.putExtra(EXTRA_RADAR_COMMAND, myCommand.setRampTime(mRampTime));
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void getSweepType(View view) {
		Log.i(TAG, "Pressed sweep type button");
		String sweepType = "FREQ:SWEEP:TYPE?$";
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, sweepType);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void getStopFreq(View view) {
		Log.i(TAG, "Pressed sweep type button");
		String stop = "FREQ:SWEEP:STOP?$";
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, stop);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void getStartFreq(View view) {
		Log.i(TAG, "Pressed sweep type button");
		String start = "FREQ:SWEEP:START?$";
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, start);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void getRefDiv(View view) {
		Log.i(TAG, "Pressed sweep type button");
		String ref = "FREQ:REF:DIV?$";
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, ref);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}







	/**
	 * Prompt for user input for radar kit settings
	 * @param view	button clicked
	 */
	public void setRampTime(View view) {
		jiggs = new MyAlertDialogFragment();
		showDialog();
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
package appliedradar.bluetooth.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnSeekBarChangeListener {
	// Debug-use
	private static final String TAG = "SettingsActivity";
	private static final boolean D = true;
	
	public static final String EXTRA_RADAR_COMMAND = "extras";  

//	private final Handler handler = MainActivity.mHandler;
	
	/**
	 * data tags
	 */
	static final String RAMP_TIME = "ramptime";
	static final String START_FREQ = "startFreq";
	static final String STOP_FREQ = "stopFreq";
	static final String SWEEP_TYPE = "sweepType";
	static final String REF_DIV = "refDiv";

//	private String mStrCommand;
//	private int mInputCommand;
	public String mCaptureTime;
	public String mBandwidth;
	public int mRampTime;
	public int mStartFreq;
	public int mStopFreq;
	public int mSweepType;
	public int mRefDiv;

	SeekBar mSeekBar;
	TextView mProgressText;
	TextView mBandwidthText;
	DialogFragment userInput;

	// Dialog display
	private TextView mDialogDisplay;

	private RadarCommand myCommand = new RadarCommand();

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mDialogDisplay = (TextView) findViewById(R.id.value_input);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setOnSeekBarChangeListener(this);
		mProgressText = (TextView)findViewById(R.id.progress);

		
		 // Check whether we're recreating a previously destroyed instance
	    if (savedInstanceState != null) {
	        // Restore value of members from saved state
	    	mRampTime = savedInstanceState.getInt(RAMP_TIME);
	    } else {
	        // Probably initialize members with default values for a new instance
	    	mRampTime = 0;
	    }
		
		updateDisplay();
		updateTextView();
	}

	@Override
	public void onStart() {
		super.onStart();
		if(D) Log.e(TAG, "++ ON START ++");
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current parameters state
	    savedInstanceState.putInt(RAMP_TIME, mRampTime);
//	    savedInstanceState.putInt(START_FREQ, mStartFreq);
	   
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
    @Override
    protected void onResume() {
        super.onResume();
    }
    
	@Override
	public synchronized void onPause() {
		super.onPause();
		if(D) Log.e(TAG, "- ON PAUSE -");
	}
    
	@Override
	public void onStop() {
		super.onStop();
		if(D) Log.e(TAG, "-- ON STOP --");
	}
	
	/** Saves resource states when orientation is changed instead of being reset */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(D) Log.e(TAG, "--- ON DESTROY ---");
	}

	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
	  mRampTime = savedInstanceState.getInt(RAMP_TIME);
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
	
	
	/**
	 * Calls the dialog to display options for user input
	 */
	void showDialog() {
	    DialogFragment newFragment = MyAlertDialogFragment.newInstance(
	            R.string.alert_dialog_two_buttons_title);
	    newFragment.show(getFragmentManager(), "dialog");
	}

	/**
	 * The 'OK' button to accept the (user) input and resume the activity which called 
	 * the dialog to the foreground.
	 * @param input input that was entered by the user in the EditText view
	 */
	public void doPositiveClick(EditText input) {
	    Log.i("FragmentAlertDialog", "OK! User-input accepted"); // Logs 'OK' button click
	    try {
	    	Editable value = input.getText();
	    	mRampTime = Integer.parseInt(value.toString()); // converts input to integer type
	    	updateDisplay();	// updates the TextView to display user input
	    }
	    catch (NumberFormatException e) {
	    	Log.e("AlertDialog-OK", "No input to format text");
	    }
	}

	/**
	 * The 'Cancel' button to reject/ignore the (user) input and resume the activity which
	 * called the dialog to the foreground.
	 */
	public void doNegativeClick() {
	    Log.i("FragmentAlertDialog", "Canceled Dialog Fragment!"); // Logs 'Cancel' button click
	}
	
	/**
	 * To update the textview display with the current parameter value to be set
	 */
	private void updateDisplay() {
		mDialogDisplay.setText(new StringBuilder().append(mRampTime));
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


	/**
	 * Updates the textView with the current parameter settings of the radar kit
	 */
	public void updateTextView() {
		if (MainActivity.currentParameters != null) {
			TextView defaults = (TextView)findViewById(R.id.parameters);
			defaults.setText(new StringBuilder().append(MainActivity.currentParameters[0] +
					" " + MainActivity.currentParameters[1] + " " + MainActivity.currentParameters[2] + 
					" " + MainActivity.currentParameters[3] + " " + MainActivity.currentParameters[4]));	// + "/n"
//			defaults.setText(new StringBuilder().append(MainActivity.currentParameters[1]));
//			defaults.setText(new StringBuilder().append(MainActivity.currentParameters[2]));
//			defaults.setText(new StringBuilder().append(MainActivity.currentParameters[3]));
//			defaults.setText(new StringBuilder().append(MainActivity.currentParameters[4]));
		}
	}
	
	
	/**
	 * sends user input of the respective radar command back to the main activtity,
	 * mesage sent via {@link sendMessage()} using Bluetooth communcation
	 * @param view	 button to send command
	 */
	public void send(View view) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RADAR_COMMAND, myCommand.setRampTime(mRampTime));
		setResult(Activity.RESULT_OK, intent);
		finish();
//		if(mStrCommand != null) {
//			intent.putExtra(EXTRA_RADAR_COMMAND, mStrCommand);
//			setResult(Activity.RESULT_OK, intent);
//			finish();
//		}
	}
	
	/** Query radar kit ramp time */
	public void getRampTime(View view) {
		Log.i(TAG, "Pressed 'Ramp Time' button");	
//		MainActivity.sendMessage(rampTime);
		
		Intent intent2 = new Intent();
//		String rampTime = "FREQ:SWEEP:RAMPTIME?$";
//		intent2.putExtra(EXTRA_RADAR_COMMAND, rampTime);
		
		intent2.putExtra(EXTRA_RADAR_COMMAND, myCommand.getRampTime());
		setResult(Activity.RESULT_OK, intent2);
		finish();
	}

	/** Query radar kit start freq */
	public void getStartFreq(View view) {
		Log.i(TAG, "Pressed sweep type button");
//		String start = "FREQ:SWEEP:START?$";
		Intent intent = new Intent();
//		intent.putExtra(EXTRA_RADAR_COMMAND, start);

		intent.putExtra(EXTRA_RADAR_COMMAND, myCommand.getStartFreq());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	/** Query radar kit stop freq */
	public void getStopFreq(View view) {
		Log.i(TAG, "Pressed sweep type button");
//		String stop = "FREQ:SWEEP:STOP?$";
		Intent intent = new Intent();
//		intent.putExtra(EXTRA_RADAR_COMMAND, stop);
		intent.putExtra(EXTRA_RADAR_COMMAND, myCommand.getStopFreq());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/** Query radar kit sweep type */
	public void getSweepType(View view) {
		Log.i(TAG, "Pressed sweep type button");
//		String sweepType = "FREQ:SWEEP:TYPE?$";
		Intent intent = new Intent();
//		intent.putExtra(EXTRA_RADAR_COMMAND, sweepType);
		intent.putExtra(EXTRA_RADAR_COMMAND, myCommand.getSweepType());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	/** Query radar kit ref. div. */
	public void getRefDiv(View view) {
		Log.i(TAG, "Pressed sweep type button");
//		String ref = "FREQ:REF:DIV?$";
		Intent intent = new Intent();
//		intent.putExtra(EXTRA_RADAR_COMMAND, ref);
		
		intent.putExtra(EXTRA_RADAR_COMMAND, myCommand.getRefDiv());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	
	/** Promopt user to set radar kit ramp time */
	public void setRampTime(View view) {
		userInput = new MyAlertDialogFragment();
		showDialog();
//		mStrCommand = myCommand.setRampTime(mRampTime);
//		mInputCommand = mRampTime;
	}
	
	/** Promopt user to set radar kit start frequency */
	public void setStartFreq(View view) {
		userInput = new MyAlertDialogFragment();
		showDialog();
	}
	
	/** Promopt user to set radar kit stop frequency */
	public void setStopFreq(View view) {
		userInput = new MyAlertDialogFragment();
		showDialog();
	}
	
	/** Promopt user to set radar kit sweep type */
	public void setSweepType(View view) {
		userInput = new MyAlertDialogFragment();
		showDialog();
	}
	
	/** Promopt user to set radar kit ref div */
	public void setRefDiv(View view) {
		userInput = new MyAlertDialogFragment();
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
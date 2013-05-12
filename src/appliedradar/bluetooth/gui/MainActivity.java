package appliedradar.bluetooth.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	// Debugging
	private static final String TAG = "MainActivity";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	public static final int REQUEST_RADAR_INFO = 4;
	public static final int REQUEST_FILE_INFO = 5;


	/** Name of the connected device */
	private String mConnectedDeviceName = null;

	/** String buffer for outgoing messages */
	private StringBuffer mOutStringBuffer;
	/** Local Bluetooth adapter */
	private BluetoothAdapter mBluetoothAdapter = null;
	/** Member object for the chat services */
	private BluetoothChatService mChatService = null;
	//END OF BT INITIALIZERS

	/** Type of data to save */
	private boolean saveFFT = false;

	// aChartEngine Objects for plotting and using Line Graph Renderer/Settings
	GraphicalView mChartView;
	XYMultipleSeriesDataset mDataset;
	XYMultipleSeriesRenderer mRenderer;
	
	/**	Class to set data, rendering, and plot axis settings - uses aChartEngine content */
	private PlotSettings plot = new PlotSettings();

	/** RadarCommand Class object that controls the context of commands sent to the kit */
	private RadarCommand myCommand = new RadarCommand();
	
	/** Array of type short (16-bit signed integer) for incoming radar kit data */
	private short[] raw;
	/** FFT data to use for plotting Range  */
	private double[] fftData;
	


	/** Whether application is receiving data or parameters settings (upon request) */
	private boolean commandInfo = false;
	
	/** Command buffer to parameter settings */
	public String receiveBuffer;
	
	/**
	 * To count the number of parameters received for querying for the five default (or 
	 * current) radar parameter settings.
	 * Should receive exactly 5 parameters, therefore msgCount should only reach a max 
	 * value of 4 at anytime. (Once it reaches 4, currentParameters array is filled and 
	 * contains the 5 default/current parameter settings.
	 */
	private int msgCount = 0;
	
	/**
	 * String array to hold the 5 default/current parameter settings of the radar kit that
	 * this application/device has setup a Bluetooth connection with.
	 * 
	 * Respective data for the indices:
	 *  	[0] Ramptime
	 * 		[1] Start Freq
	 *  	[2] Stop Freq
	 * 		[3] Sweep Type
	 * 		[4] Ref Div
	 */
	public static String[] currentParameters;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the window layout
		setContentView(R.layout.main);
		
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
		}

		// Displays chartview of plot in GUI
		mRenderer = plot.getMyDefaultRenderer();
		mDataset = plot.getMyDefaultData();

		// checks to see if chart is empty
		if (mChartView == null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint(); // use this whenever data has changed and want to redraw
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if(D) Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null) 
				setupChat();
		}
		if (mChartView != null)
			mChartView.repaint();
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
		// checks if chart is empty - if so, add chart to GUI. [Otherwise, update.]
		if (mChartView == null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint(); // use this whenever data has changed and want to redraw
		}
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

	private void setupChat() {
		// Logs that communication between devices is being intialized
		Log.d(TAG, "setupChat()");

//		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null) mChatService.stop();
		if(D) Log.e(TAG, "--- ON DESTROY ---");
	}

	/**
	 * Makes this device discoverable by other Bluetooth devices within range for a 
	 * limited amount of time specified.
	 */
	private void ensureDiscoverable() {
		if(D) Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}


	/**
	 * Sends a message to the device connected via Bluetooth.
	 * @param message  A string of text to send.
	 */
	public void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero
			mOutStringBuffer.setLength(0);
		}
	}


	/** Status of Bluetooth connection is set in ActionBar */
	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	/** Status of Bluetooth connection is set in ActionBar */
	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
//		getDefaultParameters();
	}


	/** The Handler that gets information back from the BluetoothChatService */
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					// updates Bluetooth connection status with the connected device name
					setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);	// updates status in ActionBar
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);// updates status in ActionBar
					break;
				}
				break;
			case MESSAGE_WRITE:
				// Transmit data from this device to the connected Bluetooth device
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// Log Command sent to connect SPP device
				Log.i(TAG, "Tablet:  " + writeMessage);
				break;
			case MESSAGE_READ:
				// Read data received from device connected via Bluetooth
				byte[] readBuf = (byte[]) msg.obj;
				Log.i("MESSAGE_READ", "Received data in MainActivity...");
				handleMsg(readBuf); //
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to "
						+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};




	/**
	 * When another activity returns to the MainActivity and it has important information
	 * to pass to this activity (i.e. result(s) of previous activity).
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(D) Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_RADAR_INFO:
			if (resultCode == Activity.RESULT_OK) {
				// Command to send to radar kit over Bluetooth to change parameter settings
				String radar =  data.getExtras().getString(SettingsActivity.EXTRA_RADAR_COMMAND);
				commandInfo = true;
				sendMessage(radar);	// command to send to set parameters
			}
			break;
		case REQUEST_FILE_INFO:
			if (resultCode == Activity.RESULT_OK) 
				loadFile(data);	// File/data within that file to open (user-requested)
			break;
		}
	}


	// Action Bar displays options when Menu item in Action bar is clicked 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}


	// Action Bar MenuItem onClick events --> what to do next
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivityForResult(settings, REQUEST_RADAR_INFO);
			return true;
		case R.id.connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivty.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	/**
	 * Device to attempt a Bluetooth connection to
	 * @param data Device's information to connect to
	 */
	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(DeviceListActivty.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, false);
	}


	

	/**
	 * Handles the incoming data via the Bluetooth connection (from the
	 * {@link BluetoothChatService} class). If the data is value which correspond to 
	 * parameter settings, then the boolean commandInfo will be true and data will be 
	 * handled accordingly. If commandInfo is false, then the incoming data value correspond
	 * to data that was just collected by the radar, in which this device is connected too.
	 * @param msg
	 */
	private void handleMsg(byte[] msg) {
		int n = 0; 	// size of new byte array corresponding to 2-bytes of data per value 
		// using bit-manipulation :

		raw = new short[msg.length/2];
		for(int i = 0; i < (msg.length - 1); i+=2) {
			raw[n] = (short)((msg[i] << 8) + (msg[i+1] & 0xff));	// bit-manipulation
			n++; 
		}

		int length = raw.length;
		
		if (commandInfo == false) {	// if its is not radar parameter information => data
			int avg = 0;
			for(int i = 0; i < length; i++) 
				avg += raw[i];
			avg = avg/length;
			for(int i = 0; i< length; i++)
				raw[i] -= avg;
			plotData();
		}
		else {					// otherwise it is radar parameter settings (requested)
			if(msgCount == 0)
				currentParameters = new String[5];
			
			receiveBuffer = new String(msg);
			currentParameters[msgCount++] = receiveBuffer; 
			
			for (int i = 0; i < 5; i++)
				Log.i("CurrentParameters", "i = " + i + ",   "+ currentParameters[i]);
			
			if (msgCount == 4) {	// lucky if you get all 5... 
				msgCount = 0;
				commandInfo = false;
			}
		}
	}


	
	/** Adds a delay in between commands sent consecutively after each. */
	public void pause() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Transmits commands to radar kit to get current parameter settings
	 * Should call this method once a Bluetooth connection has been established from this
	 * device. One command is sent at a time and will wait till the application receives
	 * the respective data before another command is sent.
	 */
	public void getDefaultParameters() {
		commandInfo = true;
		sendMessage(myCommand.getRampTime());
		pause();
		sendMessage(myCommand.getStartFreq());
		pause();
		sendMessage(myCommand.getStopFreq());
		pause();
		sendMessage(myCommand.getSweepType());
		pause();
		sendMessage(myCommand.getRefDiv());
	}


	
	
	/**
	 * 'Load Data' onClick event starts a new activity, 'DisplayArchive.java'
	 * @param view the button that was pressed
	 */
	public void openArchive(View view) {
		Intent archiveIntent = new Intent(this, DisplayArchive.class);
		startActivityForResult(archiveIntent, REQUEST_FILE_INFO);
	}

	/**
	 * Information regarding the file selected in Display Archive
	 * @param data file information regarding the selected file
	 */
	public void loadFile(Intent data) {
		String fileInfo =  data.getExtras().getString(DisplayArchive.EXTRA_FILE_INFO);
		loadData(fileInfo);
	}

	/**
	 * Reads data out of text file line-by-line and stores value in an ArrayList
	 * @param fileName Name of the file to open and load data into chart
	 */
	public void loadData(String fileName) {
		ArrayList<Short> shortList = new ArrayList<Short>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;

			while ((line = br.readLine()) != null) {
					short value = Short.parseShort(line);
					shortList.add(value);
//				}
			}
			br.close();
		} 
		catch (IOException e) {
			Log.e("loadData()", "Reading data out of file");		// You'll need to add proper error handling here
		}	
		toShortArray(shortList);
		
		// adds the name of file opened to the TextView
		int index = fileName.indexOf("FMCW File Archive/");
		String name = fileName.substring(index+18);		// removes file path of file open
		TextView fileOpened = (TextView)findViewById(R.id.file_name);
		fileOpened.setText(new StringBuilder().append("File Name: " + name));
		
		plotData();
	}
	
	/**
	 * ArrayList is be copied into an array of type short to use forplotting & processing
	 * @param fileData	ArrayList to copy
	 */
	public void toShortArray(ArrayList<Short> fileData) {
		int size = fileData.size();
		raw = new short[size];
		for(int i = 0; i < size; i++)
			raw[i] = fileData.get(i);
	}


	/**
	 * Plots raw data on chart.
	 * @param view button clicked
	 */
	public void plotButton(View view) {
		try {
			plotData();
			saveFFT = false;
		} catch(NullPointerException e) {
			Log.e("PlotButton", "no data to plot");
		}
	}

	/**
	 * Plots raw data of type double on graph
	 */
	public void plotData() {
		mDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("Raw Data");
//
//		for (int i=0; i<dataToPlot.length; i++)		// hence, double[] fileContent SHOULD be initialized at this point	
//			dataSeries.add(i, dataToPlot[i]);

		for (int i=0; i<raw.length; i++)		// short[] - USE THIS!
			dataSeries.add(i, raw[i]);
		mDataset.addSeries(dataSeries);

		mRenderer = plot.getMyDefaultRenderer();

		if (mChartView != null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.removeAllViews();
			layout.addView(mChartView);
		} else {
			mChartView.repaint();
		}
	}


	/**
	 * Plot FFT Button
	 * @param view	button clicked
	 */
	public void plotFFT(View view) {	
		try {
			fftPlot();
			saveFFT = true;
		} 
		catch(NullPointerException e) {
			Log.e("PlotFFTButton", "no data to plot");
		}	
	}


	/**
	 * Calculates FFT data
	 */
	public void fftPlot() {
		CalcFFT calculate = new CalcFFT();
//		fftData = calculate.fft(dataToPlot);	// to plot data that is an array of type double[]

		double [] rawDoub = new double[raw.length];
		for(int i = 0; i < raw.length; i ++) {
			rawDoub[i] = raw[i];
		}

		fftData = calculate.fft(rawDoub);

		mRenderer = plot.getFFTRenderer();

		if (mChartView != null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, plot.getFrequencyAxis(fftData),
					mRenderer);
			layout.removeAllViews();
			layout.addView(mChartView);
		} else {
			mChartView.repaint();
		}
		//		ActivityManager.getMyMemoryState(null);
	}


	/**
	 *  Save button
	 * @param display	Button view
	 */
	public void saveFile(View display) {
		try {
			saveFile();
		}
		catch(NullPointerException e) {
			Log.e("SaveButton", "no data to save");
		}
	}

	/**
	 * Saves data collected
	 * @param data	Data to save in File Archive
	 */	
	public void saveFile() {	
		String stringToSave = new String();

		try{
			if (saveFFT == false){
				for (int i = 0; i<raw.length; i++) 
					stringToSave += raw[i] + "\n";
			}
			else{	// otherwise, saveFFT = true
				for (int i = 0; i<fftData.length; i++) 
					stringToSave += (float)fftData[i] + "\n";
			}
			
			// Creates a new file
			NewFile save = new NewFile();
			
			save.createFile(this, stringToSave);

			Log.e("SaveFile()", "file saved to " + save.name);
			
			// to notify user that their data was saved successfully in archive
			Toast.makeText(this, "File Saved! File Name: " + save.name, Toast.LENGTH_LONG).show();
		} catch(Exception e) {
			Log.e("SaveFile()", "File Not Saved");
		}
	}


	/**
	 * Signals to start collecting data from Radar kit.
	 * @param butt1		Should only save this data temporarily (up to user if they want to saved perminentally)					
	 */
	public void startCollect(View butt1) {
		commandInfo = false;
		try {
			sendMessage(myCommand.startCollect());
			Log.d(TAG, "start collecting data...");
		}
		catch (Exception e) {
			Log.e("startCollect Button", "nothing to collect :(");
		}
	}

	
	/**
	 * This button is currently implemented to get the five default/current parameters
	 *  
	 * @param butt2 Button clicked
	 */
	public void collectSave(View butt2) {
		commandInfo = false;
		saveFFT = false;
		try {
			sendMessage(myCommand.startCollect());
			Log.d(TAG, "start collecting data...");
		}
		catch (Exception e) {
			Log.e("startCollect Button", "nothing to collect :(");
		}
		// try to save collected data
		try {
			saveFile();
		}
		catch(NullPointerException e) {
			Log.e("SaveButton", "failed to save collected data");
		}
	}	

	/**
	 * Queries the radar kit's current parameter settings via Bluetooth connection
	 * @param button 	when clicked and stores them into the string array called currentParameters.
	 */
	public void getParameters(View button) {
		getDefaultParameters();
	}


} //END OF MAINACTIVITY CODE!
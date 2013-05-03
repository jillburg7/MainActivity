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
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
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

	public static char[] chardefs = null;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	//END OF BT INITIALIZERS


	// RadarCommand Class object that controls the context of commands sent to the Radar Kit
	public RadarCommand myCommand = new RadarCommand();

	// List of data collected from Radar kit
	private ArrayList<Double> dataCollected;

	// Raw data to using for plotting Raw
	private double[] dataToPlot;
	// FFT data to use for plotting Range
	private double[] fftData;

	// Type of data to save
	private boolean saveFFT = false;

	// aChartEngine Objects for plotting and using Line Graph Renderer/Settings
	GraphicalView mChartView;
	XYMultipleSeriesDataset mDataset;
	XYMultipleSeriesRenderer mRenderer;
	PlotSettings plot = new PlotSettings();

	/** Array of type shot for incoming radar kit data */
	private short[] raw;

	// boolean to decide whether we are receiving data or radar parameters (upon request)
	private boolean commandInfo = false;
	/**
	 * 
	 */
	public String receiveBuffer;
	private int msgCount = 0;
	public String[] currentParameters;

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


		if (mChartView == null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint(); // use this whenever data has changed and you
			// want to redraw
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
			if (mChatService == null) setupChat();
		}
		if (mChartView != null) {
			mChartView.repaint();
		}
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
		if (mChartView == null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint(); // use this whenever data has changed and you
			// want to redraw
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
		Log.d(TAG, "setupChat()");

		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

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
	 * Sends a message.
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

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			//mOutEditText.setText(mOutStringBuffer);
		}
	}


	// Status of Bluetooth connection is set in ActionBar
	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	// Status of Bluetooth connection is set in ActionBar
	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
//		getDefaultParameters();
	}


	// The Handler that gets information back from the BluetoothChatService
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				Log.i(TAG, "Tablet:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				Log.i("MESSAGE_READ", "Received stuff main");
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
				String radar =  data.getExtras().getString(SettingsActivity.EXTRA_RADAR_COMMAND);
				Log.d(TAG, "breakpoint");
				sendMessage(radar);				
			}
			break;
		case REQUEST_FILE_INFO:
			if (resultCode == Activity.RESULT_OK) 
				loadFile(data);
			break;
		}
	}


	// Action Bar displays options when Menu item in Action bar is clicked
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem menuItem = menu.findItem(R.id.menu_item_share);

		// Fetch and store ShareActionProvider
		//		mShareActionProvider = (ShareActionProvider) menuItem
		//				.getActionProvider();
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


	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(DeviceListActivty.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, false);
	}


	

	/** new */
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
//			if (currentParameters != null) {}
			
		}
	}

//	currentParameters String array; respective data for the indices:
	// [0] Ramptime
	// [1] Start Freq
	// [2] Stop Freq
	// [3] Sweep Type
	// [4] Ref Div
	
	/**
	 * Adds a 
	 */
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
	 * @param fileName
	 */
	public void loadData(String fileName) {
		ArrayList<Double> contents = new ArrayList<Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;

			while ((line = br.readLine()) != null) {
				int comma = line.indexOf(',');

				// readCommand sends string from radar to be "parsed" (does nothing),
				// just returns string to MainActivity to display in LogCat window.
				if (comma == -1) {
					double value = Double.parseDouble(line);
					contents.add(value);
				}
				else {
					//handleStringMsg(line);
					Log.i(TAG, "handleStringMsg()");
					break;
				}
			}
			br.close();
		} 
		catch (IOException e) {
			Log.e("loadData()", "IOError");		// You'll need to add proper error handling here

		}	
		dataControl(contents);
		plotData();
	}

	/**
	 * ArrayList is be copied into an array of type double to use for processing
	 * @param dataList	ArrayList to copy
	 */
	public void dataControl(ArrayList<Double> dataList) {
		int size = dataList.size();
		dataToPlot = new double[size];
		for(int i = 0; i < size; i++)
			dataToPlot[i] = dataList.get(i);

		int avg = 0;
		for(int i = 0; i <size; i++) 
			avg += dataToPlot[i];
		avg = avg/size;
		for(int i = 0; i<size; i++)
			dataToPlot[i] -= avg;
	}


	/**
	 * Plots raw data on chart.
	 * @param plotMe
	 */
	public void plotButton(View plotMe) {
		try {
			plotData();
		} catch(NullPointerException e) {
			Log.e("PlotButton", "no data to plot");
		}
	}

	/**
	 * Plots raw data of type double on graph
	 */
	public void plotData() {
		mDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("Tablet Data");

		//		for (int i=0; i<dataToPlot.length; i++)		// hence, double[] fileContent SHOULD be initialized at this point	
		//			dataSeries.add(i, dataToPlot[i]);

		for (int i=0; i<raw.length; i++)		// hence, double[] fileContent SHOULD be initialized at this point	
			dataSeries.add(i, raw[i]);
		mDataset.addSeries(dataSeries);

		mRenderer = plot.getMyDefaultRenderer();

		if (mChartView != null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint();
		}
	}


	/**
	 * Plot FFT Button
	 * @param view	button pressed
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
			Log.e("SaveFile()", "saved your file.....this time");
			//			Kind kind = Kind.RAW;
			String kind = "";
			if (!saveFFT){
				for (int i = 0; i<raw.length; i++) 
					stringToSave += raw[i] + "\n";
				kind = "RAW";
			}
			else{
				for (int i = 0; i<fftData.length; i++) 
					stringToSave += fftData[i] + "\n";
				kind = "RANGE";
				saveFFT = false;
			}
			NewFile save = new NewFile();
			save.setKind(kind);
			save.createFile(this, stringToSave);
			Toast.makeText(this, "File Saved!", Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			Log.e("SaveFile()", "iSuck");
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
		//		butt1.setVisibility(0);			// goes invisible onClick and stopCollect button should show up.
	}


	/**
	 * Signals to start collecting data from Radar kit. 
	 * The raw data will be saved automatically.
	 * @param butt2 button pressed
	 */
	public void collectSave(View butt2) {
		getDefaultParameters();
		
		//		sendMessage(myCommand.getRampTime());
//		commandInfo = true;
//		getDefaults = true;
//		defaultCommands();
		// MAKE SURE THIS WORKS!
		//saveFile(); // Saves file perminently
	}	

	public void stopCollect(View butt1_5) {
		sendMessage(myCommand.stopCollect());
		Log.d(TAG, "...stop collecting data");
	}


} //END OF MAINACTIVITY CODE!
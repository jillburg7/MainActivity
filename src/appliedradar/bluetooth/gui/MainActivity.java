package appliedradar.bluetooth.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
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

	// RadarCommand Class object that controls the context of commands sent to the Radar Kit
	public RadarCommand myCommand = new RadarCommand();

	// Name of the connected device
	private String mConnectedDeviceName = null;

	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;

	//DATA ARRAY of 512 data points
	double[] newDataArray = new double[512];
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	//END OF BT INITIALIZERS


	public ShareActionProvider mShareActionProvider;
	// aChartEngine Objects for plotting and using Line Graph Renderer/Settings
	private double[] dataArray;
	private double[] fileContents = null;
	GraphicalView mChartView;
	XYMultipleSeriesDataset mDataset;
	XYMultipleSeriesRenderer mRenderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
		}


		// Displays chartview of plot in GUI
		mRenderer = getMyDefaultRenderer();
		mDataset = getMyDefaultData();
		setChartSettings(mRenderer);

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
	protected void onResume() {
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
			//			mChartView = ChartFactory.getLineChartView(this, getMyData(),
			//					getMyRenderer());
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint(); // use this whenever data has changed and you
			// want to redraw
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		//        mConversationView = (ListView) findViewById(R.id.in);
		//        mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		//        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		//        mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		/*       mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });*/

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
	}



	private List<Integer> arrayInteger;
	private ArrayList<Double> arrayDouble;
	
	
	int n = 0;
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
				//                mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				int comma = readMessage.indexOf(',');
				
				// readCommand sends string from radar to be "parsed" (does nothing),
				// just returns string to MainActivity to display in LogCat window.
				if (comma == -1) {
					String returned = myCommand.readCommand(readMessage);
					Log.i(TAG, returned);			//used to display in log the message after it is parced
				}
				else {
					arrayDouble = myCommand.parseCommand(readMessage);
					for(int i=0; i<arrayDouble.size(); i++) {
						double values = arrayDouble.get(i);
						Log.i(TAG, i + " = " + values);
					}
					plotData();
					//saveFile(arrayDouble);
				}
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
			if (resultCode == Activity.RESULT_OK) {
				loadFile(data);
			}
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
		mShareActionProvider = (ShareActionProvider) menuItem
				.getActionProvider();
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
		mChatService.connect(device);
	}


	public void loadFile(Intent data) {
		String fileInfo =  data.getExtras().getString(DisplayArchive.EXTRA_FILE_INFO);
		loadData(fileInfo);
	}
	
	
	public void loadData(String fileName) {
		ArrayList<Double> contents = new ArrayList<Double>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			double value;
			
			while ((line = br.readLine()) != null) {	//2048
				value = Double.parseDouble(line);
				contents.add(value);
			}
			br.close();
			
			int size = contents.size();
			fileContents = new double[size];
			for (int i = 0; i<size; i++)
				fileContents[i] = contents.get(i);
		} 
		// You'll need to add proper error handling here
		catch (IOException e) {
			Log.e("loadData()", "IOError");
		}
		plotData();
	}
	
	
	public void plotData() {
		if (fileContents == null) {					// NULL b/c nothing has been choosen to load from file Archive!
			int size = arrayDouble.size();
			fileContents = new double[size];
			for (int i = 0; i<size; i++)
				fileContents[i] = arrayDouble.get(i);
		}											// No else {} statement b/c if fileContents has been initialized already
		
		
			mDataset = new XYMultipleSeriesDataset();
			XYSeries dataSeries = new XYSeries("Tablet Data");
	
			for (int i=0; i<fileContents.length; i++)		// hence, double[] fileContent SHOULD be initialized at this point	
				dataSeries.add(i, fileContents[i]);
			
			fileContents = null;		// set to null again so that each new block of data sent is plotted
			
			mDataset.addSeries(dataSeries);
			mRenderer = new XYMultipleSeriesRenderer();
			mRenderer = getMyDefaultRenderer();
			
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
	 * 'Load Data' onClick event starts a new activity, 'DisplayArchive.java'
	 * 
	 * @param loadActivity
	 */
	public void openArchive(View loadActivity) {
		Toast.makeText(this, "Selected Load Data", Toast.LENGTH_SHORT).show();
		Intent archiveIntent = new Intent(this, DisplayArchive.class);
		startActivityForResult(archiveIntent, REQUEST_FILE_INFO);
	}

	/**
	 * 
	 * @param data	Data to save in File Archive
	 */	
	public void saveFile(ArrayList<Double> data) {	
		String stringToSave = "";
		for (int i = 0; i<arrayDouble.size(); i++) 
			stringToSave += arrayDouble.get(i) + "\n";
		try{
			NewFile save = new NewFile();
			save.createFile(this, stringToSave);
		}
		catch(IOException e){
			Log.e("MainActivity", "IOError");
		}
	}
	
	

	public void startCollect(View butt1) {
		String start = "FREQ:SWEEP:RUN$\n";
		sendMessage(start);
		Log.d(TAG, "start collecting data...");
	}


	public void stopCollect(View butt2) {
		String stop = "FREQ:SWEEP:KILL$\n";
		sendMessage(stop);
		Log.d(TAG, "...stop collecting data");
	}


	/**
	 *  Save button
	 * @param display	Button view
	 */
	public void saveFile(View display) {	

		String stringToSave = "";
		for (int i = 0; i<arrayDouble.size(); i++) 
			stringToSave += arrayDouble.get(i) + "\n";
		try{
			NewFile save = new NewFile();
			save.createFile(this, stringToSave);
		}
		catch(IOException e){
			Log.e("MainActivity", "IOError");
		}
		
//		try{
//			NewFile save = new NewFile();
//			String fileContent = getDataToSave();
//			if (fileContent == null) {
//				Log.e("MainActivity", "No data to save.");
//			} else {		
//				save.createFile(this, fileContent);
//			}
//		}
//		catch(IOException e){
//			Log.e("MainActivity", "IOError");
//		}
	}


	/**
	 * Reads data line-by-line out of a '.txt' file which is saved to internal storage
	 * 
	 * @return dataArray	floating-pointing samples from Radar Kit, stored into an array
	 */
	public double[] getDataFromFile() {
		File internalMemory = Environment.getExternalStorageDirectory();

		// Get the text file
		// NEED TO SPECIFICALLY CHANGE THIS LINE OF CODE TO BE MORE UNIVERSAL!
		File file = new File(internalMemory, "data3kHz_44KHzFs.txt");
			
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int i = 0;
			dataArray = new double[512];	//2048

			while ((line = br.readLine()) != null & (i != 512)) {	//2048
				dataArray[i] = Float.parseFloat(line);
				i++;
			}
			br.close();
		} 
		// You'll need to add proper error handling here
		catch (IOException e) {
			Log.e("MainActivity", "IOError");
		}
		return dataArray;	
	}


	/**
	 * can save any FFT data into a file even if its hasn't been plotted yet
	 * 
	 * @return
	 */
	public String getDataToSave() {
		dataArray = getDataFromFile();
		double[] arrayToSave = getFftData();
		String stringToSave = null;

		if (mDataset != null) {	
			for (int i = 0; i<arrayToSave.length; i++) {
				if (stringToSave == null)
					stringToSave = arrayToSave[i] + "\n";
				else
					stringToSave += arrayToSave[i] + "\n";
			}
			return stringToSave;
		} else {
			// NEED TO HANDLE ERRORS!
			return stringToSave;
		}	
	}

	/**
	 * Plots raw data in MainActivity chart.
	 * @param plotMe	Button view
	 */
	public void plotButton(View plotMe) {
		Toast.makeText(this, "Selected Plot", Toast.LENGTH_SHORT).show();

		mDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("Simulated Data: Fs = 44KHz");

		double[] array = getDataFromFile();

		for (int i=0; i<array.length; i++){				
			dataSeries.add(i, array[i]);
		}
		mDataset.addSeries(dataSeries);

		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer = getRawRenderer();

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
	 *  Plots FFT- power spectrum data onClick
	 * @param fftPlot
	 */
	public void plotFFT(View fftPlot) {
		Toast.makeText(this, "Selected Plot FFT", Toast.LENGTH_SHORT).show();

		mDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("Simulated Data: Fs = 44KHz");	

		double[] array = getDataFromFile();

		for (int i=0; i<array.length; i++){				
			dataSeries.add(i, array[i]);
		}

		mDataset.addSeries(dataSeries);
		mDataset.removeSeries(0);


		XYSeries dataSeries2 = new XYSeries("FFT of Simulated Data");
		double[] array2= getFftData();
		int j=0;
		for (j=0; j<(array2.length); j++){
			dataSeries2.add(j, array2[j]);
		}
		mDataset.addSeries(dataSeries2);

		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer = getFFTRenderer();
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
	 * Default dataset for initial app start-up
	 * @return myDataSet
	 */



	// Default "data" to display when no data has been selected to plot & process
	public XYMultipleSeriesDataset getMyDefaultData() {
		XYMultipleSeriesDataset myDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries(" ");
		myDataset.addSeries(dataSeries);
		return myDataset;
	}

	/**
	 *  FFT calucation
	 * @return fftOutput2	Power Spectrum output data
	 */
	public double[] getFftData() {

		double[] realArray = dataArray;
		double[] imagArray = new double[realArray.length];

		FFTcalc fftData = new FFTcalc();
		double[] fftArray = fftData.fft(realArray,imagArray, true);
		int n = realArray.length;
		double[] imagFFT = new double[fftArray.length/2];
		double[] realFFT = new double[fftArray.length/2];
		double radice = 1 / Math.sqrt(n);	

		// real and imaginary parts are separated from output of FFT algorithm
		for(int i=0; i< fftArray.length; i+=2) {
			int i2 = i/2;
			realFFT[i2] = fftArray[i] / radice;
			imagFFT[i2] = fftArray[i + 1] / radice;
		}

		// Magnitude of real & imaginary arrays is calculated and put into one array
		double[] fftOutput = new double[n];
		for (int i=0; i<n; i++){
			fftOutput[i] = Math.sqrt(Math.pow(realFFT[i], 2) + Math.pow(imagFFT[i], 2)); 
		}


		double[] fftOutput2 = new double[n];
		// Power of m
		for (int i=0; i<(n); i++){
			fftOutput2[i] = 20*Math.log10(fftOutput[i]);
		}

		return fftOutput2;	
	}

	/**
	 *  Default Renderer to display when no data has been selected to process (blank graph)
	 */
	public XYMultipleSeriesRenderer getMyDefaultRenderer() {

		XYSeriesRenderer r1 = new XYSeriesRenderer();
		r1.setColor(Color.BLUE);
		r1.setLineWidth(2);
		r1.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
		r1.setFillPoints(true); // not for point or x don't know how to set point size or point color

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r1);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, true);
		myRenderer.setZoomButtonsVisible(true);

		myRenderer.setChartTitle("FMCW Radar Data Plot");
		myRenderer.setChartTitleTextSize(30);

		myRenderer.setLegendTextSize(20);

		myRenderer.setZoomRate(10);

		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.getXLabelsAlign();
		myRenderer.setXLabelsColor(Color.BLACK);
		myRenderer.setYLabelsColor(0, Color.BLACK);
		myRenderer.setShowAxes(true);
		myRenderer.setLabelsColor(Color.BLACK);

		myRenderer.setXTitle("Samples");
		myRenderer.setYTitle("Amplitude");
		myRenderer.setAxisTitleTextSize(20);

		myRenderer.setApplyBackgroundColor(true);
		myRenderer.setBackgroundColor(Color.LTGRAY); 

		myRenderer.setMarginsColor(Color.WHITE); 

		myRenderer.setGridColor(Color.DKGRAY);
		myRenderer.setXLabels(20);
		myRenderer.setYLabels(9);
		myRenderer.setShowGrid(true);
		myRenderer.setMargins(new int[] {35, 50, 15, 30});
		return myRenderer;
	}

	public XYMultipleSeriesRenderer getRawRenderer() {

		XYSeriesRenderer r1 = new XYSeriesRenderer();
		r1.setColor(Color.BLUE);
		r1.setLineWidth(2);
		r1.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
		r1.setFillPoints(true); // not for point or x don't know how to set point size or point color

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r1);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, true);
		myRenderer.setZoomButtonsVisible(true);

		myRenderer.setChartTitle("FMCW Radar Data Plot");
		myRenderer.setChartTitleTextSize(30);

		myRenderer.setLegendTextSize(20);

		myRenderer.setZoomRate(10);

		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.getXLabelsAlign();
		myRenderer.setXLabelsColor(Color.BLACK);
		myRenderer.setYLabelsColor(0, Color.BLACK);
		myRenderer.setShowAxes(true);
		myRenderer.setLabelsColor(Color.BLACK);

		myRenderer.setXTitle("Samples");
		myRenderer.setYTitle("Amplitude");
		myRenderer.setAxisTitleTextSize(20);

		// background color of the PLOT ONLY
		myRenderer.setApplyBackgroundColor(true);
		// Color.TRANSPARENT would show the background of the app (MainActivity)
		myRenderer.setBackgroundColor(Color.LTGRAY); 

		// sets the background area of the object itself
		// does not change the plots background
		myRenderer.setMarginsColor(Color.WHITE); 

		myRenderer.setGridColor(Color.DKGRAY);
		myRenderer.setXLabels(20);
		myRenderer.setYLabels(9);
		myRenderer.setShowGrid(true);
		myRenderer.setMargins(new int[] {35, 50, 15, 30});

		// Minimum & Max values to view plot area
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(444);
		myRenderer.setYAxisMin(-9000);
		myRenderer.setYAxisMax(9000);

		return myRenderer;
	}

	public XYMultipleSeriesRenderer getFFTRenderer() {

		XYSeriesRenderer r2 = new XYSeriesRenderer();
		r2.setColor(Color.RED);
		r2.setLineWidth(2);
		r2.setPointStyle(PointStyle.SQUARE);

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r2);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, true);
		myRenderer.setZoomButtonsVisible(true);

		myRenderer.setChartTitle("FMCW Radar Data Plot");
		myRenderer.setChartTitleTextSize(30);

		myRenderer.setLegendTextSize(20);

		myRenderer.setZoomRate(10);

		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.getXLabelsAlign();
		myRenderer.setXLabelsColor(Color.BLACK);
		myRenderer.setYLabelsColor(0, Color.BLACK);
		myRenderer.setShowAxes(true);
		myRenderer.setLabelsColor(Color.BLACK);

		myRenderer.setXTitle("Frequency (kHz)");
		myRenderer.setYTitle("Power (dB)");
		myRenderer.setAxisTitleTextSize(20);

		// background color of the PLOT ONLY
		myRenderer.setApplyBackgroundColor(true);
		// Color.TRANSPARENT would show the background of the app (MainActivity)
		myRenderer.setBackgroundColor(Color.LTGRAY); 

		// sets the background area of the object itself
		// does not change the plots background
		myRenderer.setMarginsColor(Color.WHITE); 

		myRenderer.setGridColor(Color.DKGRAY);
		myRenderer.setXLabels(20);
//		myRenderer.setYLabels(9);
		myRenderer.setShowGrid(true);
		myRenderer.setMargins(new int[] {35, 50, 15, 30});

		// Minimum & Max values to view plot area
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(256);
		myRenderer.setYAxisMin(0);
		myRenderer.setYAxisMax(120);
		
		myRenderer.addXTextLabel(5, "word");
		return myRenderer;
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer) {

		renderer.setPanEnabled(true, true);
		renderer.setZoomEnabled(true, true);
		renderer.setZoomButtonsVisible(true);
		renderer.setLegendTextSize(20);
		
		renderer.setZoomRate(10);

		renderer.setAxesColor(Color.BLACK);
		renderer.getXLabelsAlign();
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setShowAxes(true);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXTitle("Frequency (kHz)");
		renderer.setYTitle("Power (dB)");

		renderer.setAxisTitleTextSize(20);

		// background color of the PLOT ONLY
		renderer.setApplyBackgroundColor(true);
		// Color.TRANSPARENT would show the background of the app (MainActivity)
		renderer.setBackgroundColor(Color.LTGRAY); 

		// sets the background area of the object itself
		// does not change the plots background
		renderer.setMarginsColor(Color.WHITE); 


		renderer.setGridColor(Color.DKGRAY);
		renderer.setXLabels(20);
		renderer.setYLabels(9);
		renderer.setShowGrid(true);

		renderer.setMargins(new int[] {35, 50, 15, 30});
	} 

} //END OF MAINACTIVITY CODE!
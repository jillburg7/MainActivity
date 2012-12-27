package appliedradar.bluetooth.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.Toast;


public class MainActivity extends Activity implements OnMenuItemClickListener {
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

    // Layout Views
    //private ListView mConversationView;
    //private EditText mOutEditText;
   // private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    //STRING ARRAY for Data
    private ArrayAdapter<String> mStringData;
    //DATA ARRAY of 512 data points
    double[] newDataArray = new double[512];
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    //END OF BT INITIALIZERS
    
    
	ShareActionProvider mShareActionProvider;
	double[] dataArray;
	GraphicalView mChartView;
	XYMultipleSeriesDataset mDataset;
	XYMultipleSeriesRenderer mRenderer;
	boolean ren2 =false;

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
		if (mChartView != null) {
			mChartView.repaint();
		}
	}
	
	private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        //mConversationView = (ListView) findViewById(R.id.in);
        //mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        //mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        //mSendButton = (Button) findViewById(R.id.button_send);
        /*mSendButton.setOnClickListener(new OnClickListener() {
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
    private void sendMessage(String message) {
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

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }
    
    int n = 0;
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
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
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                mStringData.add(readMessage);
                try {
                	 if (n<512){
                	newDataArray[n] = Float.parseFloat(readMessage);
                	if(D) Log.e(TAG, String.valueOf(n) + String.valueOf(newDataArray[n]));
                	 }
                	 n++;
                	} catch (NumberFormatException e) {
                	  // the array did not have a double
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
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivty.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
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

		// Return true to display menu
		//if(deflate3 == true){
			//getMenuInflater().deflate(R.id.bt_settings, menuItem);
		//}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.menu_settings:
        	Intent settings = new Intent(this, SettingsActivity.class);
    		startActivity(settings);
        	return true;
        /*case R.id.bt_settings:
        	if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	           //findViewById(R.id.bt_settings).
	            //findViewById(R.id.bt_settings).setVisibility(View.GONE);
	            //findViewById(R.id.bt_settings).setVisibility(View.VISIBLE);
	        // Otherwise, setup the chat session
	        } else {
	            if (mChatService == null) setupChat();
	        }
        	return true;*/
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
    /*switch (item.getItemId()) {
		case R.id.text_file:
			if (item.isChecked()) {
				// item.setChecked(false);
				Toast.makeText(this, "Was true, set False", Toast.LENGTH_SHORT)
						.show();
			} else {
				item.setChecked(true);
				Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.binary_file:
			if (item.isChecked()) {
				item.setChecked(false);
				Toast.makeText(this, "Was true, set False", Toast.LENGTH_SHORT)
						.show();
			} else {
				item.setChecked(true);
				Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.compressed_file:
			if (item.isChecked()) {
				item.setChecked(false);
				Toast.makeText(this, "Was true, set False", Toast.LENGTH_SHORT)
						.show();
			} else {
				item.setChecked(true);
				Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.matlab_file:
			if (item.isChecked()) {
				item.setChecked(false);
				Toast.makeText(this, "Was true, set False", Toast.LENGTH_SHORT)
						.show();
			} else {
				item.setChecked(true);
				Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}*/
	}

	
	// For testing button & popup menu purposes only!
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.raw_plot:
			Toast.makeText(this, "Selected Raw Plot", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.range_plot:
			Toast.makeText(this, "Selected Range Plot", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.doppler_plot:
			Toast.makeText(this, "Selected Doppler Plot", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.fft_plot:
			Toast.makeText(this, "Selected FFT Plot", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.sar_plot:
			Toast.makeText(this, "Selected SAR Plot", Toast.LENGTH_SHORT)
					.show();
			break;
		}
		return false;
	}
	
	
	
	
	
	
	

	
	// Saving pop-up menu
	public void saveMenu(View display) {
		PopupMenu popup2 = new PopupMenu(this, display);
		// popup2.setOnMenuItemClickListener(this);
		popup2.inflate(R.menu.saving_menu);
		popup2.show();
	}
	
	
	
	// SENDS COMMAND TO FMCW RADAR KIT BY SELECTING THE "Collect Data" BUTTON
	public void sendCollectSignal(View toast) {
		sendMessage("Hi Jill");
	}

	public void openArchive(View newActivity) {
		Toast.makeText(this, "Selected Load Data", Toast.LENGTH_SHORT).show();
		Intent archiveData = new Intent(this, DisplayArchive.class);
		startActivity(archiveData);
	}

	public void plotMenu(View plotMe) {
		
		mDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("Simulated Data: Fs = 44KHz");	
		double[] array = getDataFromFile();
		for (int i=0; i<512; i++){				
			dataSeries.add(i, array[i]);
		}
		mDataset.addSeries(dataSeries);

		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer = getMyRenderer();
		
		if (mChartView != null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint();
		}
		
		Toast.makeText(this, "Selected Plot", Toast.LENGTH_SHORT).show();
	}
	
	public void plotFFT(View fftPlot) {
		mDataset.removeSeries(0);
		
		XYSeries dataSeries2 = new XYSeries("FFT of Simulated Data");
		double[] array2= getFftData();
		int j=0;
		for (j=0; j<(512/2); j++){
			dataSeries2.add(j, array2[j]);
		}
		mDataset.addSeries(dataSeries2);
		
		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer = getMyFFTRenderer();
		if (mChartView != null) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			layout.addView(mChartView);
		} else {
			mChartView.repaint();
		}
		
		Toast.makeText(this, "Selected Plot FFT", Toast.LENGTH_SHORT).show();
	}

	

	public double[] getDataFromFile() {
		File sdcard = Environment.getExternalStorageDirectory();

		// Get the text file
		// NEED TO SPECIFICALLY CHANGE THIS LINE OF CODE TO BE MORE UNIVERSAL!
		File file = new File(sdcard, "3kHz_44KHzFs.txt");

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
	public XYMultipleSeriesDataset getMyDefaultData() {

		XYMultipleSeriesDataset myDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries(" ");
		myDataset.addSeries(dataSeries);
		return myDataset;
	}
	
	public XYMultipleSeriesDataset getMyData() {

		XYMultipleSeriesDataset myDataset = new XYMultipleSeriesDataset();
//		
//		XYSeries dataSeries = new XYSeries("Simulated Data: Fs = 44KHz");	
//		double[] array = getDataFromFile();
//
//		for (int i=0; i<512; i++){				
//			dataSeries.add(i, array[i]);
//		}
//		myDataset.addSeries(dataSeries);
		
//		XYSeries dataSeries2 = new XYSeries("FFT data");
//		double[] array2= getFftData();
//		System.out.print("in getMyData, array2 =" + array2);
//		int j=0;
//		for (j=0; j<(512/2); j++){
//			dataSeries2.add(j, array2[j]);
//		}
//		mDataset.addSeries(dataSeries2);
		
		return myDataset;
	}
	
	public double[] getFftData() {

		double[] realArray = dataArray;
		double[] imagArray = new double[512];
	
		FFTcalc fftData = new FFTcalc();
		double[] fftArray = fftData.fft(realArray,imagArray, true);
		int n = realArray.length;
		double[] imagFFT = new double[fftArray.length/2];
		double[] realFFT = new double[fftArray.length/2];
		double radice = 1 / Math.sqrt(n);	
		
		for(int i=0; i< fftArray.length; i+=2) {
			int i2 = i/2;
			realFFT[i2] = fftArray[i] / radice;
			imagFFT[i2] = fftArray[i + 1] / radice;
		}
		
		double[] fftOutput = new double[n];
		for (int i=0; i<n; i++){
			fftOutput[i] = Math.sqrt(Math.pow(realFFT[i], 2) + Math.pow(imagFFT[i], 2)); 
		}
		
		
		for (int i=0; i<(n/2); i++){
			fftOutput[i] = 20*Math.log10(fftOutput[i]);
			
		}
		
		return fftOutput;	
	}
	
	public XYMultipleSeriesRenderer getMyDefaultRenderer() {

		XYSeriesRenderer r1 = new XYSeriesRenderer();
		r1.setColor(Color.BLUE);
		r1.setLineWidth(2);
		r1.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
		r1.setFillPoints(true); // not for point or x don't know how to set point size or point color

//		XYSeriesRenderer r2 = new XYSeriesRenderer();
//		r2.setColor(Color.RED);
//		r2.setLineWidth(2);
//		r2.setPointStyle(PointStyle.SQUARE);

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r1);
//		myRenderer.addSeriesRenderer(r2);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, false);
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
		return myRenderer;
	}
	

	public XYMultipleSeriesRenderer getMyRenderer() {

		XYSeriesRenderer r1 = new XYSeriesRenderer();
		r1.setColor(Color.BLUE);
		r1.setLineWidth(2);
		r1.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
		r1.setFillPoints(true); // not for point or x don't know how to set point size or point color

//		XYSeriesRenderer r2 = new XYSeriesRenderer();
//		r2.setColor(Color.RED);
//		r2.setLineWidth(2);
//		r2.setPointStyle(PointStyle.SQUARE);

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r1);
//		myRenderer.addSeriesRenderer(r2);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, false);
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
		return myRenderer;
	/*	mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
//		XYSeriesRenderer renderer1 = new XYSeriesRenderer();
//		XYSeriesRenderer renderer2 = new XYSeriesRenderer();
//		if (ren2 == false) {
//			renderer = renderer2;
//			mRenderer.setXTitle("Frequency (kHz)");
//			mRenderer.setYTitle("Power (dB)");
//		}
//		else{
//			renderer = renderer1;
//		}
		
		renderer.setColor(Color.BLUE);
		renderer.setLineWidth(2);
		renderer.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
		renderer.setFillPoints(true); // not for point or x don't know how to set point size or point color
		
//		renderer2.setColor(Color.RED);
//		renderer2.setLineWidth(2);
//		renderer2.setPointStyle(PointStyle.SQUARE);
		mRenderer.setXTitle("Samples");
		mRenderer.setYTitle("Amplitude");
		mRenderer.setAxisTitleTextSize(20);
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setXLabels(20);
		mRenderer.setYLabels(9);
		
	    mRenderer.setXAxisMin(0);
	    mRenderer.setXAxisMax(5);
	    mRenderer.setYAxisMin(0);
	    mRenderer.setYAxisMax(250);*/
	}
	
	
	public XYMultipleSeriesRenderer getMyFFTRenderer() {

//		XYSeriesRenderer r1 = new XYSeriesRenderer();
//		r1.setColor(Color.BLUE);
//		r1.setLineWidth(2);
//		r1.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
//		r1.setFillPoints(true); // not for point or x don't know how to set point size or point color

		XYSeriesRenderer r2 = new XYSeriesRenderer();
		r2.setColor(Color.RED);
		r2.setLineWidth(2);
		r2.setPointStyle(PointStyle.SQUARE);

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r2);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, false);
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
		myRenderer.setYLabels(9);
		myRenderer.setShowGrid(true);
		return myRenderer;
	}
	// FFT CALLED
//	XYSeriesRenderer r2 = new XYSeriesRenderer();
//	renderer.setColor(Color.RED);
//	renderer.setLineWidth(2);
//	renderer.setPointStyle(PointStyle.SQUARE);

/*		mRenderer.setPanEnabled(true, true);
	mRenderer.setZoomEnabled(true, true);
	mRenderer.setZoomButtonsVisible(true);
	mRenderer.setLegendTextSize(20);

	mRenderer.setZoomRate(10);

	mRenderer.setAxesColor(Color.BLACK);
	mRenderer.getXLabelsAlign();
	mRenderer.setXLabelsColor(Color.BLACK);
	mRenderer.setYLabelsColor(0, Color.BLACK);
	mRenderer.setShowAxes(true);
	mRenderer.setLabelsColor(Color.BLACK);

	mRenderer.setXTitle("Frequency (kHz)");
	mRenderer.setYTitle("Power (dB)");
	mRenderer.setAxisTitleTextSize(20);

	// background color of the PLOT ONLY
	mRenderer.setApplyBackgroundColor(true);
	// Color.TRANSPARENT would show the background of the app (MainActivity)
	mRenderer.setBackgroundColor(Color.LTGRAY); 

	// sets the background area of the object itself
	// does not change the plots background
	mRenderer.setMarginsColor(Color.WHITE); 


	mRenderer.setGridColor(Color.DKGRAY);
	mRenderer.setXLabels(20);
	mRenderer.setYLabels(9);
	mRenderer.setShowGrid(true);
	
	mRenderer.setMargins(new int[] {20, 50, 15, 30});*/
//	myRenderer.setChartTitle("FMCW Radar Data");
	
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
			
			renderer.setMargins(new int[] {20, 50, 15, 30});
	  } 

} //END OF MAINACTIVITY CODE!
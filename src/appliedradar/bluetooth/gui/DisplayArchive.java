package appliedradar.bluetooth.gui;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayArchive extends Activity {

	// Debugging
	private static final String TAG = "DisplayArchiveActivity";

	private File[] mFileList;
	private String[] mFiles;
	private String filePath;
	private String fileName;
	public static String EXTRA_FILE_INFO = "file_info";
	private int toDelete;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_display_archive);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/FMCW File Archive");
		if (fileDir.exists() && fileDir.isDirectory()) {
			mFileList = fileDir.listFiles();
			mFiles = fileDir.list();
		} else {
			createDirIfNotExists("/FMCW File Archive");
		}
		//MAKES FILES VIEWABLE to Here
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 
		
		// sorts the file order with respect to thier file names
		Arrays.sort(mFileList, new Comparator<File>() {
		    public int compare(File f1, File f2) {
		        return (f1.getName()).compareTo(f2.getName());
		    } });	
		
		
		// View to display files in directory
		ListView list1 = (ListView) findViewById(R.id.list1);

		ArrayAdapter<String> fileNames = new ArrayAdapter<String>(this, R.layout.file_name, mFiles);
		
		// sorts the Strings of the file names that are displayed
		fileNames.sort(new Comparator<String>() {
			public int compare(String f1, String f2)
		    {
		        return (f1).compareTo(f2);
		    }});
		
		list1.setAdapter(fileNames);
		list1.setOnItemClickListener(mFileClickListener);
	}


	// The on-click listener for all files in the ListViews
	private OnItemClickListener mFileClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parentPath, View view, int position, long id) {

			fileName = ((TextView) view).getText().toString();
			File file = mFileList[position];
			
			TextView infoLabels = (TextView) findViewById(R.id.textView2);
			TextView info = (TextView) findViewById(R.id.textView3);
//			TextView paramLabels = (TextView) findViewById(R.id.paramLabels);
//			TextView paramDetails = (TextView) findViewById(R.id.paramDetails);
			
			filePath = file.getPath();
			String parent = file.getParent();
			String size = "" + file.length();
			FileInfo information = new FileInfo(filePath);

			infoLabels.setText("Parent Path:" + "\n" + "Name:" + "\n" + 
								"Date Created:" + "\n" + "Size:");
			info.setText(	parent + "\n" + fileName + "\n" + 
							information.created + "\n" + 
							size + "\n");
			
//			String[] parameters = new String[5];
//			parameters = information.parameters;
//			paramLabels.setText("Parameters:");
//			
//			try {
//				paramDetails.setText(parameters[0]);
//			} catch (Exception e) {
//				Log.e("ParameterDisplay", "nothing");
//			}
			
			findViewById(R.id.button_open).setVisibility(View.VISIBLE);
			findViewById(R.id.button_delete).setVisibility(View.VISIBLE);
			findViewById(R.id.information).setVisibility(View.VISIBLE);
			
			toDelete = position;
		}
	};

	public void openFile(View view) {

		Toast toast = Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_SHORT);
		toast.show();

		// Create the result Intent and include the file name
		Intent intent = new Intent();
		intent.putExtra(EXTRA_FILE_INFO, filePath);

		// Set result and finish this Activity
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void deleteFile(View view) {
		Toast toast = Toast.makeText(getApplicationContext(), "Deleting " + fileName + "...", Toast.LENGTH_SHORT);
		toast.show();
		
		File file = mFileList[toDelete];
		file.delete();
	}


	public static boolean createDirIfNotExists(String path) {//STUFF I CHANGED  Here
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory() + path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e(TAG, "Problem creating Image folder");
				ret = false;
			}
		}
		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_archive, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

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

			finish();
			//NavUtils.navigateUpFromSameTask(this);
			//return true;
		}
		return super.onOptionsItemSelected(item);
	}




}
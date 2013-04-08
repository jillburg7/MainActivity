package appliedradar.bluetooth.gui;

import java.io.File;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayArchive extends Activity implements OnItemSelectedListener {

	// Debugging
	private static final String TAG = "DisplayArchiveActivity";
	private static final boolean D = true;

	private File[] mFileList;
	String fileName;


	public static String EXTRA_FILE_INFO = "file_info";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_display_archive);


		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);


		File fileDir = new File("/mnt/sdcard/FMCW File Archive");
		if (fileDir.exists() && fileDir.isDirectory()) {	//STUFF I CHANGED Here
			mFileList = fileDir.listFiles();
		} else {
			createDirIfNotExists("/FMCW File Archive");
		}
		//MAKES FILES VIEWABLE to Here
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 


//		for(int i = 0; i < mFileList.length; i++) {
//			fileName = mFileList[i].getName();
//		}
		ListView list1 = (ListView) findViewById(R.id.list1);

		ArrayAdapter<File> fileAdapter = new ArrayAdapter<File>(this, R.layout.file_name, mFileList);
		list1.setAdapter(fileAdapter);
		list1.setOnItemClickListener(mFileClickListener);
		
		
//		// returns number of bytes in this file
//		String fileSize = "" + fileDir.length();
//
//		// Find and set up the ListView for FMCW files
//		ListView fileListView = (ListView) findViewById(R.id.files_archived);
//		fileListView.setAdapter(fileAdapter);
//		fileListView.setOnItemClickListener(mFileClickListener);

		// If there are files in directory, add each one to the ArrayAdapter
//		if (mFileList.length > 0) 
//			findViewById(R.id.title_file_list).setVisibility(View.VISIBLE);

	}


	// The on-click listener for all files in the ListViews
	private OnItemClickListener mFileClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parentPath, View view, int position, long id) {
			fileName = ((TextView) view).getText().toString();
			File file = mFileList[position];

			TextView infoLabels = (TextView) findViewById(R.id.textView2);
			TextView info = (TextView) findViewById(R.id.textView3);


//			String absolutePath = file.getAbsolutePath();
//	        "AbsolutePath:    " + AbsolutePath + "\n"
			String path = file.getPath();
			String parent = file.getParent();
			String name = file.getName();
			String size = "" + file.length();
			FileInfo information = new FileInfo(path);

			infoLabels.setText(	"Parent Path:" + "\n" + "Name:" + "\n" + "Date Created:" + "\n" + "Size:" );
			info.setText( 	parent + "\n" + name + "\n" + information.getDateCreated() + "\n" + size);
		}

	};

	public void openFile(View view) {
		

		Toast toast = Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_SHORT);
		toast.show();
		
		// Create the result Intent and include the file name
		Intent intent = new Intent();
		intent.putExtra(EXTRA_FILE_INFO, fileName);

		// Set result and finish this Activity
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {


	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
	

	public static boolean createDirIfNotExists(String path) {//STUFF I CHANGED  Here
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory() + path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating Image folder");
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
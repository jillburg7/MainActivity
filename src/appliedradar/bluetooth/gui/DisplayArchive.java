package appliedradar.bluetooth.gui;

import java.io.File;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class DisplayArchive extends Activity {

	// Debugging
	private static final String TAG = "DisplayArchiveActivity";
	private static final boolean D = true;
	
	private File[] mFileList;
	
	public static String EXTRA_FILE_INFO = "file_info";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_display_archive);

		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

//		File internalMemory = Environment.getExternalStorageDirectory();
		File internalMemory = new File("/mnt/sdcard/FMCW File Archive");
		
		mFileList = internalMemory.listFiles();
		ArrayAdapter<File> fileAdapter = new ArrayAdapter<File>(this, R.layout.file_name, mFileList);

		// Find and set up the ListView for archived .txt files
		ListView fileListView = (ListView) findViewById(R.id.files_archived);
		fileListView.setAdapter(fileAdapter);
		fileListView.setOnItemClickListener(mFileClickListener);

		// If there are paired devices, add each one to the ArrayAdapter
		if (mFileList.length > 0) 
			findViewById(R.id.title_file_list).setVisibility(View.VISIBLE);

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

	// The on-click listener for all files in the ListViews
	private OnItemClickListener mFileClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String fileInfo = ((TextView) arg1).getText().toString();

			// Create the result Intent and include the MAC address
			Intent intent = new Intent();
			intent.putExtra(EXTRA_FILE_INFO, fileInfo);

			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
			finish();
			
		}

	};
}
package appliedradar.bluetooth.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

/**
 * 
 * 
 *	@author jillburg
 */
public class NewFile extends FileInfo {


	private String root;
	private File myDir;
	protected String name;
	protected String fileKind;
	public String[] radarParameters;
	private File file;
	
	public NewFile() {
		root = Environment.getExternalStorageDirectory().toString(); 
		myDir = new File(root + "/FMCW File Archive");	// name of directory
		name = setFileName();
		file = new File (myDir, name);
		dataParameters();
	}

	/**
	 * add parameters to first few lines to file
	 * include in file information in the FileInfo text view in the file archive
	 */
	public void dataParameters(){
		radarParameters = MainActivity.currentParameters;
		fileHeader(radarParameters);
	}
	
	/**
	 * 
	 * @param context
	 * @param string
	 * @throws IOException
	 */
	public void createFile(Context c, String string) throws IOException{
		if (file.exists ()){
			FILE_NUMBER++;
			name = setFileName();
		}
		else
			FILE_NUMBER = 0;
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(string.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


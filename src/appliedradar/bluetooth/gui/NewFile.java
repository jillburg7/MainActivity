package appliedradar.bluetooth.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

public class NewFile extends FileInfo {

	
	public NewFile() {}
	
	public NewFile(String path) {
		super(path);
	}
	
	
	/**
	 * 
	 * @return date & time
	 */
	public String timeStamp() {
		Date myDate = new Date();
		return (DateFormat.getDateInstance().format(myDate) + " " + DateFormat.getTimeInstance().format(myDate));
	}

	
	/**
	 * 
	 * @param context
	 * @param string
	 * @throws IOException
	 */
	public void createFile(Context c, String string) throws IOException{
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/FMCW File Archive");	// name of directory
		
		String fname = timeStamp() + ".txt";	// name of file
		File file = new File (myDir, fname);
		if (file.exists ()){
			file.delete (); 
		}
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


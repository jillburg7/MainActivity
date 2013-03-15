package appliedradar.bluetooth.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

public class NewFile {

	public String timeStamp() {
		Date myDate = new Date();
		return (DateFormat.getDateInstance().format(myDate) + " " + DateFormat.getTimeInstance().format(myDate));
	}

	public void createFile(Context c, String string) throws IOException{
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/FMCW File Archive");
		
		String fname = timeStamp() + ".txt";
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


//	public void createFile(Context c, String string) throws IOException{
//		String FILENAME = timeStamp();
//		
//		FileOutputStream fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
//		fos.write(string.getBytes());
//		fos.close();
//	}



// currently creates a folder on the actual sdcard,
// but it doesn't allow anything to be placed inside of it
/*	//	String external_sd = Environment.getExternalStorageDirectory().toString();
//	File myDir = new File(external_sd + "/saved_data");  
File myDir = new File("/mnt/external_sd" + "/saved_data");  

//File myDir = new File("/mnt/external_sd/saved_data"); 
*/		
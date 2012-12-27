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
		File myDir = new File(root + "/saved_data");    
		myDir.mkdirs();
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


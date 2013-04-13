package appliedradar.bluetooth.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

public class NewFile extends FileInfo {

	private String root;
	private File myDir;
	private String name;
	private Kind fileKind;
	
	public NewFile() {
		root = Environment.getExternalStorageDirectory().toString(); 
		myDir = new File(root + "/FMCW File Archive");	// name of directory
		fileName();
		fileKind = dataKind;
	}
	

	private void fileName(){
		Date date = dateCreated;
		name = DateFormat.getDateInstance(DateFormat.SHORT).format(date) + "_" + dataKind;
	}
	
	/**
	 * 
	 * @param context
	 * @param string
	 * @throws IOException
	 */
	public void createFile(Context c, String string) throws IOException{
		
		String fname = created + "_" + dataKind;	// name of file 
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


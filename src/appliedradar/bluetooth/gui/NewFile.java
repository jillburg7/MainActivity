package appliedradar.bluetooth.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class NewFile extends FileInfo {

	private String root;
	private File myDir;
	private String name;
//	private Kind fileKind;
	private String fileKind;
	
	public NewFile() {
		root = Environment.getExternalStorageDirectory().toString(); 
		myDir = new File(root + "/FMCW File Archive");	// name of directory
		name = getFileName();
		fileKind = dataKindStr;
	}
	

//	private void fileName(){
//		Date date = dateCreated;
////		name = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date) + "_" + dataKind;
//		name = getFileDate();
//	}
	
	/**
	 * 
	 * @param context
	 * @param string
	 * @throws IOException
	 */
	public void createFile(Context c, String string) throws IOException{
		
//		String fname = created + "_" + fileKind;	// name of file 
//		File file = new File (myDir, fname);
		File file = new File (myDir, name);
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


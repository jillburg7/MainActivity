package appliedradar.bluetooth.gui;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class FileInfo {// extends File {
	
	protected static int FILE_NUMBER = 0;
	
	protected String fileName;
	protected Date dateCreated;
	protected String created;
	String[] parameters;

	public enum Kind{ RAW, RANGE };

	public FileInfo() {
		dateCreated();
		created = getDateCreated();
	}

	/**
	 * Constructor
	 * @param path
	 */
	public FileInfo(String path) {
		dateCreated = new Date(new File(path).lastModified());
		created = getDateCreated();
	}

	/**
	 * 
	 * @return date & time
	 */
	public Date getTimeStamp() {
		Date myDate = new Date();
		return myDate;
	}
	

	private void dateCreated() {
		dateCreated = getTimeStamp();
	}


	public String setFileName() {
	    // date and time
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
//		int mHour = c.get(Calendar.HOUR_OF_DAY);
//		int mMinute = c.get(Calendar.MINUTE);
		
		if (FILE_NUMBER > 0)
			fileName = mYear + "" + mMonth + "" + mDay + "_" + "test" + FILE_NUMBER;
		else if (FILE_NUMBER == 0)
			fileName = mYear + "" + mMonth + "" + mDay + "_" + "test";
		return fileName;
	}
	

	/**
	 * 
	 * @return
	 */
	private String getDateCreated() {
		return (DateFormat.getDateInstance(DateFormat.SHORT).format(dateCreated) + " " +
				DateFormat.getTimeInstance(DateFormat.SHORT).format(dateCreated));
	}

	/**
	 * File header - first few lines of file will be parameters
	 */
	public void fileHeader(String[] parameters) {
		this.parameters = parameters;
	}

}

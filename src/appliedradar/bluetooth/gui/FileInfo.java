package appliedradar.bluetooth.gui;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class FileInfo {// extends File {

	protected String fileName;
	protected Kind dataKind;
	protected String dataKindStr;
	protected Date dateCreated;
	protected String created;
	private Date lastOpened;
	//	private static String somethingNeededHere;

	public enum Kind{ RAW, RANGE };

	public FileInfo() {
		dateCreated();
		created = getDateCreated();
//		created = getFileDate();
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
	
	public String getFileName() {
//		int date = DateFormat.YEAR_FIELD + DateFormat.MONTH_FIELD + DateFormat.DATE_FIELD;
		String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateCreated);
		String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(dateCreated);
		return (date + "_" + time + "_" + dataKindStr);
	}

	private void dateCreated() {
		dateCreated = getTimeStamp();
	}


	public String setfileName() {
	    // date and time
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		
		
//		StringBuilder string = new StringBuilder().append();
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
	 * 
	 * @return
	 */
	public String getKind() {
		return "" + dataKind;
	}

	/**
	 * 
	 * @param someKind the Kind of data being Saved
	 */
	public void setKind(Kind someKind) {
		dataKind = someKind;
	}
	
	public void setKind(String someKind) {
		dataKindStr = someKind;
	}
	
	/**
	 * File header - first few lines of file will be parameter
	 */
	public void fileHeader() {
		
	}

}

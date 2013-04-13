package appliedradar.bluetooth.gui;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;


public class FileInfo {// extends File {

	protected Kind dataKind;
	protected Date dateCreated;
	protected String created;
	private Date lastOpened;
	//	private static String somethingNeededHere;

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



	/**
	 * 
	 * @return
	 */
	private String getDateCreated() {
		return (DateFormat.getDateInstance(DateFormat.FULL).format(dateCreated) + " " +
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
	
	
	/**
	 * File header - first few lines of file will be parameter
	 */
	public void fileHeader() {
		
	}

}

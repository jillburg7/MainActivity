package appliedradar.bluetooth.gui;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;


public class FileInfo extends File {

	private Kind dataKind;
	private Date dateCreated;
	private Date lastModified;
	private Date lastOpened;
	private static String somethingNeededHere;
	
	public enum Kind{ RAW, RANGE };
	
	public FileInfo() {
		super(somethingNeededHere);
	}
	
	/**
	 * Constructor
	 * @param path
	 */
	public FileInfo(String path) {
		super(path);
		// TODO Auto-generated constructor stub
		
		
	}

	
	/**
	 * 
	 * @return
	 */
	public Date dateCreated() {
		Date created = new Date(this.lastModified());
		return created;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDateCreated() {
		Date created = new Date(this.lastModified());
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(created) + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(created);
		return date;
	}
	
	/**
	 * 
	 * @return
	 */
	public Kind getKind() {
		return dataKind;
	}
	
}

package appliedradar.bluetooth.gui;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;


public class FileInfo extends File {

	public enum Type{ RAW, RANGE };
	
	public FileInfo(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	public Date dateCreated() {
		Date created = new Date(this.lastModified());
		return created;
	}
	
	public String getDateCreated() {
		Date created = new Date(this.lastModified());
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(created) + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(created);
		return date;
	}
	
	public Type getType() {
		
		return null;
	}
	
}

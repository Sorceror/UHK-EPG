/**
 * 
 */
package tvdata;

import java.net.URL;
import java.util.Date;

/**
 * Trida predstavuje jeden televizni porad/show
 * @author Pavel Janecka
 */
public class Program {
	private String title;
	private Date startTime;
	private Date endTime;
	private int length;
	private String description;
	private URL imageURL;
	
	/**
	 * Konstruktor poradu
	 * @param title String jmeno poradu
	 * @param startTime {@link Date} cas a datum zacatku poradu
	 * @param endTime {@link Date} cas a datum konce poradu
	 * @param description String popisek poradu
	 */
	public Program(String title, Date startTime, Date endTime, String description) {
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		initLength();
	}

	private void initLength() {
		this.length = (int)((endTime.getTime() - startTime.getTime()) / 1000);
	}
	
	/**
	 * @return the image
	 */
	public URL getImageURL() {
		return imageURL;
	}

	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(URL imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * @return the name
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		initLength();
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
		initLength();
	}
}

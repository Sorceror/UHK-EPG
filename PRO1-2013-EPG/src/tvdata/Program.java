/**
 * 
 */
package tvdata;

import java.awt.Image;
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
	private Image image;
	
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
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
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

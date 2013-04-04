/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;

/**
 * Constants for gui (colors, fonts, etc) and helper methods
 * @author Pavel Janecka
 */
public class Utils {
	/** Default font for whole application */
	public static final Font DEFAULT_APP_FONT = new Font("Segoe UI", Font.PLAIN, 13);
	/** Default font color for whole application */
	public static final Color DEFAULT_APP_FONT_COLOR = Color.BLACK;
	/** Default background color for application components */
	public static final Color DEFAULT_APP_BACKGROUND_COLOR = Color.WHITE;	
	/** Default text if no program selected */
	public static final String STR_NO_PROGRAM = "No program selected";
	/** Relative path to folder with EPG data */
	public static final String DATA_FOLDER_PATH = "./data";
	
	/**
	 * Enable antialiasing on graphics context
	 * @param g {@link Graphics} graphics context
	 */
	public static void enableAntialiasing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	/**
	 * Enable font antialiasing to graphics and draw selected string into middle of component canvas 
	 * @param str String to draw
	 * @param g {@link Graphics} to paint at
	 * @param component {@link JComponent} parent for graphics context
	 */
	public static void drawAntialiasedStringInMiddle(String str, Graphics g, JComponent component) {
		Graphics2D g2d = (Graphics2D) g;
		enableAntialiasing(g2d);
		Rectangle2D rect = g.getFontMetrics().getStringBounds(str, g);
		g2d.drawString(str, (int) (component.getWidth() / 2 - rect.getWidth() / 2), (int)(component.getHeight() / 2 + rect.getHeight() / 2));
	}
	
	/**
	 * Return time portion (hours, minutes, seconds) from selected date as total in seconds
	 * @param date {@link Date} instance
	 * @return long seconds
	 */
	public static long getTimeInSeconds(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
	}
	
	/**
	 * Return time diference between two dates in new {@link Date} instance
	 * @param late {@link Date} later date
	 * @param soon {@link Date} sooner date
	 * @return {@link Date} instance
	 */
	public static Date dateDifference(Date late, Date soon) {
		return new Date(late.getTime() - soon.getTime());
	}
	
	/**
	 * Return midnight {@link Date} instance of selected date
	 * @param date {@link Date} to be set to midnight
	 * @return {@link Date} instance
	 */
	public static Date getMidnight(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new Date(c.getTimeInMillis());
	}
	
	/**
	 * Return same date with one day added
	 * @param date {@link Date} instance
	 * @return {@link Date} instance
	 */
	public static Date getTomorrow(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		return new Date(c.getTimeInMillis());
	}
	
	/**
	 * Return changed first date/time affected by time from second date/time
	 * @param first {@link Date} instance
	 * @param second {@link Date} instance
	 * @return {@link Date}
	 */
	public static Date setTimeFromSecondDate(Date first, Date second) {
		Calendar secondCal = Calendar.getInstance();
		secondCal.setTime(second);
		Calendar firstCal = Calendar.getInstance();
		firstCal.setTime(first);
		firstCal.set(Calendar.HOUR_OF_DAY, secondCal.get(Calendar.HOUR_OF_DAY));
		firstCal.set(Calendar.MINUTE, secondCal.get(Calendar.MINUTE));
		firstCal.set(Calendar.SECOND, secondCal.get(Calendar.SECOND));
		firstCal.set(Calendar.MILLISECOND, secondCal.get(Calendar.MILLISECOND));
		return firstCal.getTime();
	}
	
	/**
	 * Return flag if two dates have same date (not time)
	 * @param first {@link Date} instance
	 * @param second {@link Date} instance
	 * @return boolean flag
	 */
	public static boolean sameDate(Date first, Date second) {
		Calendar calFirst = Calendar.getInstance();
		calFirst.setTime(first);
		Calendar calSecond = Calendar.getInstance();
		calSecond.setTime(second);
		
		if (calFirst.get(Calendar.YEAR) != calSecond.get(Calendar.YEAR)) return false;
		if (calFirst.get(Calendar.MONTH) != calSecond.get(Calendar.MONTH)) return false;
		if (calFirst.get(Calendar.DAY_OF_MONTH) != calSecond.get(Calendar.DAY_OF_MONTH)) return false;
		return true;
	}
}

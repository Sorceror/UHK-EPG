/**
 * 
 */
package gui;

import gui.utils.TimeThread;

import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;

/**
 * Zobrazuje aktualni vybrany den na platno
 * @author Pavel Janecka
 */
public class DatePanel extends JComponent {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy (EEEE)");
	private TimeThread timeThread;
	private Date currentDate;
	
	/**
	 * Konstruktor panelu zobrazujiciho cas
	 */
	public DatePanel() {
		timeThread = new TimeThread(this);
		timeThread.start();
		
		currentDate = new Date();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		String strTime = dateFormat.format(currentDate);

		boolean isToday = false;
		if (Utils.sameDate(currentDate, new Date())) isToday = true;
		g.setFont(Utils.DEFAULT_APP_FONT);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		strTime = strTime.toLowerCase();
		if (isToday) strTime = " ~ " + strTime + " ~ ";
		Utils.drawAntialiasedStringInMiddle(strTime, g, this);
	}

	/**
	 * Nastavi aktualne zobrazovany den
	 * @param currentDate {@link Date} instance
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
}

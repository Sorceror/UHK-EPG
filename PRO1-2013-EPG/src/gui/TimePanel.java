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
 * Zobrazuje aktualni cas na platno
 * @author Pavel Janecka
 */
public class TimePanel extends JComponent {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	private TimeThread timeThread;
	
	/**
	 * Konstruktor panelu zobrazujiciho cas
	 */
	public TimePanel() {
		timeThread = new TimeThread(this);
		timeThread.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Date now = new Date();
		String strTime = dateFormat.format(now);

		g.setFont(Utils.DEFAULT_APP_FONT.deriveFont(30f));
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
		
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		Utils.drawAntialiasedStringInMiddle(strTime, g, this);
	}
	
}

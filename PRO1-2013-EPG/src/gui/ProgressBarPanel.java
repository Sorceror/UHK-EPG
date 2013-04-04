/**
 * 
 */
package gui;

import gui.utils.TimeThread;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;

import tvdata.Program;

/**
 * Trida predstavuje panel na ktery je vykreslen progress bar aktualniho poradu
 * @author Pavel Janecka
 */
public class ProgressBarPanel extends JComponent {
	private Program currentProgram;
	private TimeThread timeThread;
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	private Stroke fullStroke = new BasicStroke(1);
	private Stroke thinStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	private final int padding = 10;

	/**
	 * Standardni konstruktor komponenty progress baru
	 */
	public ProgressBarPanel() {
		timeThread = new TimeThread(this);
		timeThread.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		// smaze panel a nastav zakladni hodnoty
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.setFont(Utils.DEFAULT_APP_FONT);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
		
		// pokud neni nastaven nejaky aktualni program
		if (currentProgram == null) {
			Utils.drawAntialiasedStringInMiddle(Utils.STR_NO_PROGRAM, g, this);
		// pokud je nastaven aktualni program
		} else {
			// vytvoreni retezce s naformatovany casem a zjisti jejich velikost
			String startTimeStr = timeFormatter.format(currentProgram.getStartTime());
			String endTimeStr = timeFormatter.format(currentProgram.getEndTime());
			Rectangle2D startStrBounds = g.getFontMetrics().getStringBounds(startTimeStr, g);
			Rectangle2D endStrBounds = g.getFontMetrics().getStringBounds(endTimeStr, g);
			
			// zjisteni kolik casu zabira porad celkem a kolik jiz ubehlo 
			long totalTime = currentProgram.getLength() * 1000;
			long deltaTime = (new Date()).getTime() - currentProgram.getStartTime().getTime();
			// osetreni kdy porad jeste nezacal
			if (deltaTime < 0) deltaTime = 0;
			// osetretni kdy porad jiz skoncil
			if (deltaTime > totalTime) deltaTime = totalTime;
			
			// nastaveni graphics a vykresli pocatecni a koncovy cas
			Graphics2D g2d = (Graphics2D) g;
			Utils.enableAntialiasing(g2d);
			g2d.drawString(startTimeStr, padding, (int)startStrBounds.getHeight());
			g2d.drawString(endTimeStr, this.getWidth() - (int)endStrBounds.getWidth() - padding, (int)endStrBounds.getHeight());
			
			// vykresleni primky predstavujici celou dobu poradu
			g2d.setStroke(fullStroke);
			g2d.drawLine((int)(startStrBounds.getWidth() + 2 * padding), (int)(endStrBounds.getHeight() / 2), (int)(this.getWidth() - endStrBounds.getWidth() - 2 * padding), (int)(endStrBounds.getHeight() / 2));
			
			// vypocet kolik procent poradu jiz ubehlo a vykresleni odpovidajiciho markeru
			int totalPixels = (int)(this.getWidth() - endStrBounds.getWidth() - 2 * padding) - (int)(startStrBounds.getWidth() + 2 * padding);
			double deltaTimePerPixel = totalPixels / (double)totalTime;
			int currentPosition = (int)(deltaTime * deltaTimePerPixel);
			g2d.setStroke(thinStroke);
			g2d.drawLine((int)(startStrBounds.getWidth() + 2 * padding + currentPosition), 0, (int)(startStrBounds.getWidth() + 2 * padding + currentPosition), (int)(startStrBounds.getHeight() / 2));
		}
	}

	/**
	 * Nastavy vybrany porad pro zobrazeni
	 * @param program {@link Program} instance
	 */
	public void setProgram(Program program) {
		this.currentProgram = program;
	}
}

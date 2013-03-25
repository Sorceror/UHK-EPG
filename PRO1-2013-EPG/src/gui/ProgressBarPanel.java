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
	private Stroke fullStroke;
	private Stroke thinStroke;
	private final int strokeFullWidth = 6;
	private final int strokeThinWidth = 3;

	public ProgressBarPanel() {
		timeThread = new TimeThread(this, 30000);
		timeThread.start();
		fullStroke = new BasicStroke(strokeFullWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		thinStroke = new BasicStroke(strokeThinWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.setFont(Utils.DEFAULT_APP_FONT);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
		if (currentProgram == null) {
			Utils.drawAntialiasedStringInMiddle(Utils.STR_NO_PROGRAM, g, this);
		} else {
			String startTimeStr = timeFormatter.format(currentProgram.getStartTime());
			String endTimeStr = timeFormatter.format(currentProgram.getEndTime());
			Rectangle2D startStrBounds = g.getFontMetrics().getStringBounds(startTimeStr, g);
			Rectangle2D endStrBounds = g.getFontMetrics().getStringBounds(endTimeStr, g);
			
			long totalTime = currentProgram.getLength() * 1000;
			long deltaTime = (new Date()).getTime() - currentProgram.getStartTime().getTime();
			if (deltaTime < 0) deltaTime = 0;
			if (deltaTime > totalTime) deltaTime = totalTime;
			
			Graphics2D g2d = (Graphics2D) g;
			Utils.enableAntialiasing(g2d);
			g2d.drawString(startTimeStr, 0, (int)startStrBounds.getHeight());
			g2d.drawString(endTimeStr, this.getWidth() - (int)endStrBounds.getWidth(), (int)endStrBounds.getHeight());
			
			g2d.setStroke(fullStroke);
			g2d.drawLine((int)(startStrBounds.getWidth() + strokeFullWidth), (int)(startStrBounds.getHeight() / 2 + strokeFullWidth / 2), (int)(this.getWidth() - endStrBounds.getWidth() - strokeFullWidth), (int)(startStrBounds.getHeight() / 2 + strokeFullWidth / 2));
			
			int totalPixels = (int)(startStrBounds.getWidth() + strokeFullWidth) - (int)(this.getWidth() - endStrBounds.getWidth() - strokeFullWidth);
			double deltaTimePerPixel = totalPixels / (double)totalTime;
			int currentPosition = (int)(deltaTime * deltaTimePerPixel);
			g2d.setStroke(thinStroke);
			g2d.drawLine(currentPosition, (int)(startStrBounds.getHeight() / 2) - strokeThinWidth, currentPosition, (int)(startStrBounds.getHeight() / 2));
		}
	}

	/**
	 * Set selected program as current focused program
	 * @param program
	 */
	public void setProgram(Program program) {
		this.currentProgram = program;
	}
}

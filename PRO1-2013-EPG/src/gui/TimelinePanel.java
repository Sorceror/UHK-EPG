/**
 * 
 */
package gui;

import gui.utils.TimeThread;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;

import tvdata.Channel;
import tvdata.Program;

/**
 * Trida predstavuje panel na ktery je vykreslovana casova osa pro jednotlive kanaly
 * @author Pavel Janecka
 */
public class TimelinePanel extends JComponent {
	private final int fontHeight = Utils.DEFAULT_APP_FONT.getSize();
	private final int channelPadding = 4;
	private final int channelOutlineLine = 2;
	private final int lineHeight = fontHeight + channelPadding * 2 + channelOutlineLine;
	private final int minChannelNameWidth = 100;
	private final String shortenMark = "...";
	private int pixelPerHour = 150;
	private double pixelPerSecond = pixelPerHour / 3600d;
	private Font channelFont = Utils.DEFAULT_APP_FONT.deriveFont(Font.BOLD);
	private Color currentProgramBackground = new Color(230, 230, 230);
	private Color pastProgramBackground = new Color(245, 245, 245);
	private List<Channel> channels;
	
	private int offset = 0;
	
	public TimelinePanel() {
		TimeThread timeThread = new TimeThread(this, 10000);
		timeThread.start();

		offset = 0;
		initZoomConstants();
	}
	
	private void initZoomConstants() {
		pixelPerSecond = pixelPerHour / 3600d;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		int channelNameWidth = (int)(this.getWidth() * 0.1);
		channelNameWidth = channelNameWidth < minChannelNameWidth ? minChannelNameWidth : channelNameWidth;

		Graphics2D g2d = (Graphics2D) g;
		Utils.enableAntialiasing(g2d);
		
		Date now = new Date();
		Calendar timeMark = Calendar.getInstance();
		timeMark.setTime(now);
		timeMark.add(Calendar.MINUTE, -90);
		int markPixels = (int)(pixelPerSecond * Utils.getTimeInSeconds(timeMark.getTime()));
		
		int y = 0;
		int x = 0;
		for (Channel c : channels) {
			paintChannelTimeline(g2d, c, -markPixels + channelNameWidth + offset, y, now);
			paintChannelNames(g2d, c, channelNameWidth, x, y);
			y += lineHeight;
		}
	}

	private void paintChannelNames(Graphics2D g, Channel c, int channelNameWidth, int startX, int startY) {
		g.setFont(channelFont);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
		int fontCenterY = startY + lineHeight / 2 + fontHeight / 2;
		
		g.clearRect(startX, startY, channelNameWidth, lineHeight);
		g.drawString(c.getName(), startX + channelPadding * 2, fontCenterY);
		g.drawRect(startX, startY, channelNameWidth, lineHeight);

	}
	
	private void paintChannelTimeline(Graphics2D g, Channel c, int startX, int startY, Date now) {
		g.setFont(channelFont);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);

		g.setFont(Utils.DEFAULT_APP_FONT);
		int x = startX;
		int y = startY;
		int fontCenterY = startY + lineHeight / 2 + fontHeight / 2;
		
		int programWidth;
		for (Program p : c.getProgrammes()) {
			x = (int)((Utils.dateDifference(p.getStartTime(), Utils.getMidnight(now)).getTime() / 1000d) * pixelPerSecond) + startX;
			programWidth = (int)(p.getLength() * pixelPerSecond);
			drawProgram(g, x, y, fontCenterY, programWidth, p, now);
		}

	}

	private void drawProgram(Graphics2D g, int x, int y, int fontCenterY, int programWidth, Program p, Date now) {
		if (p.getStartTime().before(now)) {
			Color tmp = g.getColor();
			g.setColor(pastProgramBackground);
			g.fillRect(x, y, programWidth, lineHeight);
			g.setColor(tmp);
		}
		if (p.getStartTime().before(now) && p.getEndTime().after(now)) {
			Color tmp = g.getColor();
			g.setColor(currentProgramBackground);
			g.fillRect(x, y, programWidth, lineHeight);
			g.setColor(tmp);
		}
		g.drawString(shortenTitleByProgramWidth(p.getTitle(), g, programWidth), x + channelPadding, fontCenterY);
		g.drawRect(x, y, programWidth, lineHeight);
	}

	private String shortenTitleByProgramWidth(String title, Graphics g, int programWidth) {
		FontMetrics fm = g.getFontMetrics();
		int marksSize = fm.stringWidth(shortenMark);
		int maxWidth = programWidth - marksSize - channelPadding;
		StringBuilder titleBuilder = new StringBuilder(title);
		if (fm.stringWidth(titleBuilder.toString()) > maxWidth) {
			while (fm.stringWidth(titleBuilder.toString()) > maxWidth && titleBuilder.length() > 1) titleBuilder.deleteCharAt(titleBuilder.length() - 1);
			titleBuilder.append(shortenMark);
			if (fm.stringWidth(titleBuilder.toString()) > maxWidth + marksSize) return "";
			return titleBuilder.toString();
		}
		return title;
	}


	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}
	
	public void zoomIn() {
		pixelPerHour += 20;
		initZoomConstants();
	}
	
	public void zoomOut() {
		pixelPerHour -= 20;
		initZoomConstants();
	}

	public void moveTimeline(int delta) {
		offset += delta;
	}
}

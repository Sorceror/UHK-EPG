/**
 * 
 */
package gui;

import gui.utils.TimeThread;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseWheelEvent;
import java.text.SimpleDateFormat;
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
	private Font timelineFont = Utils.DEFAULT_APP_FONT;
	private Font channelFont = Utils.DEFAULT_APP_FONT.deriveFont(Font.BOLD);
	private Font currentTimeMarkFont = Utils.DEFAULT_APP_FONT;
	private int fontHeight = timelineFont.getSize();
	
	private final int channelPadding = 4;
	private final int channelOutlinePadding = 2;
	private final int lineHeight = fontHeight + channelPadding * 2 + channelOutlinePadding;
	private final int minChannelNameWidth = 100;
	private final String shortenMark = "...";
	private final int minPixelPerHour = 50;
	private final int maxPixelPerHour = 500;
	private final int defaultPixelPerHour = 150;
	
	private int pixelPerHour = defaultPixelPerHour;
	private double pixelPerSecond = pixelPerHour / 3600d;
	private int channelNameWidth = minChannelNameWidth;
	private Date startDay;
	private int startHour = 6;
	private int totalHours = 18;
	private int scrollOffset = 0;

	private Stroke timeMarksStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {1.0f}, 0.0f);
	private Stroke currentTimeMarkStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	private SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
	
	private Color currentProgramBackground = new Color(230, 230, 230);
	private Color currentProgramFontColor = Utils.DEFAULT_APP_FONT_COLOR;
	private Color pastProgramBackground = new Color(245, 245, 245);
	private Color pastProgramFontColor = Utils.DEFAULT_APP_FONT_COLOR;
	private Color futureProgramBackground = Color.WHITE;
	private Color futureProgramFontColor = Utils.DEFAULT_APP_FONT_COLOR;
	private Color channelNameBackground = Color.WHITE;
	private Color channelNameFontColor = Utils.DEFAULT_APP_FONT_COLOR;
	private Color selectedProgramBackground = new Color(233, 238, 240);
	private Color selectedProgramFontColor = new Color(0, 176, 240);
	
	private List<Channel> channels;
	private Program selectedProgram;
	
	/**
	 * Konstruktor timeline
	 */
	public TimelinePanel() {
		TimeThread timeThread = new TimeThread(this);
		timeThread.start();

		scrollOffset = 0;
		initZoomConstants();
		resetScrollOffset();
	}
	
	/**
	 * Nastavuje offset posunuti tak, aby zobrazovany cas predchazel o urcity pocet minut aktualni cas 
	 */
	private void resetScrollOffset() {
		Date now = new Date();
		Calendar timeMark = Calendar.getInstance();
		timeMark.setTime(now);
		timeMark.add(Calendar.MINUTE, -90);
		scrollOffset = -secondsToPixels(Utils.getTimeInSeconds(timeMark.getTime()));
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		channelNameWidth = (int)(this.getWidth() * 0.1);
		channelNameWidth = channelNameWidth < minChannelNameWidth ? minChannelNameWidth : channelNameWidth;

		Graphics2D g2d = (Graphics2D) g;
		Utils.enableAntialiasing(g2d);

		Date now = new Date();
		int y = lineHeight;
		int x = scrollOffset;
		
		for (Channel c : channels) {
			paintChannelTimeline(g2d, c, x, y, now);
			paintChannelNames(g2d, c, channelNameWidth, 0, y);
			y += lineHeight;
		}
		paintTimeMarks(g2d, x, 0, y + lineHeight / 2);
		paintCurrentTimeMark(g2d, lineHeight / 2, y + channelPadding, now);
	}

	/**
	 * Vykresli timeline pro vybrany kanal
	 * @param g {@link Graphics2D} instance
	 * @param c {@link Channel} kanal pro vykresleni
	 * @param startX int minimalni x souradnice
	 * @param startY int minimalni y souradnice
	 * @param now {@link Date} aktualni cas
	 */
	private void paintChannelTimeline(Graphics2D g, Channel c, int startX, int startY, Date now) {
		g.setFont(timelineFont);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);

		int x = startX;
		int y = startY;
		int fontCenterY = startY + lineHeight / 2 + fontHeight / 2;
		
		boolean haveSelected = false;
		for (Program p : c.getProgrammes()) {
			if (p == selectedProgram) haveSelected = true;
			paintProgram(g, p, x, y, fontCenterY, now);
		}
		// pokud kanal obsahuje aktualne vybrany program, mel by byt vykreslen nakonec
		// jinak budou jeho okraje prekresleny jinym programem a bude to vypadat spatne ;)
		if (haveSelected) paintProgram(g, selectedProgram, x, y, fontCenterY, now);
	}
	
	/**
	 * Spocita velikost programu a vykresli jej na platno
	 * @param g {@link Graphics2D} instance
	 * @param p {@link Program} program pro vykresleni
	 * @param startX int x souradnice zacatku kanalu
	 * @param startY int y souradnice zacatku kanalu
	 * @param fontCenterY int y souradnice stredu radku kanalu
	 * @param now {@link Date} aktualni cas
	 */
	private void paintProgram(Graphics2D g, Program p, int startX, int startY, int fontCenterY, Date now) {
		int x = secondsToPixels((Utils.dateDifference(p.getStartTime(), Utils.getMidnight(now)).getTime() / 1000d)) + startX;
		int programWidth = (int)(p.getLength() * pixelPerSecond);
		paintProgramOnCoors(g, x, startY, fontCenterY, programWidth, p, now);
	}
	
	/**
	 * Vykresli program na platno
	 * @param g {@link Graphics2D} instance
	 * @param startX int x souradnice zacatku kanalu
	 * @param startY int y souradnice zacatku kanalu
	 * @param fontCenterY int y souradnice stredu radku kanalu
	 * @param programWidth int sirka policka programu
	 * @param p {@link Program} program pro vykresleni
	 * @param now {@link Date} aktualni cas
	 */
	private void paintProgramOnCoors(Graphics2D g, int x, int y, int fontCenterY, int programWidth, Program p, Date now) {
		Color bgColor = futureProgramBackground;
		Color fontColor = futureProgramFontColor;
		if (p.getStartTime().before(now)) {
			bgColor = pastProgramBackground;
			fontColor = pastProgramFontColor;
		}
		if (p.getStartTime().before(now) && p.getEndTime().after(now)) {
			bgColor = currentProgramBackground;
			fontColor = currentProgramFontColor;
		}

		if (p == selectedProgram) {
			bgColor = selectedProgramBackground;
			fontColor = selectedProgramFontColor;
		}
		
		g.setColor(bgColor);
		g.fillRect(x, y, programWidth, lineHeight);
		g.setColor(fontColor);
		g.drawString(shortenTitleByProgramWidth(g, p.getTitle(), programWidth), x + channelPadding, fontCenterY);
		g.drawRect(x, y, programWidth, lineHeight);
	}
	
	/**
	 * Zkrati jmeno programu s pozadovanou koncovkou (suffix) dle velikosti policka programu
	 * @param g {@link Graphics2D} instance
	 * @param title String jmeno programu
	 * @param programWidth int sirka policka programu
	 * @return String zkracene jmeno programu
	 */
	private String shortenTitleByProgramWidth(Graphics g, String title, int programWidth) {
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
	
	/**
	 * Vykresli jmeno vybraneho kanalu
	 * @param g {@link Graphics2D} instance
	 * @param c {@link Channel} kanal pro vykresleni
	 * @param channelNameWidth int sirka policka jmena kanalu
	 * @param startX int minimalni x souradnice
	 * @param startY int minimalni y souradnice
	 */
	private void paintChannelNames(Graphics2D g, Channel c, int channelNameWidth, int startX, int startY) {
		g.setFont(channelFont);
		g.setColor(channelNameBackground);
		int fontCenterY = startY + lineHeight / 2 + fontHeight / 2;
		
		g.fillRect(startX, startY, channelNameWidth, lineHeight);
		g.setColor(channelNameFontColor);
		g.drawString(c.getName(), startX + channelPadding * 2, fontCenterY);
		g.drawRect(startX, startY, channelNameWidth, lineHeight);

	}
	
	/**
	 * Vykresli casove znacky odpovidajici hodinam
	 * @param g {@link Graphics2D} instance
	 * @param startX int x souradnice zacatku kanalu
	 * @param startY int y souradnice zacatku kanalu minus pozadovy offset
	 * @param endY int y vyska vsech kanalu plus pozadovany offset
	 */
	private void paintTimeMarks(Graphics2D g, int startX, int startY, int endY) {
		g.setFont(Utils.DEFAULT_APP_FONT);
		g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
		int fontCenterY = startY + lineHeight / 2 + fontHeight / 2;

		g.setStroke(timeMarksStroke);
		for (int i = startHour, x = startX + startHour * pixelPerHour; i <= (startHour + totalHours); i++, x += pixelPerHour) {
			if (x > channelNameWidth) {
				g.drawLine(x, startY + channelPadding, x, endY);
				g.drawString((i > 23 ? i - 24 : i) + ":00", x + channelPadding, fontCenterY);
			}
		}
	}
	
	/**
	 * Vykresli znacku aktualniho casu na platno
	 * @param g {@link Graphics2D} instance
	 * @param startY int y souradnice zacatku kanalu minus pozadovy offset
	 * @param endY int y vyska vsech kanalu plus pozadovany offset
	 * @param now {@link Date} aktualni cas
	 */
	private void paintCurrentTimeMark(Graphics2D g, int startY, int endY, Date now) {
		int x = secondsToPixels((Utils.dateDifference(now, startDay).getTime()) / 1000) + scrollOffset;
		if (x > channelNameWidth) {
			g.setFont(Utils.DEFAULT_APP_FONT);
			g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
			int fontCenterY = startY + lineHeight / 2 - fontHeight / 2;
	
			g.setStroke(currentTimeMarkStroke);
			g.drawLine(x, startY + channelPadding, x, endY);
			g.setFont(currentTimeMarkFont);
			String str = currentTimeFormat.format(now);
			g.drawString(str, x - (g.getFontMetrics().stringWidth(str) / 2), endY + fontCenterY);
		}
	}

	/**
	 * Prevede pocet x souradnici do mnozstvi casu v sekundach odpovidajiciho pixelu
	 * @param xCoor int souradnice
	 * @return long pocet sekund
	 */
	private long pixelsToSeconds(int xCoor) {
		return (long)(xCoor / pixelPerSecond);
	}
	
	/**
	 * Prevede pocet sekund na x souradnici odpovidajiciho casu
	 * @param seconds double pocet sekund
	 * @return int x souradnice
	 */
	private int secondsToPixels(double seconds) {
		return (int)(seconds * pixelPerSecond);
	}
	
	/**
	 * Predzpracuje kanaly a ziska z nich potrebna data
	 */
	private void preprocessChannels() {
		Date firstProgram = null;
		Date lastProgram = null;
		for (Channel c : channels) {
			for (Program p : c.getProgrammes()) {
				if (firstProgram == null) firstProgram = p.getStartTime();
				if (lastProgram == null) lastProgram = p.getEndTime();
				if (p.getStartTime().compareTo(firstProgram) < 0) firstProgram = p.getStartTime();
				if (p.getEndTime().compareTo(lastProgram) > 0) lastProgram = p.getEndTime();
			}
		}
		Calendar c = Calendar.getInstance();
		c.setTime(firstProgram);
		startDay = Utils.getMidnight(firstProgram);
		startHour = c.get(Calendar.HOUR_OF_DAY);
		totalHours = (int) Math.ceil((Utils.dateDifference(lastProgram, firstProgram).getTime() / 3600000d));
	}

	/**
	 * Nastavi kanaly pro vykresleni timeline
	 * @param channels {@link List} seznam {@link Channel} instanci
	 * @throws IllegalArgumentException pokud je seznam kanalu null
	 */
	public void setChannels(List<Channel> channels) {
		if (channels == null) throw new IllegalArgumentException("Channels cannot be null!");
		this.channels = channels;
		preprocessChannels();
	}
	
	/**
	 * Zazoomuje timeline dle udalosti mysi (prebira smer rotace kolecka a souradnice udalosti)
	 * @param e {@link MouseWheelEvent} instance
	 */
	public void zoom(MouseWheelEvent e) {
		zoom(e.getWheelRotation() < 0, e.getX());
	}
	
	/**
	 * Zazoomuje timeline nad danou souradnici 
	 * @param zoomIn boolean true pro zoom in, false pro zoom out
	 * @param x int x souradnice mista zoomu
	 */
	public void zoom(boolean zoomIn, int x) {
		long timeBeforeUnderMouse = pixelsToSeconds(x - scrollOffset);
		if (zoomIn) zoomIn();
		else zoomOut();
		scrollOffset = -secondsToPixels(timeBeforeUnderMouse) + x;
	}
	
	/**
	 * Provede zoomin
	 */
	private void zoomIn() {
		pixelPerHour += 20;
		pixelPerHour = Math.min(maxPixelPerHour, pixelPerHour);
		initZoomConstants();
	}
	
	/**
	 * Provede zoom out
	 */
	private void zoomOut() {
		pixelPerHour -= 20;
		pixelPerHour = Math.max(minPixelPerHour, pixelPerHour);
		initZoomConstants();
	}

	/**
	 * Resetuje zoom na standartni hodnotu
	 */
	private void zoomReset() {
		pixelPerHour = defaultPixelPerHour;
		initZoomConstants();
	}
	
	/**
	 * Upravuje konstanty dle aktualniho zoomu
	 */
	private void initZoomConstants() {
		pixelPerSecond = pixelPerHour / 3600d;
	}
	
	/**
	 * Posune timeline o dany pocet pixelu doprava (kladna hodnota), doleva (zaporna hodnota)
	 * @param delta
	 */
	public void moveTimeline(int delta) {
		scrollOffset += delta;
	}
	
	/**
	 * Vrati program pod danymi souradnicemi
	 * @param x int x souradnice
	 * @param y int y souradnice
	 * @return {@link Program} instance nebo null pokud pod danou souradnici zadny program neni
	 */
	public Program getProgramByCoordinates(int x, int y) {
		Program result = null;
		// first line (time marks)
		y -= lineHeight;
		int channelNum = y / lineHeight;
		int sec = (int) pixelsToSeconds(x - scrollOffset);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDay);
		cal.add(Calendar.SECOND, sec);
		Date time = cal.getTime();
		
		if (channelNum >= 0 && channelNum < channels.size()) {
			Channel c = channels.get(channelNum);
			for (Program p : c.getProgrammes()) {
				if (p.getStartTime().compareTo(time) <= 0 && p.getEndTime().compareTo(time) >= 0) {
					result = p;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * Restartuje posun timeline na zakladni hodnotu
	 */
	public void resetMoveOfTimeline() {
		resetScrollOffset();
	}
	
	/**
	 * Restartuje zoom na zakladni hodnotu
	 */
	public void resetZoom() {
		zoomReset();
	}
	
	/**
	 * Nastavuje dany porad na aktualne vybrany.
	 * Parametr null rusi vyber
	 * @param selectedProgram {@link Program} instance nebo null
	 */
	public void setSelectedProgram(Program selectedProgram) {
		this.selectedProgram = selectedProgram;
	}

	/**
	 * Nastavuje font pouzivany pro vykresleni nazvu poradu
	 * @param timelineFont {@link Font} instance
	 */
	public void setTimelineFont(Font timelineFont) {
		this.timelineFont = timelineFont;
		fontHeight = Math.max(timelineFont.getSize(), channelFont.getSize());
	}

	/**
	 * Nastavuje font pouzivany pro vykresleni nazvu kanalu
	 * @param channelFont {@link Font} instance
	 */
	public void setChannelFont(Font channelFont) {
		this.channelFont = channelFont;
		fontHeight = Math.max(timelineFont.getSize(), channelFont.getSize());
	}

	/**
	 * Nastavuje font pouzivany pro vykresleni aktualni hodnoty casu
	 * @param currentTimeMarkFont {@link Font} instance
	 */
	public void setCurrentTimeMarkFont(Font currentTimeMarkFont) {
		this.currentTimeMarkFont = currentTimeMarkFont;
	}

	/**
	 * Nastavuje stetec (stroke) pro linky s hodinovymi znackami
	 * @param timeMarksStroke {@link Stroke} instance
	 * @see BasicStroke
	 */
	public void setTimeMarksStroke(Stroke timeMarksStroke) {
		this.timeMarksStroke = timeMarksStroke;
	}

	/**
	 * Nastavuje stetec (stroke) pro linku s aktualnim casem
	 * @param currentTimeMarkStroke {@link Stroke} instance
	 * @see BasicStroke
	 */
	public void setCurrentTimeMarkStroke(Stroke currentTimeMarkStroke) {
		this.currentTimeMarkStroke = currentTimeMarkStroke;
	}
	
	/**
	 * Nastavuje vzor (pattern) formatu casovych udaju pro timeline (standardni pattern <code>"HH:mm"</code>)
	 * @param timePattern
	 * @see SimpleDateFormat
	 */
	public void setTimeFormatPattern(String timePattern) {
		currentTimeFormat = new SimpleDateFormat(timePattern);
	}

	/**
	 * Nastavuje barvu pozadi pro program bezici v aktualnim case
	 * @param currentProgramBackground {@link Color} instance
	 */
	public void setCurrentProgramBackground(Color currentProgramBackground) {
		this.currentProgramBackground = currentProgramBackground;
	}

	/**
	 * Nastavuje barvu fontu a ramecku pro program bezici v aktualnim case
	 * @param currentProgramFontColor {@link Color} instance
	 */
	public void setCurrentProgramFontColor(Color currentProgramFontColor) {
		this.currentProgramFontColor = currentProgramFontColor;
	}

	/**
	 * Nastavuje barvu pozadi pro program vysilany pozdeji nez je aktualni cas
	 * @param futureProgramBackground {@link Color} instance
	 */
	public void setFutureProgramBackground(Color futureProgramBackground) {
		this.futureProgramBackground = futureProgramBackground;
	}

	/**
	 * Nastavuje barvu fontu a ramecku pro program vysilany pozdeji nez je aktualni cas
	 * @param futureProgramFontColor {@link Color} instance
	 */
	public void setFutureProgramFontColor(Color futureProgramFontColor) {
		this.futureProgramFontColor = futureProgramFontColor;
	}

	/**
	 * Nastavuje barvu pozadi pro program vysilany drive nez je aktualni cas
	 * @param pastProgramBackground {@link Color} instance
	 */
	public void setPastProgramBackground(Color pastProgramBackground) {
		this.pastProgramBackground = pastProgramBackground;
	}

	/**
	 * Nastavuje barvu fontu a ramecku pro program vysilany pozdeji nez je aktualni cas
	 * @param pastProgramFontColor the pastProgramFontColor to set
	 */
	public void setPastProgramFontColor(Color pastProgramFontColor) {
		this.pastProgramFontColor = pastProgramFontColor;
	}

	/**
	 * Nastavuje barvu pozadi pro jmeno kanalu
	 * @param channelNameBackground {@link Color} instance
	 */
	public void setChannelNameBackground(Color channelNameBackground) {
		this.channelNameBackground = channelNameBackground;
	}

	/**
	 * Nastavuje barvu fontu a ramecku pro jmeno kanalu
	 * @param channelNameFontColor {@link Color} instance
	 */
	public void setChannelNameFontColor(Color channelNameFontColor) {
		this.channelNameFontColor = channelNameFontColor;
	}
	
	
}

/**
 * 
 */
package datasources;

import gui.Utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tvdata.Channel;
import tvdata.Program;
import datasources.exception.DataSourceException;

/**
 * Trida predstavuje zdroj dat televizniho programu pro ceskou televizi
 * @author Pavel Janecka
 */
public class CeskaTelevizeProgrammeSource {
	private final Map<String, String> sources = new HashMap<String, String>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	public CeskaTelevizeProgrammeSource() {
		sources.put("ct1", "shedule.ct1.xml");
		sources.put("ct2", "shedule.ct2.xml");
		sources.put("ct4", "shedule.ct4.xml");
		sources.put("ct24", "shedule.ct24.xml");
	}
	
	public List<Channel> loadChannels() throws DataSourceException {
		List<Channel> loadedChannels = new ArrayList<Channel>();
		
		for (String source : sources.keySet()) {
			File sourceFile = new File(sources.get(source));
			loadedChannels.add(loadChannelFromXmlFile(sourceFile));
		}
		
		return loadedChannels;
	}

	private Channel loadChannelFromXmlFile(File sourceFile) throws DataSourceException {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(sourceFile);
			document.getDocumentElement().normalize();
			
			Date day = dateFormat.parse(document.getDocumentElement().getAttribute("datum_vysilani"));
			// FIXME hacky! REMOVE
			day = new Date(1364166000000l);
			Calendar tommorow = Calendar.getInstance();
			tommorow.setTime(day);
			tommorow.add(Calendar.DATE, 1);
			Date tommorowMidnight = Utils.getMidnight(tommorow.getTime());
			
			List<Program> schedule = new ArrayList<Program>();
			NodeList tmpProgrammes = document.getElementsByTagName("porad");
			Node rootElement = document.getDocumentElement();
			
			Element element;
			for (int i = 0; i < tmpProgrammes.getLength(); i++) {
				if (tmpProgrammes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					element = (Element) tmpProgrammes.item(i);
					
					String title = parseProgramName(element);
					Date startTime = parseProgramStartTime(day, element);
					int length = parseProgramLength(element);
					Calendar calEnd = Calendar.getInstance();
					calEnd.setTime(startTime);
					calEnd.add(Calendar.SECOND, length);
					Date endTime = calEnd.getTime();
					String description = parseProgramDescription(element);
					Image img = parseProgramImage(element);

					// corrections
					// after midnight switch to next day
					if (startTime.before(tommorowMidnight) && endTime.after(tommorowMidnight)) {
						day = Utils.getTomorrow(day);
					}
					// program started at midnight exactly
					else if (Utils.getTomorrow(startTime).equals(tommorowMidnight)) {
						day = Utils.getTomorrow(day);
						startTime = Utils.getTomorrow(startTime);
					}
					// start of current is end of last
					if (schedule.size() > 0) {
						Program last = schedule.get(schedule.size() - 1);
						last.setEndTime(startTime);
					}
					
					Program p = new Program(title, startTime, endTime, description);
					if (img != null) p.setImage(img);
					schedule.add(p);
				}
			}
			return new Channel(parseChannelName(rootElement), day, tommorow.getTime(), schedule);
		} catch (Exception e) {
			throw new DataSourceException("Datasource cannot be parsed", e);
		}
	}

	private String parseChannelName(Node root) {
		String name = "";
		
		if (root.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) root;
			name = element.getAttribute("kanal");
		}
		
		return name;
	}

	private String parseProgramName(Element program) {
		String name = "";
		Node names = program.getElementsByTagName("nazvy").item(0);
		if (names.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) names;
			name = element.getElementsByTagName("nazev").item(0).getTextContent();
		}
		return name;
	}

	private Date parseProgramStartTime(Date day, Element program) throws Exception {
		Date time = timeFormat.parse(program.getElementsByTagName("cas").item(0).getTextContent());
		Long totalMillis = day.getTime() + time.getTime() + TimeZone.getDefault().getOffset(time.getTime());
		return new Date(totalMillis);
	}
	
	private int parseProgramLength(Element program) throws DataSourceException {
		String time = program.getElementsByTagName("stopaz").item(0).getTextContent();
		String[] times = time.split(":");
		if (times.length != 2) throw new DataSourceException("Cannot parse element 'stopaz' (" + time + "), inapropriate format (expected mm:ss)");
		Integer minutes = Integer.parseInt(times[0]);
		Integer seconds = Integer.parseInt(times[1]);
		return minutes * 60 + seconds;
	}
	
	private String parseProgramDescription(Element program) {
		return program.getElementsByTagName("noticka").item(0).getTextContent();
	}
	
	private Image parseProgramImage(Element program) {
		String url = program.getElementsByTagName("tv_program").item(0).getTextContent();
		if (url != null && !url.isEmpty()) {
			try {
				return ImageIO.read(new URL(url));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

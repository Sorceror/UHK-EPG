/**
 * 
 */
package datasources;

import gui.Utils;

import java.io.File;
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
	
	/**
	 * Konstruktor datoveho zdroje pro program ceske televize
	 */
	public CeskaTelevizeProgrammeSource() {
		sources.put("ct1", "shedule.ct1.xml");
		sources.put("ct2", "shedule.ct2.xml");
		sources.put("ct4", "shedule.ct4.xml");
		sources.put("ct24", "shedule.ct24.xml");
	}
	
	/**
	 * Nahraje vsechny programy registrovanych kanalu
	 * @return {@link List} seznam kanalu
	 * @throws DataSourceException pokud dojde pri cteni ze zdroje k nejake chybe
	 */
	public List<Channel> loadChannels() throws DataSourceException {
		List<Channel> loadedChannels = new ArrayList<Channel>();
		
		// projde vsechny registrovane kanaly (ulozene v mape kanalu) a nacte jejich data
		for (String source : sources.keySet()) {
			File sourceFile = new File(sources.get(source));
			loadedChannels.add(loadChannelFromXmlFile(sourceFile));
		}
		
		return loadedChannels;
	}

	/**
	 * Nacte informace o kanalu (programy) z datoveho zdroje
	 * @param sourceFile {@link File} soubor se zdrojem dat
	 * @return {@link Channel} instance
	 * @throws DataSourceException pokud dojde pri cteni ze zdroje k nejake chybe
	 */
	private Channel loadChannelFromXmlFile(File sourceFile) throws DataSourceException {
		try {
			// priprava pro cteni z XML
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(sourceFile);
			document.getDocumentElement().normalize();
			
			// ziskani data ke kteremu byl zdroj vytvoren
			Date day = dateFormat.parse(document.getDocumentElement().getAttribute("datum_vysilani"));
			// FIXME hacky! REMOVE
			day = new Date(1364767200000L);
			Calendar tommorow = Calendar.getInstance();
			tommorow.setTime(day);
			tommorow.add(Calendar.DATE, 1);
			Date tommorowMidnight = Utils.getMidnight(tommorow.getTime());
			
			// pomocne datove struktury
			List<Program> schedule = new ArrayList<Program>();
			NodeList tmpProgrammes = document.getElementsByTagName("porad");
			Node rootElement = document.getDocumentElement();
			
			// prochazeni jednotlivych poradu ve zdroji a parsovani jejich dat
			Element element;
			for (int i = 0; i < tmpProgrammes.getLength(); i++) {
				if (tmpProgrammes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					element = (Element) tmpProgrammes.item(i);
					
					// parsovani dat zdroje
					String title = parseProgramName(element);
					Date startTime = parseProgramStartTime(day, element);
					int length = parseProgramLength(element);
					String description = parseProgramDescription(element);
					URL url = parseProgramImage(element);

					// dodatecne vypocty
					Calendar calEnd = Calendar.getInstance();
					calEnd.setTime(startTime);
					calEnd.add(Calendar.SECOND, length);
					Date endTime = calEnd.getTime();

					// korekce
					// po pulnoci preskoc na dalsi den
					if (startTime.before(tommorowMidnight) && endTime.after(tommorowMidnight)) {
						day = Utils.getTomorrow(day);
					}
					// vysilani programu zacalo presne o pulnoci
					else if (Utils.getTomorrow(startTime).equals(tommorowMidnight)) {
						day = Utils.getTomorrow(day);
						startTime = Utils.getTomorrow(startTime);
					}
					// casy jednotlivych poradu presne nenavazuji (nejspis kvuli reklamam)
					// odstraneni volnych casovych useku nastavenim konce posledniho poradu
					// na zacatek aktualniho
					if (schedule.size() > 0) {
						Program last = schedule.get(schedule.size() - 1);
						last.setEndTime(startTime);
					}
					
					// samotne vytvoreni instance poradu a pridani do seznamu
					Program p = new Program(title, startTime, endTime, description);
					if (url != null) p.setImageURL(url);
					schedule.add(p);
				}
			}
			return new Channel(parseChannelName(rootElement), day, schedule);
		} catch (Exception e) {
			throw new DataSourceException("Datasource cannot be parsed", e);
		}
	}

	
	/**
	 * Parsuje nazev aktualniho poradu
	 * @param program {@link Element} prvek XML
	 * @return String jmeno poradu
	 */
	private String parseProgramName(Element program) {
		String name = "";
		Node names = program.getElementsByTagName("nazvy").item(0);
		if (names.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) names;
			name = element.getElementsByTagName("nazev").item(0).getTextContent();
		}
		return name;
	}

	/**
	 * Parsuje zacatek aktualniho poradu
	 * @param day {@link Date} instance - den ke kteremu se ma svazat cas zacatku poradu
	 * @param program {@link Element} prvek XML
	 * @return {@link Date} instance - datum a cas zacatku poradu
	 * @throws Exception pokud dojde pri parsovani k chybe
	 */
	private Date parseProgramStartTime(Date day, Element program) throws Exception {
		Date time = timeFormat.parse(program.getElementsByTagName("cas").item(0).getTextContent());
		Long totalMillis = day.getTime() + time.getTime() + TimeZone.getDefault().getOffset(time.getTime());
		return new Date(totalMillis);
	}
	
	/**
	 * Parsuje delku trvani poradu
	 * @param program {@link Element} prvek XML
	 * @return int delka poradu v sekundach
	 * @throws DataSourceException pokud dojde pri parsovani k chybe
	 */
	private int parseProgramLength(Element program) throws DataSourceException {
		String time = program.getElementsByTagName("stopaz").item(0).getTextContent();
		String[] times = time.split(":");
		if (times.length != 2) throw new DataSourceException("Cannot parse element 'stopaz' (" + time + "), inapropriate format (expected mm:ss)");
		Integer minutes = Integer.parseInt(times[0]);
		Integer seconds = Integer.parseInt(times[1]);
		return minutes * 60 + seconds;
	}
	
	/**
	 * Parsuje popisek poradu
	 * @param program {@link Element} prvek XML
	 * @return String popisek poradu
	 */
	private String parseProgramDescription(Element program) {
		return program.getElementsByTagName("noticka").item(0).getTextContent();
	}
	
	/**
	 * Parsuje URL k obrazku poradu
	 * @param program {@link Element} prvek XML
	 * @return {@link URL} instance nebo null pokud porad nema nastaveny obrazek
	 */
	private URL parseProgramImage(Element program) {
		URL url = null;
		String strURL = program.getElementsByTagName("tv_program").item(0).getTextContent();
		try {
			if(!strURL.isEmpty()) url = new URL(strURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * Parsuje jmeno kanalu z datoveho zdroje
	 * @param root {@link Node} element
	 * @return String nazev
	 */
	private String parseChannelName(Node root) {
		String name = "";
		
		if (root.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) root;
			name = element.getAttribute("kanal");
		}
		
		return name;
	}
}

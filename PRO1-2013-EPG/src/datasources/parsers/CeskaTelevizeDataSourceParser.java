/**
 * 
 */
package datasources.parsers;

import gui.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tvdata.Channel;
import tvdata.Program;
import tvdata.ProgramInfo;
import tvdata.enums.SoundType;
import datasources.exception.DataSourceException;

/**
 * Trida predstavuje zdroj dat televizniho programu pro ceskou televizi
 * @author Pavel Janecka
 */
public class CeskaTelevizeDataSourceParser implements DataSourceParser {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	@Override
	public Channel loadChannelFromXmlFile(File sourceFile) throws DataSourceException {
		try {
			// priprava pro cteni z XML
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(sourceFile);
			document.getDocumentElement().normalize();
			
			// ziskani data ke kteremu byl zdroj vytvoren
			Date day = dateFormat.parse(document.getDocumentElement().getAttribute("datum_vysilani"));
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
					ProgramInfo programInfo = parseAditionalInfo(element);

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
					if (programInfo != null) p.setProgramInfo(programInfo);
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
	
	/**
	 * Parsuje dodatecne info k programu (muze obsahovat null hodnoty)
	 * @param program {@link Element} prvek XML
	 * @return {@link ProgramInfo} instance
	 */
	private ProgramInfo parseAditionalInfo(Element program) {
		ProgramInfo programInfo = new ProgramInfo();
		
		String showName = program.getElementsByTagName("nadtitul").item(0).getTextContent();
		if (showName != null && !showName.isEmpty()) programInfo.setShowName(showName);
		String originalName = program.getElementsByTagName("original").item(0).getTextContent();
		if (originalName != null && !originalName.isEmpty()) programInfo.setOriginalName(originalName);
		String episodeName = program.getElementsByTagName("nazev_casti").item(0).getTextContent();
		if (episodeName != null && !episodeName.isEmpty()) programInfo.setOriginalName(originalName);
		String genre = program.getElementsByTagName("zanr").item(0).getTextContent();
		if (genre != null && !genre.isEmpty()) programInfo.setGenre(genre);
		String aspectRatio = program.getElementsByTagName("pomer").item(0).getTextContent();
		if (aspectRatio != null && !aspectRatio.isEmpty()) programInfo.setPictureAspectRatio(aspectRatio);
		
		String part = program.getElementsByTagName("dil").item(0).getTextContent();
		if (part != null && !part.isEmpty()) {
			String[] strs = part.split("/");
			if (strs.length == 2) {
				try {
					Integer partNum = Integer.parseInt(strs[0]);
					Integer totalNum = Integer.parseInt(strs[1]);
					programInfo.setEpisodeCount(partNum);
					programInfo.setEpisodesInSeason(totalNum);
				} catch (NumberFormatException e) { /* nic se nedeje */ }
			}
		}
		
		String programURL = program.getElementsByTagName("program").item(0).getTextContent();
		if (programURL != null && !programURL.isEmpty()) {
			try {
				programInfo.setProgramURL(new URL(programURL));
			} catch (MalformedURLException e) { /* nic se nedeje */ }
		}
		String iVisilaniURL = program.getElementsByTagName("ivysilani").item(0).getTextContent();
		if (iVisilaniURL != null && !iVisilaniURL.isEmpty()) {
			try {
				programInfo.setStreamPageURL(new URL(iVisilaniURL));
			} catch (MalformedURLException e) { /* nic se nedeje */ }
		}
		
		String soundType = program.getElementsByTagName("zvuk").item(0).getTextContent();
		if (soundType != null && !soundType.isEmpty()) programInfo.setSoundType(SoundType.fromString(soundType));
		
		String hiddenSubtitles = program.getElementsByTagName("skryte_titulky").item(0).getTextContent();
		programInfo.setHiddenSubtitles(booleanFromString(hiddenSubtitles));
		String forDeaf = program.getElementsByTagName("neslysici").item(0).getTextContent();
		programInfo.setCommentsForDeafPeople(booleanFromString(forDeaf));
		String live = program.getElementsByTagName("live").item(0).getTextContent();
		programInfo.setLiveShow(booleanFromString(live));
		String premiere = program.getElementsByTagName("premiera").item(0).getTextContent();
		programInfo.setPremiere(booleanFromString(premiere));
		String bw = program.getElementsByTagName("cb").item(0).getTextContent();
		programInfo.setBlackAndWhiteOnly(booleanFromString(bw));
		String notForChildren = program.getElementsByTagName("hvezdicka").item(0).getTextContent();
		programInfo.setNotForChildren(booleanFromString(notForChildren));
		String originalDubbing = program.getElementsByTagName("puvodni_zneni").item(0).getTextContent();
		programInfo.setOriginalDubbing(booleanFromString(originalDubbing));
		
		return programInfo;
	}
	
	/**
	 * Converts string boolean representation (0 - false, 1 - true) to {@link Boolean} instance
	 * @param str String
	 * @return {@link Boolean} instance or null (for other values that 0/1)
	 */
	private Boolean booleanFromString(String str) {
		if (str != null && !str.isEmpty()) {
			if (str.equals("1")) return Boolean.TRUE;
			if (str.equals("0")) return Boolean.FALSE;
		}
		return null;
	}
}

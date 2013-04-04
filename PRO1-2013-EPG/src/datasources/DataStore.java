/**
 * 
 */
package datasources;

import gui.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tvdata.Channel;
import datasources.parsers.DataSourceParser;
import datasources.providers.OnlineDataSourceProvider;

/**
 * Datovy sklad pro data o televiznich kanalech a programech. 
 * Uklada data na disk a pokud nejsou k dispozici vyhledava je v online datovych zdrojich 
 * @author Pavel Janecka
 */
public class DataStore {
	private String pathToData;
	private Map<String, OnlineDataSourceProvider> providers = new HashMap<String, OnlineDataSourceProvider>();
	private Map<String, DataSourceParser> parsers = new HashMap<String, DataSourceParser>();
	private Map<String, Channel> internalCache = new HashMap<String, Channel>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Konstruktor datoveho skladu, vytvari adresarovou strukturu pro data pokud neexistuje
	 */
	public DataStore() {
		this(Utils.DATA_FOLDER_PATH);
	}
	
	/**
	 * Konstruktor datoveho skladu na zadane ceste, vytvari adresarovou strukturu pro data pokud neexistuje
	 * @param path String cesta k datovemu skladu (slozka)
	 */
	public DataStore(String path) {
		pathToData = path;
		createFolderIfNotExist(path);
	}
	
	/**
	 * Registruje kanal do datoveho skladu
	 * @param channelName String jmeno kanalu
	 * @param onlineProvider {@link OnlineDataSourceProvider} instance online datoveho zdroje
	 * @param sourceParser {@link DataSourceParser} instance parseru dat
	 */
	public void registerChannelAndSource(String channelName, OnlineDataSourceProvider onlineProvider, DataSourceParser sourceParser) {
		providers.put(channelName, onlineProvider);
		parsers.put(channelName, sourceParser);
		createFolderIfNotExist(pathToData + "/" + channelName);
	}
	
	/**
	 * Vytvari slozky na zadane ceste, pokud neexistuji
	 * @param path String cesta
	 */
	private void createFolderIfNotExist(String path) {
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
	}
	
	/**
	 * Pokusi se ziskat data z datoveho skladu a pokud nejsou pritomny, stahne je z datoveho zdroje.
	 * Po ziskani dat jsou data parsovany pozadovanym parserem kanalu.
	 * @param key String jmeno kanalu
	 * @param date {@link Date} den pro ktery maji byt ziskany data ze zdroje
	 * @return {@link Channel}
	 * @throws Exception pokud dojde k chybe pri ziskavani nebo zpracovani dat 
	 */
	public Channel getDataForChannel(String key, Date date) throws Exception {
		Channel channel = null;
		if (internalCache.containsKey(generateInternalCacheKey(key, date))) channel = internalCache.get(generateInternalCacheKey(key, date));
		else {
			File data = loadData(key, date);
			channel = parseData(parsers.get(key), data);
			internalCache.put(generateInternalCacheKey(key, date), channel);
		}
		return channel;
	}
	
	/**
	 * Generuje klic do interni cache naparsovanych kanalu
	 * @param key String jmeno kanalu
	 * @param date {@link Date} den pro ktery maji byt ziskany data ze zdroje
	 * @return String klic
	 */
	private String generateInternalCacheKey(String key, Date date) {
		return key + dateFormat.format(date); 
	}

	/**
	 * Ziska data z datoveho skladu nebo online zdroje podle potreby
	 * @param key String jmeno kanalu
	 * @param date {@link Date} den pro ktery maji byt ziskany data ze zdroje
	 * @return {@link File} instance ulozeneho souboru
	 * @throws Exception pokud dojde k chybe pri ziskavani dat ze zdroje
	 */
	private File loadData(String key, Date date) throws Exception {
		String filePath = pathToData + "/" + key + "/" + dateFormat.format(date) + ".xml";
		File dataFile = new File(filePath);
		if (!dataFile.isFile()) {
			OnlineDataSourceProvider provider = providers.get(key);
			if (provider == null) throw new IllegalArgumentException("Provider for selected channel not registered!");
			dataFile = copyDataFromSource(provider.obtainData(key, date), filePath);
		}
		return dataFile;
	}

	/**
	 * Kopiruje data z datoveho zdroje do datoveho skladu
	 * @param url {@link URL} instance odkaz na online data
	 * @param fileName String cesta kam ma byt soubor ulozen
	 * @return {@link File} instance ulozeneho souboru
	 * @throws Exception pokud dojde pri ziskavani dat k chybe
	 */
	private File copyDataFromSource(URL url, String fileName) throws Exception {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(url.openStream());
			
			File file = new File(fileName);
			if (!file.exists()) file.createNewFile();
			fout = new FileOutputStream(file);

			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} catch (Exception e) {
			throw new IOException("Cannot save data to data store!", e);
		} finally {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		}
		return new File(fileName);
	}
	
	/**
	 * Parsuje data z datoveho souboru
	 * @param parser {@link DataSourceParser} instance parseru
	 * @param data {@link File} instance datoveho souboru
	 * @return {@link Channel} kanal s daty
	 * @throws Exception pokud dojde k chybe pri parsovani souboru
	 */
	private Channel parseData(DataSourceParser parser, File data) throws Exception {
		if (parser == null) throw new IllegalArgumentException("Parser for selected channel not registered!");
		return parser.loadChannelFromXmlFile(data);
	}

}

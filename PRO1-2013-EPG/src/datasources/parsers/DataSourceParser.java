/**
 * 
 */
package datasources.parsers;

import java.io.File;

import tvdata.Channel;

/**
 * Rozhrani pro parser dat kanalu ziskanych z online datovych zdroju
 * @author Pavel Janecka
 */
public interface DataSourceParser {

	/**
	 * Parsuje XML data z uvedeneho souboru
	 * @param sourceFile {@link File} instance na soubor s daty
	 * @return {@link Channel} kanal s daty
	 * @throws Exception pokud dojde k chybe pri parsovani
	 */
	public Channel loadChannelFromXmlFile(File sourceFile) throws Exception;
}

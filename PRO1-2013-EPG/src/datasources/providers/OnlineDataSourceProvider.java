/**
 * 
 */
package datasources.providers;

import java.net.URL;
import java.util.Date;

/**
 * Rozhrani pro online datovy zdroj dat vybraneho kanalu
 * @author Pavel Janecka
 */
public interface OnlineDataSourceProvider {
	
	/**
	 * Vraci url online datoveho zdroje pro vybrany kanal
	 * @param key String jmeno vybraneho kanalu
	 * @param date {@link Date} instance - den pro ktery maji byt ziskany data ze zdroje
	 * @return {@link URL} instance jako odkaz na online datovy zdroj
	 * @throws Exception pokud dojde pri zpracovani kanalu k chybe
	 */
	public URL obtainData(String key, Date date) throws Exception;
}

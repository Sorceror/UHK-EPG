/**
 * 
 */
package datasources.providers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Online zdroj dat pro ceskou televizi
 * @author Pavel Janecka
 */
public class CeskaTelevizeOnlineDataSourceProvider implements OnlineDataSourceProvider {
//	private final String baseURL = "http://www.ceskatelevize.cz/services/programme/xml/schedule.php?user=test";
	private final String baseURL = "http://edu.uhk.cz/~krizpa1/pro1/xml/ct/schedule.php?user=test";
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Override
	public URL obtainData(String key, Date date) throws Exception {
		String url = baseURL + "&date=" + dateFormat.format(date) + "&channel=" + key;
		return new URL(url);
	}
	
}

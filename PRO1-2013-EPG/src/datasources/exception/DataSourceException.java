/**
 * 
 */
package datasources.exception;

/**
 * Vlastni vyjimka pro chyby pri parsovani dat z datovych zdroju
 * @author Pavel Janecka
 */
public class DataSourceException extends Exception {

	/**
	 * Konstruktor
	 * @param message chybova hlaska
	 */
	public DataSourceException(String message) {
		super(message);
	}

	/**
	 * Konstruktor
	 * @param message chybova hlaska
	 * @param cause {@link Throwable} instance duvod chyby
	 */
	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

}

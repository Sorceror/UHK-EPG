/**
 * 
 */
package datasources.exception;

/**
 * Custom exception when data source cannot be loaded
 * @author Pavel Janecka
 */
public class DataSourceException extends Exception {

	/**
	 * @param message
	 */
	public DataSourceException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

}

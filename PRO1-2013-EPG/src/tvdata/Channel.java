/**
 * 
 */
package tvdata;

import java.util.Date;
import java.util.List;

/**
 * Trida predstavuje televizni kanal, ktery obsahuje seznam televiznich poradu v danem casovem okamziku
 * @author Pavel Janecka
 */
public class Channel implements Comparable<Channel> {
	private String name;
	private Date fromTime;
	private Date toTime;
	private List<Program> programmes;
	
	public Channel(String name, Date fromTime, Date toTime,	List<Program> programmes) {
		this.name = name;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.programmes = programmes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the fromTime
	 */
	public Date getFromTime() {
		return fromTime;
	}

	/**
	 * @return the toTime
	 */
	public Date getToTime() {
		return toTime;
	}

	/**
	 * @return the programmes
	 */
	public List<Program> getProgrammes() {
		return programmes;
	}

	@Override
	public int compareTo(Channel o) {
		return this.name.compareTo(o.name);
	}
	
}

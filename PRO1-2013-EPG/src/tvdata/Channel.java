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
	private List<Program> programmes;
	
	/**
	 * Konstruktor dat televizniho kanalu
	 * @param name String jmeno kanalu
	 * @param fromTime {@link Date} den pro ktery kanal obsahuje data
	 * @param programmes {@link List} seznam {@link Program} instanci pro dany kanal na dany den
	 */
	public Channel(String name, Date fromTime, List<Program> programmes) {
		this.name = name;
		this.fromTime = fromTime;
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
	 * @return the programmes
	 */
	public List<Program> getProgrammes() {
		return programmes;
	}

	@Override
	public int compareTo(Channel o) {
		return this.name.compareTo(o.name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Channel [name=" + name + ", fromTime=" + fromTime
				+ ", programmes=" + programmes + "]";
	}

}

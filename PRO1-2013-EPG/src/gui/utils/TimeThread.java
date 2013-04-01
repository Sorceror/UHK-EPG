/**
 * 
 */
package gui.utils;

import javax.swing.JComponent;

/**
 * Pomocna trida predstavujici vlakno, ktere po zadanem case prekresli vybranou komponentu
 * @author Pavel Janecka
 */
public class TimeThread extends Thread {
	private JComponent component;
	private long timeToRepaint = 1000;
	
	/**
	 * Kontruktor prekreslovaciho vlakna (cas prekresleni 1s)
	 * @param component {@link JComponent} komponenta ktera se ma prekreslovat
	 */
	public TimeThread(JComponent component) {
		this.component = component;
	}
	
	/**
	 * Konstruktor vykreslovaciho vlakna s vybranym casem
	 * @param component {@link JComponent} komponenta ktera se ma prekreslovat
	 * @param timeToRepaint long doba za kterou se ma komponenta prekreslit v milisekundach
	 */
	public TimeThread(JComponent component, long timeToRepaint) {
		this(component);
		this.timeToRepaint = timeToRepaint;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(timeToRepaint);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			component.repaint();
		}
	}
	
}

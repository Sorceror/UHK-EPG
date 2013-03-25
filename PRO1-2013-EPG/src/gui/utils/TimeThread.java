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
	
	public TimeThread(JComponent component) {
		this.component = component;
	}
	
	public TimeThread(JComponent component, long timeToRepaint) {
		this(component);
		this.timeToRepaint = timeToRepaint;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(timeToRepaint);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		component.repaint();
	}
	
}

/**
 * 
 */
package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import tvdata.Program;

/**
 * Panel pro vykresloveni obrazku aktualne vybraneho programu
 * @author Pavel Janecka
 */
public class ProgramIconPanel extends JComponent {
	private Program currentProgram;
	private final String STR_NO_IMAGE = "No image";
	
	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		if (currentProgram != null && currentProgram.getImage() != null) {
			g.drawImage(currentProgram.getImage(), 0, 0, null);
		} else {
			String str = Utils.STR_NO_PROGRAM;
			if (currentProgram != null) str = STR_NO_IMAGE;

			g.setFont(Utils.DEFAULT_APP_FONT);
			g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
			Utils.drawAntialiasedStringInMiddle(str, g, this);
		}
	}

	/**
	 * Set selected program as current focused program
	 * @param program
	 */
	public void setProgram(Program program) {
		this.currentProgram = program;
	}
}

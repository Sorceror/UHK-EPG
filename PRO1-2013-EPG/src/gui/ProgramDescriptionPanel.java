/**
 * 
 */
package gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import tvdata.Program;

/**
 * Panel pro vykresleni popisku aktualne vybraneho programu
 * @author Pavel Janecka
 */
public class ProgramDescriptionPanel extends JComponent {
	private Program currentProgram;
	private JTextArea taDescription;
	private JTextArea taTitle;
	
	private final String STR_NO_DESCRIPTION = "No description";

	/**
	 * Standardni konstruktor komponenty s popiskem poradu.
	 * Vytvari komponenty GUI
	 */
	public ProgramDescriptionPanel() {
		this.setLayout(new BorderLayout());
		
		taTitle = new JTextArea();
		taTitle.setWrapStyleWord(true);
		taTitle.setLineWrap(true);
		taTitle.setEditable(false);
		taTitle.setFont(Utils.DEFAULT_APP_FONT);
		taTitle.setForeground(Utils.DEFAULT_APP_FONT_COLOR);
		taTitle.setHighlighter(null);
		this.add(taTitle, BorderLayout.NORTH);
		
		taDescription = new JTextArea();
		taDescription.setWrapStyleWord(true);
		taDescription.setLineWrap(true);
		taDescription.setEditable(false);
		taDescription.setFont(Utils.DEFAULT_APP_FONT);
		taDescription.setForeground(Utils.DEFAULT_APP_FONT_COLOR);
		taDescription.setHighlighter(null);
		this.add(taDescription, BorderLayout.CENTER);
		
		initText();
	}
	
	/**
	 * Set selected program as current focused program
	 * @param program {@link Program} instance
	 */
	public void setProgram(Program program) {
		this.currentProgram = program;
		initText();
	}

	/**
	 * Nastavi text jednotlivym komponentam dle stavu aktualne vybraneho programu
	 */
	private void initText() {
		if (currentProgram != null) {
			taTitle.setText(currentProgram.getTitle());
			if (taDescription != null) taDescription.setText(currentProgram.getDescription());
			else taDescription.setText(STR_NO_DESCRIPTION);
		} else {
			taTitle.setText("");
			taDescription.setText(Utils.STR_NO_PROGRAM);
		}
	}
}

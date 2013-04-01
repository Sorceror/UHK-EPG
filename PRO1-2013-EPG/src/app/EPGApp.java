/**
 * 
 */
package app;

import gui.ProgramDescriptionPanel;
import gui.ProgramIconPanel;
import gui.ProgressBarPanel;
import gui.TimePanel;
import gui.TimelinePanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import tvdata.Channel;
import tvdata.Program;
import datasources.CeskaTelevizeProgrammeSource;
import datasources.exception.DataSourceException;

/**
 * Spousteci trida aplikace, vytvari GUI, vola datove providery
 * @author Pavel Janecka
 */
public class EPGApp {
	private JFrame window;
	private ProgramIconPanel programIconPanel;
	private ProgramDescriptionPanel programDescriptionPanel;
	private TimePanel timePanel;
	private ProgressBarPanel progressBarPanel;
	private TimelinePanel timelinePanel;
	
	private int mouseX = -1;

	/**
	 * Konstruktor aplikace, vytvari gui a spousti logiku
	 */
	public EPGApp() {
		createGUI();
		initListeners();
		
		CeskaTelevizeProgrammeSource ctSource = new CeskaTelevizeProgrammeSource();
		try {
			List<Channel> channels = ctSource.loadChannels();
			Collections.sort(channels);
			timelinePanel.setChannels(channels);
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Vytvari komponenty GUI
	 */
	private void createGUI() {
		window = new JFrame("FIM EPG sample ~ Pavel Janecka 2013");
		window.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .2; c.gridheight = 2;
		programIconPanel = new ProgramIconPanel();
		window.add(programIconPanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .2; c.gridheight = 2;
		programDescriptionPanel = new ProgramDescriptionPanel();
		window.add(programDescriptionPanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .1;
		timePanel = new TimePanel();
		window.add(timePanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 1; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .1;
		progressBarPanel = new ProgressBarPanel();
		window.add(progressBarPanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 2; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = 1; c.gridwidth = 3;
		timelinePanel = new TimelinePanel();
		window.add(timelinePanel, c);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(900, 300));
		window.pack();
		
		programDescriptionPanel.setPreferredSize(new Dimension(100, 100));
		
		window.setVisible(true);
	}
	
	/**
	 * Registruje a obsluhuje listenery komponent
	 */
	private void initListeners() {
		timelinePanel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				timelinePanel.zoom(e);
				timelinePanel.repaint();
			}
		});
		timelinePanel.addMouseMotionListener(new MouseMotionAdapter() {
			
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (mouseX != -1 && SwingUtilities.isRightMouseButton(e)) {
					timelinePanel.moveTimeline(e.getX() - mouseX);
					timelinePanel.repaint();
				}
				mouseX = e.getX();
			}
		});
		timelinePanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				if (SwingUtilities.isLeftMouseButton(e)) {
					Program p = timelinePanel.getProgramByCoordinates(e.getX(), e.getY());
					if (p != null) fireProgramChange(p);
					timelinePanel.setSelectedProgram(p);
					timelinePanel.repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseX = -1;
			}
		});
		window.requestFocusInWindow();
		window.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_R) {
					timelinePanel.resetZoom();
					timelinePanel.resetMoveOfTimeline();
					timelinePanel.repaint();
				}
			}
		});
	}
	
	/**
	 * Nastavuje novy vybrany televizni program vsem komponentam a zajistuje jejich prekresleni
	 * @param program {@link Program} instance
	 */
	private void fireProgramChange(Program program) {
		programIconPanel.setProgram(program);
		programDescriptionPanel.setProgram(program);
		progressBarPanel.setProgram(program);
		
		programIconPanel.repaint();
		programDescriptionPanel.repaint();
		progressBarPanel.repaint();
	}

	/**
	 * main :)
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new EPGApp();
			}
		});
	}

}

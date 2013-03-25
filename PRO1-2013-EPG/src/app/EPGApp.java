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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
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

	public EPGApp() {
		createGUI();
		initListeners();
		
		CeskaTelevizeProgrammeSource ctSource = new CeskaTelevizeProgrammeSource();
		try {
			List<Channel> channels = ctSource.loadChannels();
			Collections.sort(channels);
			fireProgramChange(channels.get(0).getProgrammes().get(0));
			timelinePanel.setChannels(channels);
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}
	
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
	
	private void initListeners() {
		timelinePanel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int diff = e.getWheelRotation();
				if (diff > 0) {
					timelinePanel.zoomIn();
				} else {
					timelinePanel.zoomOut();
				}
				timelinePanel.repaint();
			}
		});
		timelinePanel.addMouseMotionListener(new MouseMotionAdapter() {
			
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (mouseX != -1) {
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
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseX = -1;
			}
		});
	}
	
	public void fireProgramChange(Program program) {
		this.programIconPanel.setProgram(program);
		this.programDescriptionPanel.setProgram(program);
		this.progressBarPanel.setProgram(program);
	}

	/**
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

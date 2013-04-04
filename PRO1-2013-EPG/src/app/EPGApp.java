/**
 * 
 */
package app;

import gui.DatePanel;
import gui.ProgramDescriptionPanel;
import gui.ProgramIconPanel;
import gui.ProgressBarPanel;
import gui.TimePanel;
import gui.TimelinePanel;
import gui.Utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;

import tvdata.Channel;
import tvdata.Program;
import datasources.DataStore;
import datasources.parsers.CeskaTelevizeDataSourceParser;
import datasources.parsers.DataSourceParser;
import datasources.providers.CeskaTelevizeOnlineDataSourceProvider;
import datasources.providers.OnlineDataSourceProvider;

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
	private DatePanel datePanel;
	private JButton btnDayBefore;
	private JButton btnDayAfter;
	
	private DataStore dataStore;
	
	private Date currentDay;
	private int mouseX = -1;

	/**
	 * Konstruktor aplikace, vytvari gui a spousti logiku
	 */
	public EPGApp() {
		createGUI();
		initListeners();
		
		dataStore = new DataStore();
		OnlineDataSourceProvider ctProvider = new CeskaTelevizeOnlineDataSourceProvider();
		DataSourceParser ctParser = new CeskaTelevizeDataSourceParser();
		dataStore.registerChannelAndSource("ct1", ctProvider, ctParser);
		dataStore.registerChannelAndSource("ct2", ctProvider, ctParser);
		dataStore.registerChannelAndSource("ct4", ctProvider, ctParser);
		dataStore.registerChannelAndSource("ct24", ctProvider, ctParser);
		
		currentDay = new Date();
		try {
			loadDataForDay(currentDay);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Vytvari komponenty GUI
	 */
	private void createGUI() {
		window = new JFrame("FIM EPG sample ~ Pavel Janecka 2013");
		window.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .2; c.gridheight = 3;
		programIconPanel = new ProgramIconPanel();
		window.add(programIconPanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .2; c.gridheight = 3;
		programDescriptionPanel = new ProgramDescriptionPanel();
		window.add(programDescriptionPanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .1; c.gridwidth = 3;
		timePanel = new TimePanel();
		window.add(timePanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 1; c.fill = GridBagConstraints.BOTH; c.weightx = 0.1; c.weighty = .05;
		btnDayBefore = new BasicArrowButton(BasicArrowButton.WEST);
		btnDayBefore.setBackground(Utils.DEFAULT_APP_BACKGROUND_COLOR);
		btnDayBefore.setBorder(BorderFactory.createLineBorder(Utils.DEFAULT_APP_BACKGROUND_COLOR));
		btnDayBefore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeDate(-1);
			}
		});
		window.add(btnDayBefore, c);

		c = new GridBagConstraints();
		c.gridx = 3; c.gridy = 1; c.fill = GridBagConstraints.BOTH; c.weightx = 0.8; c.weighty = .05;
		datePanel = new DatePanel();
		window.add(datePanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 4; c.gridy = 1; c.fill = GridBagConstraints.BOTH; c.weightx = 0.1; c.weighty = .05;
		btnDayAfter = new BasicArrowButton(BasicArrowButton.EAST);
		btnDayAfter.setBackground(Utils.DEFAULT_APP_BACKGROUND_COLOR);
		btnDayAfter.setBorder(BorderFactory.createLineBorder(Utils.DEFAULT_APP_BACKGROUND_COLOR));
		btnDayAfter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeDate(+1);
			}
		});
		window.add(btnDayAfter, c);
		
		c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 2; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = .05; c.gridwidth = 3;
		progressBarPanel = new ProgressBarPanel();
		window.add(progressBarPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 3; c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = 1; c.gridwidth = 5;
		timelinePanel = new TimelinePanel();
		window.add(timelinePanel, c);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setMinimumSize(new Dimension(600, 310));
		window.setPreferredSize(new Dimension(900, 310));
		window.pack();
		
		programDescriptionPanel.setPreferredSize(new Dimension(100, 100));
		
		window.setVisible(true);
	}
	
	/**
	 * Nastavuje datum zobrazeni EPG na vybrany den
	 * @param selectedDate {@link Date} instance
	 */
	private void changeDate(Date selectedDate) {
		try {
			loadDataForDay(selectedDate);
			currentDay = selectedDate;
		} catch (Exception e) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			JOptionPane.showMessageDialog(window, "Cannot load data for date " + dateFormat.format(selectedDate), "Error during loading", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Nastavuje datum zobrazeni dle zmeny poctu dnu od aktualne zobrazovaneho dne
	 * @param dayOffset int zmena poctu dnu
	 */
	private void changeDate(int dayOffset) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentDay);
		c.add(Calendar.DAY_OF_YEAR, dayOffset);
		changeDate(c.getTime());
	}
	
	/**
	 * Nahraje data pro vybrany den a zobrazi je v gui EPG
	 * @param day {@link Date} instance
	 * @throws Exception pokud dojde k chybe pri nahravani dat
	 */
	private void loadDataForDay(Date day) throws Exception {
		List<Channel> channels = new ArrayList<Channel>(4);
		channels.add(dataStore.getDataForChannel("ct1", day));
		channels.add(dataStore.getDataForChannel("ct2", day));
		channels.add(dataStore.getDataForChannel("ct4", day));
		channels.add(dataStore.getDataForChannel("ct24", day));
		Collections.sort(channels);
		timelinePanel.setChannels(channels);
		timelinePanel.repaint();
		datePanel.setCurrentDate(day);
		datePanel.repaint();
		fireProgramChange(null);
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
					fireProgramChange(p);
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
					changeDate(new Date());
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

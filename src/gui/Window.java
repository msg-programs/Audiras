package gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;


import main.RadioMain;

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Dimension DIM = new Dimension(387, 300);

	private SettingPanel settings;
	private RecordPanel record;
	private ListPanel list;

	private JTabbedPane tabs;

	public Window() {
		super("Audiras");

		this.setMaximumSize(DIM);
		this.setMinimumSize(DIM);
		this.setPreferredSize(DIM);
		this.setLayout(null);
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				RadioMain.win = null;
			}
		});

		tabs = new JTabbedPane();
		tabs.setBounds(0, 0, 400, 300);

		record = new RecordPanel();
		list = new ListPanel();
		settings = new SettingPanel();

		tabs.addTab("Record", record);
		tabs.addTab("Browse", list);
		tabs.addTab("Settings", settings);

		this.add(tabs);

		this.pack();
		this.setVisible(true);
	}

}

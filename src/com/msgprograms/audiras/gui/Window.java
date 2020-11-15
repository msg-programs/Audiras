package com.msgprograms.audiras.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.msgprograms.audiras.main.RadioMain;
import com.msgprograms.audiras.settings.Lang;
import com.msgprograms.audiras.settings.Settings;

public class Window extends JFrame implements ChangeListener {

	private static final long serialVersionUID = 1L;

	private static final Dimension DIM = new Dimension(387, 300);

	private SettingPanel settings;
	private RecordPanel record;
	private ListPanel list;

	private JTabbedPane tabs;

	private int lastIdx = 0;

	public Window() {
		super("Audiras v" + RadioMain.VERSION);
		ImageIcon icon = new ImageIcon(Settings.ICO_FILE.getAbsolutePath());
		this.setIconImage(icon.getImage());

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
//				System.exit(0);
			}
		});

		tabs = new JTabbedPane();
		tabs.setBounds(0, 0, 400, 300);

		record = new RecordPanel();
		list = new ListPanel();
		settings = new SettingPanel();

		tabs.addTab(Lang.get("tab_record"), record);
		tabs.addTab(Lang.get("tab_list"), list);
		tabs.addTab(Lang.get("tab_settings"), settings);
		tabs.addChangeListener(this);

		this.add(tabs);

		this.pack();
		this.setVisible(true);
	}

	@Override
	public void stateChanged(@SuppressWarnings("exports") ChangeEvent e) {
		if (list.updateExtTable) {
			record.populateTable();
			list.updateExtTable = false;
			Settings.save();
		}
		int idx = tabs.getSelectedIndex();
		if (lastIdx == 2 && tabs.getSelectedIndex() != 2) {
			settings.save();
		}
		lastIdx = idx;
	}

	public void save() {
		settings.save();
	}

	public void update() {
		record.updateGUI();
	}

}

package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import main.RadioMain;
import settings.Lang;
import settings.Settings;
import streamlogic.RadioStation;
import streamlogic.RecordingMaster;
import streamlogic.StreamList;

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

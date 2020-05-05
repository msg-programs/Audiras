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

public class Window extends JFrame implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private static final Dimension DIM = new Dimension(387, 300);

	private JPanel main, settings, search;

	private JTable table, table2;
	private DefaultTableModel model, model2;

	private InfoPanel dispR, list;

	private JButton recToggle, delete, masOn, masOff, save, add, addS;

	private JTabbedPane tabs;

	private JLabel gb;

	private int stationID;

	public JCheckBox recOnBoot, instRec, showWindow;
	public JTextField block;
	public JComboBox<String> mode;

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

// TABS SETUP START
		tabs = new JTabbedPane();
		tabs.setBounds(0, 0, 400, 300);

// -MAIN TAB SETUP START
		main = new JPanel();
		main.setLayout(null);

// --LEFT SIDE SETUP START
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder contentB = BorderFactory.createTitledBorder(border, Lang.get("strm_lst"));

		JPanel dispL = new JPanel();
		dispL.setLayout(null);
		dispL.setBorder(contentB);
		dispL.setBounds(10, 10, 160, 217);
		main.add(dispL);

// ---TABLE SETUP START
		model = new DefaultTableModel(0, 1);

		loadStreams();

		table = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};

		table.setModel(model);
		table.setTableHeader(null);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.getSelectionModel().addListSelectionListener(this);
		table.setRowSelectionInterval(0, 0);

		JScrollPane pane = new JScrollPane(table);
		pane.setBounds(10, 22, 140, 131);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		dispL.add(pane);

// ---TABLE SETUP END

		masOn = new JButton(Lang.get("mstr_on"));
		masOn.setBounds(10, 160, 140, 20);
		dispL.add(masOn);
		masOn.addActionListener(this);

		masOff = new JButton(Lang.get("mstr_off"));
		masOff.setBounds(10, 185, 140, 20);
		dispL.add(masOff);
		masOff.addActionListener(this);

// --LEFT SETUP END

// --RIGHT SETUP START
		TitledBorder contentR = BorderFactory.createTitledBorder(border, Lang.get("strm_info"));

		dispR = new InfoPanel();
		dispR.setBounds(180, 10, 180, 217);
		dispR.setBorder(contentR);
		dispR.setLayout(null);

		recToggle = new JButton(Lang.get("rec_tog_on"));
		recToggle.setEnabled(false);
		recToggle.setBounds(15, 160, 150, 20);
		dispR.add(recToggle);
		recToggle.addActionListener(this);

		delete = new JButton(Lang.get("dlt_strm"));
		delete.setBounds(15, 185, 150, 20);
		delete.setEnabled(false);
		dispR.add(delete);
		delete.addActionListener(this);

//--RIGHT SETUP END

		main.add(dispR);

// -MAIN SETUP END

// -SETTINGS SETUP START

		settings = new JPanel();
		settings.setLayout(null);

		recOnBoot = new JCheckBox();
		recOnBoot.setText(Lang.get("rec_on_bt"));
		recOnBoot.setBounds(10, 10, 260, 15);
		recOnBoot.setSelected(Settings.getBootRec());
		settings.add(recOnBoot);

		showWindow = new JCheckBox();
		showWindow.setText(Lang.get("shw_win"));
		showWindow.setBounds(10, 30, 350, 15);
		showWindow.setSelected(Settings.getShowWin());
		settings.add(showWindow);

		instRec = new JCheckBox();
		instRec.setText(Lang.get("inst_rec"));
		instRec.setBounds(10, 50, 350, 15);
		instRec.setSelected(Settings.getInstRec());
		settings.add(instRec);

		String[] choices = { Lang.get("num_per"), Lang.get("sze_per"), Lang.get("num_all"), Lang.get("sze_all") };

		mode = new JComboBox<String>(choices);
		mode.setBounds(10, 75, 250, 20);
		mode.setSelectedIndex(Settings.getBlockCond());
		settings.add(mode);
		mode.addActionListener(this);

		block = new JTextField();
		block.setText((mode.getSelectedIndex() % 2 == 1)
				? String.valueOf(String.format("%.3f", (float) Settings.getBlockMax() / 1000f)).replace(".", ",")
				: String.valueOf(String.format("%.0f", (float) Settings.getBlockMax() / 1000f)).replace(".", ","));
		block.setBounds(265, 75, 50, 20);
		settings.add(block);

		save = new JButton(Lang.get("save"));
		save.setBounds(270, 207, 90, 20);
		settings.add(save);
		save.addActionListener(this);

		gb = new JLabel("GB");
		gb.setBounds(316, 36, 20, 20);
		gb.setVisible(Settings.getBlockCond() % 2 == 1);
		settings.add(gb);

// -SETTINGS SETUP END

// -SEARCH SETUP START
// -RIGHT SETUP START 		
		search = new JPanel();
		search.setLayout(null);

		list = new InfoPanel();
		list.setBounds(180, 10, 180, 217);
		list.setBorder(contentR);
		list.setLayout(null);
		search.add(list);
//-RIGHT SETUP END
		// --LEFT SIDE SETUP START
		TitledBorder contentS = BorderFactory.createTitledBorder(border, Lang.get("strm_lst"));
		JPanel dispL2 = new JPanel();
		dispL2.setLayout(null);
		dispL2.setBorder(contentS);
		dispL2.setBounds(10, 10, 160, 217);
		search.add(dispL2);

		add = new JButton(Lang.get("add"));
		add.setBounds(15, 160, 150, 20);
		list.add(add);
		add.addActionListener(this);

		addS = new JButton(Lang.get("add_new"));
		addS.setBounds(15, 185, 150, 20);
		list.add(addS);
		addS.addActionListener(this);

// --LEFT SIDE SETUP END		
		// ---TABLE SETUP START
		model2 = new DefaultTableModel(0, 1);

		loadList();

		table2 = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};

		table2.setModel(model2);
		table2.setTableHeader(null);
		table2.setColumnSelectionAllowed(false);
		table2.setRowSelectionAllowed(false);
		table2.getSelectionModel().addListSelectionListener(this);
		table2.setRowSelectionInterval(0, 0);

		JScrollPane pane2 = new JScrollPane(table2);
		pane2.setBounds(10, 22, 140, 186);
		pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		table2.setFillsViewportHeight(true);
		dispL2.add(pane2);

		// ---TABLE SETUP END

// -SEARCH SETUP END		

		tabs.addTab(Lang.get("rcrd_tab"), main);
		tabs.addTab(Lang.get("srch"), search);
		tabs.addTab(Lang.get("sttngs_tab"), settings);

		this.add(tabs);

// TAB SETUP END

		this.pack();
		this.setVisible(true);
	}

	private void loadList() {
		String[] s = new String[1];
		boolean isValid = false;
		for (RadioStation rs : StreamList.stations) {
			isValid = true;
			if (rs != null) {
				s[0] = rs.name;
			} else {
				s[0] = Lang.get("no_strm");
			}
			model2.addRow(s);
		}
		if (!isValid) {
			s[0] = Lang.get("lst_empty");
			add.setEnabled(false);
			model2.addRow(s);
		}

	}

	private void loadStreams() {
		String[] s = new String[1];
		for (RadioStation rs : RecordingMaster.stations) {
			if (rs != null) {
				s[0] = rs.name;
			} else {
				s[0] = Lang.get("no_strm");
			}
			model.addRow(s);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent ev) {

		switch (tabs.getSelectedIndex()) {
		case 0:
			stationID = table.getSelectedRow();

			RadioStation rs = RecordingMaster.stations[stationID];

			this.recToggle.setEnabled(true);

			if (!(rs == null)) {
				dispR.setSName(rs.name);
				dispR.setFormat(rs.format);
				dispR.setBRate(rs.bitrate);
				dispR.setGenre(rs.genre);
				dispR.setStatus(rs.getStatus());
				recToggle.setText(rs.getStatusB());
				delete.setEnabled(true);

				if (rs.lock) {
					recToggle.setEnabled(false);
				}
			} else {
				dispR.setSName("");
				dispR.setFormat("");
				dispR.setBRate("");
				dispR.setGenre("");
				dispR.setStatus("");
				recToggle.setEnabled(false);
				delete.setEnabled(false);
			}
			break;
		case 1:

			table2.getSelectedRow();
			if (table2.getSelectedRow() < 0) {
				return;
			}
			RadioStation rs2 = StreamList.stations.get(table2.getSelectedRow());

			if (!(rs2 == null)) {
				list.setSName(rs2.name);
				list.setFormat(rs2.format);
				list.setBRate(rs2.bitrate);
				list.setGenre(rs2.genre);
				list.setStatus(rs2.getStatus());
			}

			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		switch (tabs.getSelectedIndex()) {
		case 2:

			if (ae.getSource().equals(save)) {
				Settings.setBlockMode(mode.getSelectedIndex());
				Settings.setBlockMax(block.getText());
				Settings.setBootRec(recOnBoot.isSelected());
				Settings.setInstRec(instRec.isSelected());
				Settings.setShowWin(showWindow.isSelected());
				Settings.save();
				return;
			}

			if (ae.getSource().equals(mode)) {
				if (mode.getSelectedIndex() % 2 == 1) {
					gb.setVisible(true);
					block.setText(
							String.valueOf(String.format("%.3f", Float.valueOf(block.getText().replace(",", "."))))
									.replace(".", ","));
				} else {
					gb.setVisible(false);
					block.setText(
							String.valueOf(String.format("%.0f", Float.valueOf(block.getText().replace(",", "."))))
									.replace(".", ","));
				}
			}

			return;

		case 0:

			if (ae.getSource().equals(masOn)) {
				RecordingMaster.setAllOn();
				return;
			}
			if (ae.getSource().equals(masOff)) {
				RecordingMaster.setAllOff();
				return;
			}
			if (ae.getSource().equals(recToggle)) {
				RecordingMaster.toggle(table.getSelectedRow());
			}

			if (ae.getSource().equals(delete)) {

				int i = JOptionPane.showConfirmDialog(null, Lang.get("dlt_strm_conf"), null, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (i == JOptionPane.YES_OPTION) {
					RadioStation rs = RecordingMaster.stations[table.getSelectedRow()];
					rs.stopRec();

					model2.removeRow(0);
					add.setEnabled(true);

					StreamList.stations.add(rs);
					String[] s = { rs.name };
					model2.addRow(s);

					rs = null;
					RecordingMaster.stations[table.getSelectedRow()] = null;

					model.setValueAt(Lang.get("no_strm"), table.getSelectedRow(), 0);

				}

				return;
			}

			RadioStation rs = RecordingMaster.stations[table.getSelectedRow()];

			if (rs != null) {
				dispR.setStatus(rs.getStatus());
				recToggle.setText(rs.getStatusB());
				if (rs.err) {
					recToggle.setText(Lang.get("rec_tog_on"));
					recToggle.setEnabled(true);
					dispR.setStatus(Lang.get("rec_err"));
				}

			} else {
				dispR.setSName("");
				dispR.setFormat("");
				dispR.setBRate("");
				dispR.setGenre("");
				dispR.setStatus("");
				recToggle.setEnabled(false);
			}
			return;
		case 1:
			if (ae.getSource().equals(add)) {

				if (table2.getValueAt(0, 0).equals(Lang.get("lst_empty"))) {
					add.setEnabled(false);
					return;
				}

				for (int i = 0; i < Settings.getMaxStreams(); i++) {
					RadioStation ra = RecordingMaster.stations[i];

					if (ra == null && table2.getSelectedRow() >= 0) {
						ra = new RadioStation(StreamList.stations.get(table2.getSelectedRow()).url, i);
						RecordingMaster.stations[i] = ra;
						StreamList.stations.remove(table2.getSelectedRow());
						model2.removeRow(table2.getSelectedRow());
						model.setValueAt(ra.name, i, 0);
						if (model2.getRowCount() == 0) {
							String[] s = { Lang.get("lst_empty") };
							add.setEnabled(false);
							model2.addRow(s);
						}
						return;
					}
				}
				JOptionPane.showMessageDialog(null, Lang.get("lst_full"));

			}
			if (ae.getSource().equals(addS)) {
				String url = (String) JOptionPane.showInputDialog(null, Lang.get("new_strm_url"));
				if (StreamList.test(url)) {
					JOptionPane.showMessageDialog(null, Lang.get("add_succ"));
					String[] a = { StreamList.add(url).name };

					model2.addRow(a);
				}
			}
		}
	}
}

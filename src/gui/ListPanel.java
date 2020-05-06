package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import settings.Lang;
import settings.Settings;
import streamlogic.RadioStation;
import streamlogic.RecordingMaster;
import streamlogic.StreamList;

public class ListPanel extends JPanel implements ActionListener, ListSelectionListener {

	private DefaultTableModel model2;
	private JTable table2;
	private InfoPanel list;
	private JButton moveRec, addS;

	public ListPanel() {

		this.setLayout(null);

		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder contentR = BorderFactory.createTitledBorder(border, "Stream info");

		list = new InfoPanel();
		list.setBounds(180, 10, 180, 217);
		list.setBorder(contentR);
		list.setLayout(null);
		this.add(list);
//-RIGHT SETUP END

		// --LEFT SIDE SETUP START
		TitledBorder contentS = BorderFactory.createTitledBorder(border, "Stream list");
		JPanel dispL2 = new JPanel();
		dispL2.setLayout(null);
		dispL2.setBorder(contentS);
		dispL2.setBounds(10, 10, 160, 217);
		this.add(dispL2);

		moveRec = new JButton("Record Stream");
		moveRec.setBounds(15, 160, 150, 20);
		list.add(moveRec);
		moveRec.addActionListener(this);

		addS = new JButton("Add new");
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
	}

	private void loadList() {
		String[] s = new String[1];
		boolean isValid = false;
		for (RadioStation rs : StreamList.stations) {
			isValid = true;
			if (rs != null) {
				s[0] = rs.name;
			} else {
				s[0] = "[No Stream]";
			}
			model2.addRow(s);
		}
		if (!isValid) {
			s[0] = "[Empty]";
			moveRec.setEnabled(false);
			model2.addRow(s);
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		table2.getSelectedRow();
		if (table2.getSelectedRow() < 0) {
			return;
		}
		RadioStation rs2 = StreamList.stations.get(table2.getSelectedRow());

		if (!(rs2 == null)) {
			list.updateText(rs2);
		}

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(moveRec)) {

			if (table2.getValueAt(0, 0).equals("[Empty]")) {
				moveRec.setEnabled(false);
				return;
			}

			for (int i = 0; i < Settings.getMaxStreams(); i++) {
				RadioStation ra = RecordingMaster.stations[i];

				if (ra == null && table2.getSelectedRow() >= 0) {
					ra = new RadioStation(StreamList.stations.get(table2.getSelectedRow()).url, i);
					RecordingMaster.stations[i] = ra;
					StreamList.stations.remove(table2.getSelectedRow());
					model2.removeRow(table2.getSelectedRow());
//					model.setValueAt(ra.name, i, 0);
					if (model2.getRowCount() == 0) {
						String[] s = { "[Empty]" };
						moveRec.setEnabled(false);
						model2.addRow(s);
					}
					return;
				}
			}
			JOptionPane.showMessageDialog(null, "Recording list is full!", "Error",JOptionPane.ERROR_MESSAGE);

		}
		if (ae.getSource().equals(addS)) {
			String url = (String) JOptionPane.showInputDialog(null, "Enter new stream's URL");
			if (StreamList.test(url)) {
				JOptionPane.showMessageDialog(null, "Stream successfully added");
				String[] a = { StreamList.add(url).name };

				model2.addRow(a);
			}
		}

	}

}

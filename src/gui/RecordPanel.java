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
import streamlogic.RadioStation;
import streamlogic.RecordingMaster;
import streamlogic.StreamList;

public class RecordPanel extends JPanel implements ActionListener, ListSelectionListener {

	private JTable table;
	private DefaultTableModel model;
	private JButton  masOn, masOff,recToggle, delete;
	private InfoPanel dispR;
	
	private int stationID;

	public RecordPanel() {
		this.setLayout(null);

		// --LEFT SIDE SETUP START
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder contentB = BorderFactory.createTitledBorder(border, "Streams");

		JPanel dispL = new JPanel();
		dispL.setLayout(null);
		dispL.setBorder(contentB);
		dispL.setBounds(10, 10, 160, 217);
		this.add(dispL);

		// ---TABLE SETUP START
		model = new DefaultTableModel(0, 1);

		populateStreamList();

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

		masOn = new JButton("Start all");
		masOn.setBounds(10, 160, 140, 20);
		dispL.add(masOn);
		masOn.addActionListener(this);

		masOff = new JButton("Stop all");
		masOff.setBounds(10, 185, 140, 20);
		dispL.add(masOff);
		masOff.addActionListener(this);

		// --LEFT SETUP END

		// --RIGHT SETUP START
		TitledBorder contentR = BorderFactory.createTitledBorder(border, "Stream info");

		dispR = new InfoPanel();
		dispR.setBounds(180, 10, 180, 217);
		dispR.setBorder(contentR);
		dispR.setLayout(null);

		recToggle = new JButton("Start recording");
		recToggle.setEnabled(false);
		recToggle.setBounds(15, 160, 150, 20);
		dispR.add(recToggle);
		recToggle.addActionListener(this);

		delete = new JButton("Reomve stream");
		delete.setBounds(15, 185, 150, 20);
		delete.setEnabled(false);
		dispR.add(delete);
		delete.addActionListener(this);

		// --RIGHT SETUP END

		this.add(dispR);

	}

	private void populateStreamList() {
		
		for (int i = 0; i<model.getRowCount(); i++) {
			model.removeRow(i);
		}
		
		String[] s = new String[1];
		for (RadioStation rs : RecordingMaster.stations) {
			if (rs != null) {
				s[0] = rs.name;
			} else {
				s[0] = "[Empty]";
			}
			model.addRow(s);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
//		stationID = table.getSelectedRow();
//
//		RadioStation rs = RecordingMaster.stations[stationID];
//
//		recToggle.setEnabled(true);
//
//		if (!(rs == null)) {
//			dispR.updateText(rs);
//			recToggle.setText(rs.getStatusB());
//			delete.setEnabled(true);
//
//			if (rs.lock) {
//				recToggle.setEnabled(false);
//			}
//		} else {
//			dispR.updateText(null);
//			recToggle.setEnabled(false);
//			delete.setEnabled(false);
//		}

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
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

			int i = JOptionPane.showConfirmDialog(null, "Are you sure?", null, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (i == JOptionPane.YES_OPTION) {
				RadioStation rs = RecordingMaster.stations[table.getSelectedRow()];
				rs.stopRec();

//				model2.removeRow(0);
//				add.setEnabled(true);
//
//				StreamList.stations.add(rs);
//				String[] s = { rs.name };
//				model2.addRow(s);

				rs = null;
				RecordingMaster.stations[table.getSelectedRow()] = null;

				model.setValueAt("[Empty]", table.getSelectedRow(), 0);

			}

			return;
		}

		RadioStation rs = RecordingMaster.stations[table.getSelectedRow()];

		if (rs != null) {
//			dispR.setStatus(rs.getStatus());
//			recToggle.setText(rs.getStatusB());
//			if (rs.err) {
//				recToggle.setText(Lang.get("rec_tog_on"));
//				recToggle.setEnabled(true);
//				dispR.setStatus(Lang.get("rec_err"));
//			}

		} else {
			dispR.updateText(rs);
			recToggle.setEnabled(false);
		}
		return;

	}

}

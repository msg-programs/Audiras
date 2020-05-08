package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class RecordPanel extends JPanel implements ActionListener, ListSelectionListener {

	private JTable table;
	private DefaultTableModel model;
	private JButton masOn, masOff, recToggle, delete;
	private InfoPanel dispR;

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

		delete = new JButton("Remove stream");
		delete.setBounds(15, 185, 150, 20);
		delete.setEnabled(false);
		dispR.add(delete);
		delete.addActionListener(this);

		// --RIGHT SETUP END

		this.add(dispR);
		populateTable();

	}

	void populateTable() {

		if (model.getRowCount() > 0) {
			for (int i = model.getRowCount() - 1; i >= 0; i--) {
				model.removeRow(i);
			}
		}

		String[] s = new String[1];
		for (RadioStation rs : RecordingMaster.stations) {
			s[0] = rs.meta.name;
			model.addRow(s);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		int rowNr = table.getSelectedRow();

		if (rowNr < 0) {
			return;
		}
		RadioStation rs = RecordingMaster.getStation(rowNr);

		recToggle.setEnabled(true);

		dispR.updateText(rs);
		if (rs != null) {
			recToggle.setText(rs.getStatusB());
			delete.setEnabled(true);

			if (rs.lock) {
				recToggle.setEnabled(false);
			}
		} else {
			recToggle.setEnabled(false);
			delete.setEnabled(false);
		}

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

		int rowNr = table.getSelectedRow();
		if (ae.getSource().equals(delete)) {

			RadioStation rs = RecordingMaster.getStation(rowNr);
			rs.stopRec();
			RecordingMaster.remove(rs);

			model.removeRow(rowNr);

			return;
		}

		RadioStation rs = RecordingMaster.getStation(rowNr);

		dispR.updateText(rs);
		if (rs != null) {
			recToggle.setText(rs.getStatusB());
			if (rs.err) {
				recToggle.setText(Lang.get("rec_tog_on"));
				recToggle.setEnabled(true);
			}
		} else {
			recToggle.setEnabled(false);
		}
		return;
	}
}

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

import streamlogic.RadioStation;
import streamlogic.RecordingMaster;
import streamlogic.StationList;

public class ListPanel extends JPanel implements ActionListener, ListSelectionListener {

	private DefaultTableModel model;
	private JTable table;
	private InfoPanel info;
	private JButton moveRec, addS;

	public boolean updateExtTable = false;

	public ListPanel() {

		this.setLayout(null);

		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder contentR = BorderFactory.createTitledBorder(border, "Stream info");

		info = new InfoPanel();
		info.setBounds(180, 10, 180, 217);
		info.setBorder(contentR);
		info.setLayout(null);
		this.add(info);
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
		info.add(moveRec);
		moveRec.addActionListener(this);

		addS = new JButton("Add new");
		addS.setBounds(15, 185, 150, 20);
		info.add(addS);
		addS.addActionListener(this);

// --LEFT SIDE SETUP END		
		// ---TABLE SETUP START
		model = new DefaultTableModel(0, 1);

		populateTable();

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

		JScrollPane pane2 = new JScrollPane(table);
		pane2.setBounds(10, 22, 140, 186);
		pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		dispL2.add(pane2);

		// ---TABLE SETUP END
	}

	void populateTable() {

		String[] s = new String[1];

		for (RadioStation rs : StationList.stations) {
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

		RadioStation rs = StationList.getStation(rowNr);
		info.updateText(rs);
		if (RecordingMaster.stations.contains(rs)) {
			moveRec.setEnabled(false);
			moveRec.setText("Already on list!");
		} else {
			moveRec.setEnabled(true);
			moveRec.setText("Record Stream");
		}
		

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(moveRec)) {
			moveRecorder();
		}
		if (ae.getSource().equals(addS)) {
			String url = (String) JOptionPane.showInputDialog(null, "Enter new stream's URL");
			if (StationList.isValidStream(url)) {
				JOptionPane.showMessageDialog(null, "Stream successfully added");
				StationList.add(url);
			}
		}

	}

	private void moveRecorder() {
		int rowNr = table.getSelectedRow();

		if (rowNr < 0) {
			return;
		}

		if (rowNr >= 0) {
			RadioStation rs = StationList.getStation(rowNr);
			RecordingMaster.addStation(rs);
			
			moveRec.setEnabled(false);
			moveRec.setText("Already on list!");
			
			updateExtTable = true;
			return;

		}

	}

}

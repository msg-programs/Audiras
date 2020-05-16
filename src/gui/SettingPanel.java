package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import settings.Lang;
import settings.Settings;
import streamlogic.RecordingMaster;

public class SettingPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public JCheckBox startOnBoot, startRecs, showWindow;
	public JTextField block;
	public JComboBox<String> mode;

	private JButton save;

	private JLabel gb;

	public SettingPanel() {
		this.setLayout(null);

		startOnBoot = new JCheckBox();
		startOnBoot.setText(Lang.get("check_bootStart"));
		startOnBoot.setBounds(10, 10, 260, 15);
		startOnBoot.setSelected(Settings.doStartOnBoot());
		this.add(startOnBoot);

		showWindow = new JCheckBox();
		showWindow.setText(Lang.get("check_startWin"));
		showWindow.setBounds(10, 30, 350, 15);
		showWindow.setSelected(Settings.doShowWin());
		this.add(showWindow);

		startRecs = new JCheckBox();
		startRecs.setText(Lang.get("check_startRec"));
		startRecs.setBounds(10, 50, 350, 15);
		startRecs.setSelected(Settings.doStartRec());
		this.add(startRecs);

		String[] choices = { Lang.get("dd_numPer"),Lang.get("dd_sizePer"),Lang.get("dd_numAll"),Lang.get("dd_sizeAll") };

		mode = new JComboBox<String>(choices);
		mode.setBounds(10, 75, 250, 20);
		mode.setSelectedIndex(Settings.getBlockCond());
		this.add(mode);
		mode.addActionListener(this);

		block = new JTextField();
		block.setText((mode.getSelectedIndex() % 2 == 1)
				? String.format("%.3f",  Settings.getBlockMax())
				: String.format("%.0f",  Settings.getBlockMax()));
		block.setBounds(265, 75, 50, 20);
		this.add(block);

		save = new JButton(Lang.get("btn_save"));
		save.setBounds(270, 207, 90, 20);
		this.add(save);
		save.addActionListener(this);

		gb = new JLabel("GB");
		gb.setBounds(316, 74, 20, 20);
		gb.setVisible(Settings.getBlockCond() % 2 == 1);
		this.add(gb);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource().equals(save)) {
			save();
			return;
		}

		if (ae.getSource().equals(mode)) {
			if (mode.getSelectedIndex() % 2 == 1) {
				float size=0.5f;
				try {
					size = Float.parseFloat(block.getText());
				}catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, Lang.get("err_invFloat"), Lang.get("err"),JOptionPane.ERROR_MESSAGE);
				}
				
				gb.setVisible(true);
				block.setText(String.valueOf(size));
				Settings.setBlockMax(size);
			} else {
				// block mode is # recordings
				int num=1;
				try {
					num = Integer.parseInt(block.getText());
				}catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null,  Lang.get("err_invInt"),Lang.get("err"),JOptionPane.ERROR_MESSAGE);
				}
				
				gb.setVisible(false);
				block.setText(String.valueOf(num));
				Settings.setBlockMax((float)num);
			}
			// baba is you
		}
	}
	
	public void save() {
		Settings.setBlockMode(mode.getSelectedIndex());
		Settings.setBlockMax(Float.parseFloat(block.getText()));
		Settings.setStartOnBoot(startOnBoot.isSelected());
		Settings.setStartRec(startRecs.isSelected());
		Settings.setShowWin(showWindow.isSelected());
		RecordingMaster.doRecalcAll();
		Settings.save();
	}
	
}

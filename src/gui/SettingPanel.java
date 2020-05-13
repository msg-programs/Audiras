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

import settings.Settings;

public class SettingPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public JCheckBox recOnBoot, instRec, showWindow;
	public JTextField block;
	public JComboBox<String> mode;

	private JButton save;

	private JLabel gb;

	public SettingPanel() {
		this.setLayout(null);

		recOnBoot = new JCheckBox();
		recOnBoot.setText("Start recording on startup");
		recOnBoot.setBounds(10, 10, 260, 15);
		recOnBoot.setSelected(Settings.getBootRec());
		this.add(recOnBoot);

		showWindow = new JCheckBox();
		showWindow.setText("Show window on startup");
		showWindow.setBounds(10, 30, 350, 15);
		showWindow.setSelected(Settings.getShowWin());
		this.add(showWindow);

		instRec = new JCheckBox();
		instRec.setText("Start recording on startup");
		instRec.setBounds(10, 50, 350, 15);
		instRec.setSelected(Settings.getInstRec());
		this.add(instRec);

		String[] choices = { "Number of songs / stream", "Size of all songs / stream", "Number of all songs recorded", "Size of all songs recorded" };

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

		save = new JButton("Save");
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
			Settings.setBlockMode(mode.getSelectedIndex());
			Settings.setBlockMax(Float.parseFloat(block.getText()));
			Settings.setBootRec(recOnBoot.isSelected());
			Settings.setInstRec(instRec.isSelected());
			Settings.setShowWin(showWindow.isSelected());
			Settings.save();
			return;
		}

		if (ae.getSource().equals(mode)) {
			if (mode.getSelectedIndex() % 2 == 1) {
				float size=0.5f;
				try {
					size = Float.parseFloat(block.getText());
				}catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Invalid float!", "Error",JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(null, "Invalid integer!", "Error",JOptionPane.ERROR_MESSAGE);
				}
				
				gb.setVisible(false);
				block.setText(String.valueOf(num));
				Settings.setBlockMax((float)num);
			}
			// baba is you
		}
		
	}
	
}

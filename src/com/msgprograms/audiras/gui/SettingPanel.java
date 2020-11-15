package com.msgprograms.audiras.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.msgprograms.audiras.settings.Lang;
import com.msgprograms.audiras.settings.Settings;
import com.msgprograms.audiras.streamlogic.RecordingMaster;

public class SettingPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JCheckBox startOnBoot, startRecs, showWindow;
	private JTextField block;
	private JComboBox<String> mode;

	private JButton dirchange, langchange;

	private JLabel gb, dir, lang;

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

		String[] choices = { Lang.get("dd_numPer"), Lang.get("dd_sizePer"), Lang.get("dd_numAll"),
				Lang.get("dd_sizeAll") };

		mode = new JComboBox<String>(choices);
		mode.setBounds(10, 75, 250, 20);
		mode.setSelectedIndex(Settings.getBlockCond());
		this.add(mode);
		mode.addActionListener(this);

		block = new JTextField();
		block.setText((mode.getSelectedIndex() % 2 == 1) ? String.format("%.3f", Settings.getBlockMax())
				: String.format("%.0f", Settings.getBlockMax()));
		block.setBounds(265, 75, 50, 20);
		this.add(block);

		gb = new JLabel("GB");
		gb.setBounds(316, 74, 20, 20);
		gb.setVisible(Settings.getBlockCond() % 2 == 1);
		this.add(gb);

		dirchange = new JButton(Lang.get("btn_dirchange"));
		dirchange.setBounds(10, 110, 200, 20);
		this.add(dirchange);
		dirchange.addActionListener(this);

		dir = new JLabel(Lang.get("lbl_curr") + ": " + Settings.getStreamDir());
		dir.setBounds(10, 130, 350, 20);
		this.add(dir);
		
		langchange = new JButton(Lang.get("btn_langchange"));
		langchange.setBounds(10, 170, 200, 20);
		this.add(langchange);
		langchange.addActionListener(this);

		lang = new JLabel(Lang.get("lbl_curr") + ": " + Settings.getLangName());
		lang.setBounds(10, 190, 350, 20);
		this.add(lang);
	}

	@Override
	public void actionPerformed(@SuppressWarnings("exports") ActionEvent ae) {

		if (ae.getSource().equals(mode)) {
			if (mode.getSelectedIndex() % 2 == 1) {
				float size = 0.5f;
				try {
					size = Float.parseFloat(block.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, Lang.get("err_invFloat"), Lang.get("err"),
							JOptionPane.ERROR_MESSAGE);
				}

				gb.setVisible(true);
				block.setText(String.valueOf(size));
				Settings.setBlockMax(size);
			} else {
				// block mode is # recordings
				int num = 1;
				try {
					num = Integer.parseInt(block.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, Lang.get("err_invInt"), Lang.get("err"),
							JOptionPane.ERROR_MESSAGE);
				}

				gb.setVisible(false);
				block.setText(String.valueOf(num));
				Settings.setBlockMax((float) num);
			}
			// baba is you
		}

		if (ae.getSource().equals(dirchange)) {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory(Settings.THIS_DIR);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ret = jfc.showOpenDialog(null);

			if (ret != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File f = jfc.getSelectedFile();
			if (f != null && f.canRead() && f.canWrite()) {
				Settings.setStreamDir(f.getAbsolutePath());
				dir.setText(Lang.get("lbl_curr") + ": " + Settings.getStreamDir());
				RecordingMaster.resetStreamDirs();
			} else {
				JOptionPane.showMessageDialog(null, Lang.get("err_invDir"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (ae.getSource().equals(langchange)) {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory(Settings.DATA_DIR);
			int ret = jfc.showOpenDialog(null);

			if (ret != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File f = jfc.getSelectedFile();
			if (f != null && f.canRead() && f.getName().matches("lang_.{3}\\.txt")) {
				Settings.setLang(f.getName().substring(5,8));
				JOptionPane.showMessageDialog(null, Lang.get("diag_requireRestart"), Lang.get("msg"), JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			} else {
				JOptionPane.showMessageDialog(null, Lang.get("err_invLangFile"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void save() {
		System.out.println("Getting settings state");
		Settings.setBlockMode(mode.getSelectedIndex());
		Settings.setBlockMax(Float.parseFloat(block.getText()));
		Settings.setStartOnBoot(startOnBoot.isSelected());
		Settings.setStartRec(startRecs.isSelected());
		Settings.setShowWin(showWindow.isSelected());
		RecordingMaster.recalcAll();
		Settings.save();
	}

}

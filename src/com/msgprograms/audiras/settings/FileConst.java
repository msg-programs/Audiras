package com.msgprograms.audiras.settings;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import com.msgprograms.audiras.main.RadioMain;

public class FileConst {
	
	public static File THIS_DIR, DATA_DIR, ICO_FILE, INI_FILE, LIST_FILE;
	
	public static void init() {
		try {
			THIS_DIR =  new File(RadioMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			JOptionPane.showMessageDialog(null, Lang.get("err_whereami"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		DATA_DIR = new File(THIS_DIR.getAbsolutePath()+ "\\data");
		ICO_FILE = new File(DATA_DIR.getAbsolutePath() + "\\icon.png");
		INI_FILE = new File(DATA_DIR.getAbsolutePath() + "\\settings.ini");
		LIST_FILE = new File(DATA_DIR.getAbsolutePath() + "\\streams.txt");
	}
}

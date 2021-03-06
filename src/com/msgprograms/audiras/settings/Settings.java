package com.msgprograms.audiras.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.msgprograms.audiras.streamlogic.RadioStation;
import com.msgprograms.audiras.streamlogic.RecordingMaster;

public class Settings {

	private static HashMap<String, String> settings = new HashMap<>();

	public static final int NUM_PER = 0;
	public static final int SIZE_PER = 1;
	public static final int NUM_ALL = 2;
	public static final int SIZE_ALL = 3;

	public static final File THIS_DIR = new File(".");
	public static final File DATA_DIR = new File((THIS_DIR.getAbsolutePath() + "\\data").replace("\\.\\", "\\"));
	public static final File ICO_FILE = new File(DATA_DIR.getAbsolutePath() + "\\icon.png");
	public static final File INI_FILE = new File(DATA_DIR.getAbsolutePath() + "\\settings.ini");

	public static void init() {

		settings.put("lang", "eng");

		try {
			INI_FILE.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Lang.get("err_iniFile"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		try {
			BufferedReader bfr = new BufferedReader(new FileReader(INI_FILE));

			String line;
			while ((line = bfr.readLine()) != null) {
				settings.put(line.split("->")[0], line.split("->")[1]);
			}

			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void save() {
		System.out.println("Trying to save settings...");
		try {
			PrintStream out = new PrintStream(INI_FILE);

			for (String key : settings.keySet()) {
				if (!key.equals("num_streams") && !key.contains("station_")) {
					String val = settings.get(key);

					out.println(key + "->" + val);
				}
			}

			int i = 0;
			for (RadioStation rs : RecordingMaster.stations) {
				if (rs != null) {
					out.println("station_" + i + "->" + rs.id);
					i++;
				}
			}
			out.println("num_streams->" + i);
			out.close();
		} catch (Exception e) {
			try {
				if (INI_FILE.createNewFile()) {
					save();
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, Lang.get("err_iniFile"), Lang.get("err"),
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

		File astart = new File(
				System.getenv("appdata") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\audiras.bat");

		if (doStartOnBoot()) {

			if (astart.exists()) {
				return;
			}
			try {
				PrintWriter pw = new PrintWriter(astart);

				pw.println("@echo off");
				pw.println("cd /D \"" + THIS_DIR.getAbsolutePath().replace("\\.\\", "\\") + "\"");
				pw.println("call Audiras.bat");
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, Lang.get("err_astart"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			if (astart.exists()) {
				astart.delete();
			}
		}
		System.out.println("Settings saved!");
	}

	public static String getStreamDir() {
		if (!settings.containsKey("target_dir")) {
			settings.put("target_dir", (THIS_DIR.getAbsolutePath() + "\\Recordings").replace("\\.\\", "\\"));
		}

		return settings.get("target_dir");
	}

	public static int getBlockCond() {
		if (!settings.containsKey("block_cond")) {
			settings.put("block_cond", "0");
		}
		int i = Integer.valueOf(settings.get("block_cond"));
		return i;
	}

	public static float getBlockMax() {
		if (!settings.containsKey("block_max")) {
			settings.put("block_max", "1");
		}
		float f = Float.valueOf(settings.get("block_max"));
		return f;
	}

	public static void setBlockMode(int selectedIndex) {
		settings.put("block_cond", String.valueOf(selectedIndex));
	}

	public static void setBlockMax(float num) {
		settings.put("block_max", String.valueOf(num));
	}

	public static void setStartOnBoot(boolean selected) {
		settings.put("boot_strt", (selected) ? "1" : "0");

	}

	public static boolean doStartOnBoot() {
		if (!settings.containsKey("boot_strt")) {
			settings.put("boot_strt", "0");
		}
		return settings.get("boot_strt").equals("1");
	}

	public static boolean doShowWin() {
		if (!settings.containsKey("shw_win")) {
			settings.put("shw_win", "1");
		}
		return settings.get("shw_win").equals("1");
	}

	public static void setShowWin(boolean selected) {
		settings.put("shw_win", (selected) ? "1" : "0");

	}

	public static boolean doStartRec() {
		if (!settings.containsKey("strt_rec")) {
			settings.put("strt_rec", "0");
		}
		return settings.get("strt_rec").equals("1");
	}

	public static void setStartRec(boolean selected) {
		settings.put("strt_rec", (selected) ? "1" : "0");

	}

	public static int getStreamIDByIdx(int i) {
		String res = settings.get("station_" + i);
		if (res == null) {
			return -1;
		} else {
			return Integer.parseInt(res);
		}
	}

	public static int getNumStreams() {
		String res = settings.get("num_streams");
		if (res == null) {
			return 0;
		} else {
			return Integer.parseInt(res);
		}
	}

	public static void setStreamDir(String path) {
		settings.put("target_dir", path + "\\Recordings");
	}

	public static String getLang() {
		if (!settings.containsKey("lang")) {
			settings.put("lang", "eng");
		}
		return settings.get("lang");
	}

	public static void setLang(String lang) {
		settings.put("lang", lang);
	}

	public static String getLangName() {
		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(Lang.LANG), "UTF8"));
			String name = bfr.readLine();
			bfr.close();
			return name.split(":")[1].trim();
		} catch (IOException e) {
			System.err.println("Something went wrong while reading the language file:");
			e.printStackTrace();
		}
		return null;
	}

}
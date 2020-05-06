package settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import streamlogic.RadioStation;
import streamlogic.RecordingMaster;

public class Settings {

	private static HashMap<String, String> settings = new HashMap<>();

	public static final File THIS_DIR = new File(
			ClassLoader.getSystemClassLoader().getResource(".").getPath().replaceAll("%20", " "));

	private static final File INI = new File(THIS_DIR + "/data/settings.ini");
	public static final File ICO = new File(THIS_DIR + "/data/icon.png");
	public static final File BAT = new File(THIS_DIR + "/data/autoexec.bat");

	public static final int NUM_PER = 0;
	public static final int SIZE_PER = 1;
	public static final int NUM_ALL = 2;
	public static final int SIZE_ALL = 3;

	public static void init() {

		settings.put("language", "eng");

		if (!INI.exists()) {
			firstDiag();
		}

		if (!BAT.exists()) {
			try {

				PrintWriter pw = new PrintWriter(BAT);

				pw.println("@echo off");
				pw.println("java -jar \"" + THIS_DIR.getAbsolutePath() + "/Audiras.jar\"");
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error while creating autostart file!","Error",JOptionPane.ERROR_MESSAGE);
			}
		}

		try {
			BufferedReader bfr = new BufferedReader(new FileReader(INI));

			String line;
			while ((line = bfr.readLine()) != null) {
				settings.put(line.split("->")[0], line.split("->")[1]);
			}

			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void firstDiag() {
		if (!new File(THIS_DIR + "/data").mkdirs()) {
			JOptionPane.showMessageDialog(null, "Can't create /data directory!");
			System.exit(0);
		}

		// GET LANG FILE =============================
		JOptionPane.showMessageDialog(null, "Select language file");
		File file = null;
		boolean b = true;
		do {
			JFileChooser jf = new JFileChooser();
			jf.setCurrentDirectory(THIS_DIR);
			int result = jf.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				file = jf.getSelectedFile();
			}

			if (file == null) {
				int i = JOptionPane.showConfirmDialog(null, "Invalid file!", "Error", JOptionPane.OK_CANCEL_OPTION);
				if (i == 2) {
					JOptionPane.showMessageDialog(null, "Setup was aborted");
					System.exit(0);
				}
			} else {

				if (file.canRead()) {
					b = false;
				} else {
					JOptionPane.showMessageDialog(null, "Can't read from this file!");
				}
			}
		} while (b);

		// COPY LANG FILE =============================
		try {
			Files.move(Paths.get(file.toURI()),
					Paths.get(new File(System.getenv("APPDATA") + "/Audiras/" + file.getName()).toURI()),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error while moving the file");
			e1.printStackTrace();
		}
		settings.put("language", file.getName().split(".txt")[0]);
		Lang.init();

		JOptionPane.showMessageDialog(null,"Welcome, new Audiras user!");
//		JOptionPane.showMessageDialog(null, Lang.get("rcrd_dir"));

		// RECORDING DIR INIT ==================================

		// STREAM LIST INIT
		JOptionPane.showMessageDialog(null, "Select stream list");

		boolean c = true;

		file = null;

		do {

			JFileChooser jf = new JFileChooser();
			jf.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = jf.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				file = jf.getSelectedFile();
			}

			if (file == null) {
				int i = JOptionPane.showConfirmDialog(null, "Invalid directory!", "Error", JOptionPane.OK_CANCEL_OPTION);
				if (i == 2) {
					JOptionPane.showMessageDialog(null, "Setup was aborted");
					System.exit(0);
				}
			} else {

				if (file.canWrite() && file.canRead()) {
					c = false;
				} else {
					JOptionPane.showMessageDialog(null, "Can't read from / write to this directory");
				}
			}
		} while (c);

		try {
			Files.move(Paths.get(file.toURI()),
					Paths.get(new File(System.getenv("APPDATA") + "/Audiras/" + file.getName()).toURI()),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error while moving the file!");
			e1.printStackTrace();
		}

		// REC COND INIT ========================================
		String[] choices = { "Number of songs / stream", "Size of all songs / stream", "Number of all songs recorded", "Size of all songs recorded" };
		String input = (String) JOptionPane.showInputDialog(null,"Set stop condition", null,
				JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

		if (input == null) {
			JOptionPane.showMessageDialog(null, "Setup was aborted");
			System.exit(0);
		}

		int res = -1;

		if (input.equals("Number of songs / stream")) {
			res = Settings.NUM_PER;

		} else if (input.equals("Size of all songs / stream")) {
			res = Settings.SIZE_PER;

		} else if (input.equals("Number of all songs recorded")) {
			res = Settings.NUM_ALL;

		} else if (input.equals("Size of all songs recorded" )) {
			res = Settings.SIZE_ALL;
		}

		if (res == -1) {
			JOptionPane.showMessageDialog(null, "Setup was aborted");
			System.err.println("err stt");
			System.exit(0);
		}

		float lmt = -1;

		String in = null;

		do {

			if (res % 2 == 1) {
				in = (String) JOptionPane.showInputDialog(null, "Size to stop");
			} else if (res % 2 == 0) {
				in = (String) JOptionPane.showInputDialog(null, "Number to stop");
			}

			if (in == null) {
				JOptionPane.showMessageDialog(null, "Setup was aborted");
				System.err.println("err stt 2");
				System.exit(0);
			}

			try {
				lmt = Integer.parseInt(in);
				if (lmt <= 0)
					lmt = 1 / 0;
			} catch (Exception e) {
				lmt = -1;
			}

			if (in.equals("") || lmt == -1) {
				JOptionPane.showMessageDialog(null,"Invalid number!");
			}

		} while (in.equals("") || lmt == -1);

		settings.put("block_cond", Integer.toString(res));
		settings.put("block_max", Float.toString(lmt * 1000));

		JOptionPane.showMessageDialog(null, "Setup complete!");

		Settings.save();
	}

	public static void save() {
		try {
			PrintStream out = new PrintStream(INI);

			for (String key : settings.keySet()) {
				if (!key.equals("num_streams") && !key.contains("stream_")) {
					String val = settings.get(key);

					out.println(key + "->" + val);
				}
			}

			int i = 0;
			for (RadioStation rs : RecordingMaster.stations) {
				if (rs != null) {
					out.println("stream_" + i + "->" + rs.url);
					i++;
				}
			}
			out.println("num_streams->" + i);
			out.close();
		} catch (Exception e) {
			try {
				if (INI.createNewFile()) {
					save();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

		File vbs = new File(
				System.getenv("appdata") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\audiras.vbs");

		if (getBootRec()) {

			if (vbs.exists()) {
				return;
			}

			try {
				PrintWriter pw = new PrintWriter(vbs);

				pw.println("Set ws = CreateObject(\"Wscript.Shell\")");
				pw.println("ws.run \"cmd /c " + BAT.getAbsolutePath() + "\",vbhide");

				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			if (!vbs.exists()) {
				return;
			}
			vbs.delete();
		}
	}

	public static String getStreamDir() {
		if (!settings.containsKey("target_dir")) {
			settings.put("target_dir", THIS_DIR.getAbsolutePath() + "/Recordings");
		}

		return settings.get("target_dir");
	}

	public static int getNumStreams() {
		return Integer.valueOf(settings.get("num_streams"));
	}

	public static String getStreamUrlById(int id) {
		return settings.get("stream_" + id);
	}

	public static int getMaxStreams() {
		if (!settings.containsKey("max_threads")) {
			settings.put("max_threads", Integer.toString(Runtime.getRuntime().availableProcessors() * 2));
		}
		return Integer.valueOf(settings.get("max_threads"));
	}

	public static String getLang() {
		if (!settings.containsKey("language")) {
			settings.put("language", "eng");
		}
		return settings.get("language");
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
		return  f;
	}

	public static void setBlockMode(int selectedIndex) {
		settings.put("block_cond", String.valueOf(selectedIndex));
	}

	public static void setBlockMax(float num) {
		settings.put("block_max", String.valueOf(num));
	}

	public static void setBootRec(boolean selected) {
		settings.put("boot_rec", (selected) ? "1" : "0");

	}

	public static boolean getBootRec() {
		if (!settings.containsKey("boot_rec")) {
			settings.put("boot_rec", "0");
		}
		return settings.get("boot_rec").equals("1");
	}

	public static boolean getShowWin() {
		if (!settings.containsKey("shw_win")) {
			settings.put("shw_win", "1");
		}
		return settings.get("shw_win").equals("1");
	}

	public static void setShowWin(boolean selected) {
		settings.put("shw_win", (selected) ? "1" : "0");

	}

	public static boolean getInstRec() {
		if (!settings.containsKey("inst_rec")) {
			settings.put("inst_rec", "0");
		}
		return settings.get("inst_rec").equals("1");
	}

	public static void setInstRec(boolean selected) {
		settings.put("inst_rec", (selected) ? "1" : "0");

	}

}
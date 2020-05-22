package com.msgprograms.audiras.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

public class Lang {

	static final File LANG = new File(FileConst.THIS_DIR + "/data/lang_" + Settings.getLang() + ".txt");
	private static HashMap<String, String> trans = new HashMap<>();

	public static void init() {
		String line = null;

		addDefaultStrings();

		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(LANG), "UTF8"));

			while ((line = bfr.readLine()) != null) {
				if (line.startsWith("//") || line.length() < 3) {
					continue;
				}
				trans.put(line.split("=")[0], line.split("=")[1]);
			}

			bfr.close();
		} catch (IOException e) {
			System.err.println("Something went wrong while reading the language file:");
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(line);
			System.exit(0);
		}

	}


	public static String get(String key) {
		String r = trans.get(key);
		if (r == null) {
			System.err.println(key + " not found!");
			return "MISSING";
		}
		return r;
	}

	private static void addDefaultStrings() {
		// errors
		trans.put("err", "Error");
		trans.put("err_astart", "Error while creating autostart file!");
		trans.put("err_noTrayIco", "Couldn't load the tray icon image!");
		trans.put("err_addTrayIcon", "Couldn't create the tray icon!");
		trans.put("err_invFloat", "Invalid float!");
		trans.put("err_invInt", "Invalid integer!");
		trans.put("err_conn", "Connection error!");
		trans.put("err_unsuppForm", "Format not supported!");
		trans.put("err_invMeta", "Invalid metadata!");
		trans.put("err_noMetaInt", "Can't record this stream! (No metainterval was send)");
		trans.put("err_iniFile", "Can't create settings save file!");
		trans.put("err_noStreamFile", "Stream list file not found!");
		trans.put("err_streamFileRead", "Error while reading the stream list!");
		trans.put("err_streamAdd", "Error while adding stream!");
		trans.put("err_invDir", "Can't use this directory!");
		trans.put("err_invLangFile", "Not a language file!");
		trans.put("err_createDir", "Couldn't create the directory %s!");
		trans.put("err_whereami", "Can't find current path name!");
		trans.put("err_invStream", "Can't record this stream!");

		// labels
		trans.put("lbl_streams", "Streams");
		trans.put("lbl_streamInfo", "Stream info");
		trans.put("lbl_streamList", "Stream list");
		trans.put("lbl_name", "Name");
		trans.put("lbl_genre", "Genre");
		trans.put("lbl_bitrate", "Bitrate");
		trans.put("lbl_status", "Status");
		trans.put("lbl_format", "Format");
		trans.put("lbl_curr", "Currently");

		// checkboxes
		trans.put("check_bootStart", "Start program on OS startup");
		trans.put("check_startWin", "Show window on startup");
		trans.put("check_startRec", "Start recording on program startup");

		// dropdown choices
		trans.put("dd_numPer", "Number of songs / stream");
		trans.put("dd_sizePer", "Size of all songs / stream");
		trans.put("dd_numAll", "Number of all songs recorded");
		trans.put("dd_sizeAll", "Size of all songs recorded");

		// buttons
		trans.put("btn_masterStart", "Start all");
		trans.put("btn_masterStop", "Stop all");
		trans.put("btn_deleteStream", "Remove stream");
		trans.put("btn_startRec", "Start recording");
		trans.put("btn_stopRec", "Stop recording");
		trans.put("btn_save", "Save");
		trans.put("btn_recordStream", "Record Stream");
		trans.put("btn_addNewStream", "Add new");
		trans.put("btn_alreadyThere", "Already on list!");
		trans.put("btn_dirchange", "Change recording directory");
		trans.put("btn_langchange", "Change language");

		// status
		trans.put("stat_idle", "Idle");
		trans.put("stat_recording", "Recording");
		trans.put("stat_full", "Limit hit!");

		// dialog
		trans.put("msg", "Info");
		trans.put("diag_reqStreamURL", "Enter new stream's URL");
		trans.put("diag_addSucc", "Stream successfully added");
		trans.put("diag_requireRestart", "Restart required, closing program.");

		// tabs in window
		trans.put("tab_record", "Record");
		trans.put("tab_list", "Browse");
		trans.put("tab_settings", "Settings");

		// tray icon
		trans.put("ti_open", "Open window");
		trans.put("ti_exit", "Exit Audiras");

	}
}

package com.msgprograms.audiras.streamlogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.msgprograms.audiras.settings.FileConst;
import com.msgprograms.audiras.settings.Lang;
import com.msgprograms.audiras.settings.Settings;

public class StationList {

	public static ArrayList<RadioStation> stations = new ArrayList<>();

	// load streams saved in file FileConst.LIST_FILE
	public static void init() {

		if (!FileConst.LIST_FILE.exists()) {
			JOptionPane.showMessageDialog(null, Lang.get("err_noStreamFile"), Lang.get("err"),
					JOptionPane.ERROR_MESSAGE);
		} else {

			try {
				BufferedReader br = new BufferedReader(new FileReader(FileConst.LIST_FILE));
				String line;

				int i = 0;
				while ((line = br.readLine()) != null) {
					if (line.equals("")) {
						continue;
					}
					stations.add(new RadioStation(line, i));
					i++;

				}

				br.close();

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Lang.get("err_streamFileRead"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}

	}

	// test if the url given is a stream that can be recorded from
	public static boolean isValidStream(String url) {
		StreamMeta meta = new StreamMeta(url);
		if (meta.error != null) {
			return false;
		} else {
			return true;
		}
	}

	// add inputted station to list of radiostations and the file FileConst.LIST_FILE
	public static void add(String s) {
		try {
			Files.write(FileConst.LIST_FILE.toPath(), new String(s + "\n").getBytes(), StandardOpenOption.APPEND);
			RadioStation rs = new RadioStation(s, stations.size());
			stations.add(rs);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Lang.get("err_streamAdd"), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static RadioStation getStation(int idx) {
		return stations.get(idx);
	}

}

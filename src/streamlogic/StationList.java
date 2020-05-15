package streamlogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import settings.Settings;

public class StationList {

	public static ArrayList<RadioStation> stations = new ArrayList<>();
	private static final File STREAMFILE = new File(Settings.THIS_DIR + "/data/streams.txt");

	// load streams saved in file STREAMFILE
	public static void init() {

		if (!STREAMFILE.exists()) {
			JOptionPane.showMessageDialog(null,
					"Stream list file not found!\nPlease download the streamfile and try again\nProgram will exit.",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0); // could open without anything, but why? stream recorder is useless w/o
							// streams...
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(STREAMFILE));
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
			System.err.println("Something went wrong while loading the stream list:");
			e.printStackTrace();
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

	// add inputted station to list of radiostations and the file STREAMFILE
	public static void add(String s) {
		try {
			Files.write(STREAMFILE.toPath(), new String(s + "\n").getBytes(), StandardOpenOption.APPEND);
			RadioStation rs = new RadioStation(s, stations.size());
			stations.add(rs);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while adding stream!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static RadioStation getStation(int idx) {
		return stations.get(idx);
	}

}

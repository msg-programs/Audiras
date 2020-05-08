package streamlogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import settings.Lang;
import settings.Settings;

public class StreamList {

	public static ArrayList<RadioStation> stations = new ArrayList<>();
	private static final File STREAMFILE = new File(Settings.THIS_DIR + "/data/streams.txt");

	// load streams saved in file STREAMFILE
	public static void init() {

		if (!STREAMFILE.exists()) {
			JOptionPane.showMessageDialog(null, "Stream list file not found!","Error",JOptionPane.ERROR_MESSAGE);
			// TODO throw exception
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(STREAMFILE));
			String line;

			int i = 0;
			boolean a = false;

			while ((line = br.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}

				// loop init'd radiostations, if current stream isn't there, add it to the list
				for (RadioStation rs : RecordingMaster.stations) {
					if (rs != null && rs.url.equals(line)) {
						a = true;
					}
				}

				if (!a) {
					stations.add(new RadioStation(line, i));
					i++;
				}

				a = false;

			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// test if the url given is a stream that can be recorded from
	public static boolean test(String url) {
		// TODO rewrite

		return false;
	}

	// add inputted station to list of radiostations and the file STREAMFILE
	public static RadioStation add(String s) {
		try {
			Files.write(STREAMFILE.toPath(), new String("\n" + s).getBytes(), StandardOpenOption.APPEND);
			RadioStation rs = new RadioStation(s, stations.size());
			stations.add(rs);
			return rs;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while adding stream!");
			return null;
		}
	}

}

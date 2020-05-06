package settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Lang {

	private static final File LANG = new File(Settings.THIS_DIR + "/data/" + Settings.getLang() + ".txt");
	private static HashMap<String, String> trans = new HashMap<>();

	public static void init() {
		String line = null;
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(LANG));

			while ((line = bfr.readLine()) != null) {
				if (line.startsWith("//")) {
					continue;
				}
				trans.put(line.split("=")[0], line.split("=")[1]);
			}

			bfr.close();
		} catch (IOException e) {
			System.err.println("Something went wrong while reading the language file:");
			e.printStackTrace();
		}

	}

	public static String get(String key) {
		String r = trans.get(key);
		if (r == null)
			System.err.println("Can't find " + key + " in translation!");
		return r;
	}
}

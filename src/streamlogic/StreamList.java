package streamlogic;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import settings.Lang;

public class StreamList {

	public static ArrayList<RadioStation> stations = new ArrayList<>();
	private static final File SRC = new File(System.getenv("APPDATA") + "/Audiras/streams.txt");

	public static void init() {

		if (!SRC.exists()) {
			JOptionPane.showMessageDialog(null, Lang.get("srms_not_fnd"));
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(SRC));
			String line;

			int i = 0;
			boolean a = false;

			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					for (RadioStation rs : RecordingMaster.stations) {

						if (rs != null) {
//						System.out.println(rs.url);
							if (rs.url.equals(line)) {
								a = true;
							}
						}
//					System.out.println(a);
					}

					if (!a) {
						stations.add(new RadioStation(line, i));
						i++;
					}

					a = false;
				}
			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean test(String url) {
//		System.out.println("3");
		try {
			URLConnection conn = new URL(url + ".xspf").openConnection();
//			System.out.println("4");
			InputStream is = conn.getInputStream();
//			System.out.println("5");
			int i;
			byte[] b = new byte[4096];
			is.readNBytes(b, 0, b.length);
			String a = new String(b);

//			System.out.println(new String(b));

			if (!a.contains("title") || !(a.length() > 5)) {
				JOptionPane.showMessageDialog(null, Lang.get("no_mta"));
				i = 1 / 0;
			}

			if (!a.contains("mpeg")) {
				JOptionPane.showMessageDialog(null, Lang.get("not_supp"));
				i = 1 / 0;
			}

//			System.out.println("10");

		} catch (Exception e) {
//			e.printStackTrace();
			JOptionPane.showMessageDialog(null, Lang.get("not_vld"));
			return false;
		}
//		System.out.println("11");
		return true;
	}

	public static RadioStation add(String s) {
		try {
			Files.write(SRC.toPath(), new String("\n" + s).getBytes(), StandardOpenOption.APPEND);
			RadioStation rs = new RadioStation(s, stations.size());
			stations.add(rs);
			return rs;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, Lang.get("err_add"));
			return null;
		}
	}

}

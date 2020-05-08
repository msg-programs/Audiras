package streamlogic;

import java.io.File;
import java.util.ArrayList;

import settings.Settings;

public class RecordingMaster {

	public static ArrayList<RadioStation> stations = new ArrayList<>();

	public static ArrayList<Recording> files = new ArrayList<>();

	public static void init() {

		for (int i = 0; i < Settings.getNumStreams(); i++) {
			int id = Settings.getStreamIDByIdx(i);
			if (id != -1) {				
				stations.add(StationList.getStation(id));
			}
		}

	}

	public static void toggle(int i) {
		RadioStation rs = stations.get(i);

		if (rs.recording) {
			rs.stopRec();
		} else {
			rs.startRec();
		}
	}

	public static void setAllOn() {
		for (RadioStation rs : stations) {
			if (rs != null && !rs.lock) {
				rs.startRec();
			}
		}
	}

	public static void setAllOff() {
		for (RadioStation rs : stations) {
			if (rs != null) {
				rs.stopRec();
			}
		}
	}

	public static synchronized void addRecording(int i, File f) {
		if (!f.getName().equals("tmp.mp3")) {
			files.add(new Recording(i, f));
		}
	}

	public static synchronized boolean checkFull(int id) {

		boolean full = false;

		switch (Settings.getBlockCond()) {
		case Settings.NUM_ALL:

			System.out.println("Settings.NUM_ALL | # of files: " + files.size() + "\nNumber of max. files: "
					+ Settings.getBlockMax() / 1000);
			System.out.println("Should everything be stopped? " + (files.size() >= Settings.getBlockMax() / 1000));
			full = files.size() - 1 >= Settings.getBlockMax() / 1000;

			if (full) {
				for (RadioStation rs : stations) {
					if (rs != null) {
						rs.lock = true;
						rs.stopRec();
					}
				}
			}
			break;

		case Settings.NUM_PER:
			int count = 0;

			for (Recording r : files) {

				if (r.rsID == id) {
					count++;
				}
			}
			System.out.println("Settings.NUM_PER | # of files in Stream " + id + ": " + count
					+ "\nNumber of max. files: " + Settings.getBlockMax() / 1000);
			System.out.println("Should this stream be stopped? " + (count >= Settings.getBlockMax() / 1000));

			full = count - 1 >= Settings.getBlockMax() / 1000f;

			if (full && stations[id - 1] != null) {
				stations[id - 1].lock = true;
				stations[id - 1].stopRec();

			}
			break;

		case Settings.SIZE_ALL:
			long size = 0;
			for (Recording r : files) {

				File f = r.file;
				System.out.println("Current file: " + f.getName());
				System.out.println("File size in  B: " + f.length());
				System.out.println("File size in MB: " + (f.length() / 1000f / 1000f));
				size += (f.length() / 1000f / 1000f);
			}

			System.out.println(
					"Settings.SZE_ALL | size of all files: " + size + "\nMax. file size: " + Settings.getBlockMax());
			System.out.println("Should everything be stopped? " + (Settings.getBlockMax() <= size));

			full = Settings.getBlockMax() <= size;
			if (full) {
				for (RadioStation rs : stations) {
					if (rs != null) {
						rs.lock = true;
						rs.stopRec();
					}
				}
			}
			break;
		case Settings.SIZE_PER:
			long size2 = 0;
			for (Recording r : files) {
				if (r.rsID == id) {
					File f2 = r.file;
					System.out.println("Current file: " + f2.getName());
					System.out.println("File size in  B: " + f2.length());
					System.out.println("File size in MB: " + (f2.length() / 1000f / 1000f));
					size2 += (f2.length() / 1000f / 1000f);
				}
			}

			System.out.println(
					"Settings.SZE_PER | size of files: " + size2 + "\nMax. file size: " + Settings.getBlockMax());
			System.out.println("Should this stream be stopped? " + (Settings.getBlockMax() <= size2));

			full = Settings.getBlockMax() <= size2;

			if (full && stations[id - 1] != null) {
				stations[id - 1].lock = true;
				stations[id - 1].stopRec();

			}

			break;
		}

		return full;
	}

	public static void addStation(RadioStation rs) {
		stations.add(rs);
		
	}

	public static RadioStation getStation(int idx) {
		return stations.get(idx);
	}

	public static void remove(RadioStation rs) {
		stations.remove(rs);
		
	}

}

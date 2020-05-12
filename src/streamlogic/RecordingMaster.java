package streamlogic;

import java.util.ArrayList;

import settings.Settings;

public class RecordingMaster {

	public static ArrayList<RadioStation> stations = new ArrayList<>();

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
			if (!rs.lock) {
				rs.startRec();
			}
		}
	}

	public static void setAllOff() {
		for (RadioStation rs : stations) {
			rs.stopRec();
		}
	}

	public static boolean isFull() {

		if (Settings.getBlockCond() == Settings.NUM_ALL) {
			int count = 0;
			for (RadioStation rs : stations) {
				System.out.println("Adding list of station " + rs.meta.name + ": " + rs.records.size());
				count += rs.records.size();
			}
			System.out.println(">= Comparing size of all (" + count + ") to block max (" + Settings.getBlockMax()+")");
			return 	count >= (int) Settings.getBlockMax();
		}
		
		// size_all
		double size =0;
		for (RadioStation rs : stations) {
			System.out.println("Adding filesize of station " + rs.meta.name + ": " + rs.getRecSize());
			size += rs.getRecSize();
		}
		System.out.println(">= Comparing size of all (" + size + ") to block max (" + Settings.getBlockMax()+")");
		return size >= Settings.getBlockMax();
	}

	public static void addStation(RadioStation rs) {
		stations.add(rs);

	}

	public static RadioStation getStation(int idx) {
		return stations.get(idx);
	}

	public static void remove(RadioStation rs) {
		stations.remove(rs);

		// TODO ask for record deletion
	}

}

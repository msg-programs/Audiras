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

		if (rs.isRecording) {
			rs.stopRec();
		} else {
			rs.startRec();
		}
	}

	public static void setAllOn() {
		for (RadioStation rs : stations) {
			rs.startRec();
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
				count += rs.records.size();
			}
			return count >= (int) Settings.getBlockMax();
		}

		// size_all
		double size = 0;
		for (RadioStation rs : stations) {
			size += rs.getRecSize();
		}
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
	}

	public static void recalcAll() {
		for (RadioStation rs: stations)  {
			rs.doDirScan();
			rs.recalcFull();
		}
	}
	
	public static void resetStreamDirs() {
		for (RadioStation rs: stations)  {
			rs.resetStreamDir();
		}
	}

}

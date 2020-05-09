package streamlogic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import settings.Settings;

public class RadioStation {

	public StreamMeta meta;

	public boolean recording = false;
	public boolean err = false;
	public boolean lock = false;

	public ArrayList<File> records = new ArrayList<>();
	
	public File streamdir = null;

	public int id;

	public RadioStation(String url, int id) {

		this.meta = new StreamMeta(url);
		this.id = id;

		if (meta.error != null) {
			err = true;
		}

		streamdir = new File(Settings.getStreamDir() + "\\" + meta.name);
		if (streamdir.exists()) {
			for (File f : streamdir.listFiles()) {
				if (f.getName().equals("tmp.mp3")) {
					continue;
				}
				records.add(f);
			}
		}

		lock = isFull();
	}

	boolean isFull() {
		// num_all or size_all
		if (Settings.getBlockCond() >= 2) {
			return RecordingMaster.isFull();
		}

		if (Settings.getBlockCond() == Settings.NUM_PER) {
			return records.size() >= (int) Settings.getBlockMax();
		}

		// size_per
		return getRecSize() >= Settings.getBlockMax();

	}

	public long getRecSize() {
		try {
			long size = Files.size(streamdir.toPath());
			size /= 1000l; // kb
			size /= 1000l; // mb
			size /= 1000l; // gb
			return size;
		} catch (IOException e) {
			System.err.println("Something went wrong while trying to calculate the dir size");
			e.printStackTrace();
			return Long.MAX_VALUE;
		}
	}

	public void startRec() {
		if (!lock) {
			new RadioRecorder(this);
			recording = true;
		}
	}

	public void stopRec() {
		recording = false;
		System.out.println("Stopped RStation " + meta.name);
	}

	public String getStatus() {

		if (err) {
			return "Connection error";
		}

		if (lock) {
			return "Hit the limit";
		}

		if (recording) {
			return "Recording";
		}

		if (!recording) {
			return "Idle";
		}

		return "???";
	}

	public String getButtonStatus() {

		if (!recording || err || lock) {
			return "Start recording";
		}

		if (recording) {
			return "Stop recording";
		}
		return null;
	}
}

package streamlogic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
		System.out.println(meta.name + " Checking if full...");
		if (Settings.getBlockCond() >= 2) {
			System.out.println("Redirecting to master");
			return RecordingMaster.isFull();
		}

		if (Settings.getBlockCond() == Settings.NUM_PER) {
			System.out.println(meta.name + " >= Comparing size of list (" + records.size() + ") to block max (" + Settings.getBlockMax()+")");
			return records.size() >= (int) Settings.getBlockMax();
		}

		System.out.println(meta.name + " Comparing size of files to block max (" + Settings.getBlockMax()+")");
		return getRecSize() >= Settings.getBlockMax();

	}

	public double getRecSize() {
			double size = 0;
			
			for (File f : records) {
				size += f.length();
			}
			
			size /= 1000d; // kb
			size /= 1000d; // mb
			size /= 1000d; // gb
			System.out.println("Files in "+ streamdir.toPath()+ " are " + size + " gb");
			return size;
	}

	public void startRec() {
		if (!lock && !recording) {
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

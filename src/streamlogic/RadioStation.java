package streamlogic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import settings.Lang;
import settings.Settings;

public class RadioStation {

	public StreamMeta meta;

	public boolean isRecording = false;
	public boolean hasError = false;
	public boolean isFull = false;

	public ArrayList<File> records = new ArrayList<>();

	public File streamdir = null;

	public int id;

	public RadioStation(String url, int id) {

		this.meta = new StreamMeta(url);
		this.id = id;

		if (meta.error != null) {
			hasError = true;
		}
		
		resetStreamDir();

		doDirScan();

		recalcFull();
	}

	public void doDirScan() {
		records.clear();
		if (streamdir.exists()) {
			for (File f : streamdir.listFiles()) {
				if (f.getName().equals("tmp.mp3")) {
					continue;
				}
				System.out.println(f);
				records.add(f);
			}
		}
	}

	boolean isFull() {
		// num_all or size_all
//		System.out.println(meta.name + " Checking if full...");
		if (Settings.getBlockCond() >= 2) {
//			System.out.println("Redirecting to master");
			return RecordingMaster.isFull();
		}

		if (Settings.getBlockCond() == Settings.NUM_PER) {
//			System.out.println(meta.name + " >= Comparing size of list (" + records.size() + ") to block max (" + Settings.getBlockMax()+")");
			return records.size() >= (int) Settings.getBlockMax();
		}

//		System.out.println(meta.name + " Comparing size of files to block max (" + Settings.getBlockMax()+")");
		return getRecSize() >= Settings.getBlockMax();

	}

	public void recalcFull() {
		isFull = isFull();
	}

	public double getRecSize() {
		double size = 0;

		for (File f : records) {
			size += f.length();
		}

		size /= 1000d; // kb
		size /= 1000d; // mb
		size /= 1000d; // gb
		return size;
	}

	public void startRec() {
		if (!isFull && !isRecording) {
			new RadioRecorder(this);
			isRecording = true;
		}
	}

	public void stopRec() {
		isRecording = false;
		System.out.println("Stopped RStation " + meta.name);
	}

	public String getStatus() {

		if (hasError) {
			return meta.error;
		}

		if (isFull) {
			return Lang.get("stat_full");
		}

		if (isRecording) {
			return Lang.get("stat_recording");
		}

		if (!isRecording) {
			return Lang.get("stat_idle");
		}

		return "???";
	}

	public String getButtonStatus() {

		if (!isRecording || hasError || isFull) {
			return Lang.get("btn_startRec");
		}

		if (isRecording) {
			return Lang.get("btn_stopRec");
		}
		return null;
	}

	public void resetStreamDir() {
		streamdir = new File(Settings.getStreamDir() + "\\" + meta.name);
		
	}
}

package com.msgprograms.audiras.streamlogic;

import java.io.File;
import java.util.ArrayList;

import com.msgprograms.audiras.settings.Lang;
import com.msgprograms.audiras.settings.Settings;

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
		if (Settings.getBlockCond() >= 2) {
			return RecordingMaster.isFull();
		}

		if (Settings.getBlockCond() == Settings.NUM_PER) {
			return records.size() >= (int) Settings.getBlockMax();
		}

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

package streamlogic;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import settings.Lang;
import settings.Settings;

public class RadioStation {

	public StreamMeta meta;

	public boolean recording = false;
	public boolean err = false;
	public boolean lock = false;
	public boolean first = true;

	public int id;

	public RadioStation(String url, int id) {

		this.meta = new StreamMeta(url);
		this.id = id;

		if(meta.error != null) {
			err = true;
		}

		File dir = new File(Settings.getStreamDir() + "\\" + meta.name);
		if (dir.exists()) {
			for (File f : dir.listFiles()) {
				RecordingMaster.addRecording(this.id, f);
				System.out.println(f.getName());
			}
			if (RecordingMaster.checkFull(this.id)) {
				this.lock = true;
			}
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

	public String getStatusB() {

		if (!recording || err || lock) {
			return "Start recording";
		}

		if (recording) {
			return "Stop recording";
		}
		return null;
	}
}

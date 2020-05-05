package streamlogic;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import settings.Lang;
import settings.Settings;

public class RadioStation {

	public String name;
	public String genre;
	public String bitrate;
	public String url;
	public String format;

	public boolean recording = false;
	public boolean err = false;
	public boolean lock = false;
	public boolean first = true;

	public int id;

	public RadioStation(String url, int id) {

		this.url = url;
		this.id = id;

		URLConnection toData;
		try {
			toData = new URL(url + ".xspf").openConnection();
			toData.connect();

			InputStream is = toData.getInputStream();

			byte[] b = new byte[4096];
			is.readNBytes(b, 0, b.length);
			String a = new String(b);

			is.close();
			toData = new URL(url + ".xspf").openConnection();
			toData.connect();

			is = toData.getInputStream();

			if (!a.contains("title") || !(a.length() > 5)) {
				name = "";
				bitrate = "";
				genre = "";
				format = "";
				err = true;
			} else {

				String raw = new String(is.readAllBytes());

				raw = raw.substring(raw.indexOf("<annotation>") + "<annotation>".length(),
						raw.indexOf("</annotation>"));

				String[] lines = raw.split("\n");

				for (String line : lines) {
					if (line.contains("Bitrate")) {
						bitrate = line.split(":")[1].trim() + " kb/s";
					}
					if (line.contains("Stream Title")) {
						name = line.split(":")[1].trim();
					}
					if (line.contains("Stream Genre")) {
						genre = line.split(":")[1].trim();
					}
					if (line.contains("Content Type")) {
						format = line.split(":")[1].trim();
					}
				}
			}

		} catch (Exception e) {
			err = true;
//			System.exit(0);
			e.printStackTrace();
		}

		File dir = new File(Settings.getStreamDir() + "\\" + this.name);
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
		System.out.println("Stopped RStation " + this.name);
	}

	public String getStatus() {

		if (err) {
			return Lang.get("conn_err");
		}

		if (lock) {
			return Lang.get("lmt_hit");
		}

		if (recording) {
			return Lang.get("rec_on");
		}

		if (!recording) {
			return Lang.get("rec_off");
		}

		return "???";
	}

	public String getStatusB() {

		if (!recording || err || lock) {
			return Lang.get("rec_tog_on");
		}

		if (recording) {
			return Lang.get("rec_tog_off");
		}
		return null;
	}
}

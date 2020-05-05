import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.Mp3File;

public class RadioRecorder extends Thread {

	private InputStream music, data;
	private File tmpFile, dir;

	private OutputStream outStream;

	private static final int BUF_SZE = 1 * 1024;

	private byte[] bufferPre = new byte[BUF_SZE];
//	private byte[] bufferWrite = new byte[BUF_SZE];
//	private byte[] bufferNew = new byte[BUF_SZE];

	private URLConnection toData;
	private String prevC = "";
	private String prevT = "";

	private String c = "";
	private String t = "";
	private String infoUrl;

	private RadioStation rs;

	private boolean empty = true;

	public RadioRecorder(RadioStation rs1) {
		this.rs = rs1;

		String streamURL = rs.url;
		infoUrl = rs.url + ".xspf";

		dir = new File(Settings.getStreamDir() + "\\" + rs.name);

		System.out.println(dir.getAbsolutePath());

		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				JOptionPane.showMessageDialog(null, Lang.get("inv_dir"));
				System.exit(0);
			}
		}

		tmpFile = new File(dir.getAbsolutePath() + "\\tmp.mp3");

		try {
			URLConnection toMusic = new URL(streamURL).openConnection();
			toMusic.connect();

			toData = new URL(infoUrl).openConnection();
			toData.connect();

			music = toMusic.getInputStream();
			data = toData.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < BUF_SZE; i++) {
			bufferPre[i] = 0;
//			bufferWrite[i] = 0;
//			bufferNew[i] = 0;
		}

		this.start();
	}

	@Override
	public void run() {
		if (RecordingMaster.checkFull(rs.id)) {
			rs.lock = true;
			return;
		}

		try {
			int bytesRead;

			outStream = new FileOutputStream(tmpFile);

			while ((bytesRead = music.read(bufferPre)) != -1 && rs.recording) {
				outStream.write(bufferPre, 0, BUF_SZE);
//				long now = System.nanoTime();
//				System.arraycopy(bufferWrite, 0, bufferPre, 0, BUF_SZE);
//				System.arraycopy(bufferNew, 0, bufferWrite, 0, BUF_SZE);
//				System.out.println(System.nanoTime()-now);
//				System.exit(0);

				if (check()) {
					save();
				}
			}
//			System.out.println("Trying to close music in line 106");
			music.close();
//			System.out.println("Success");
//			System.out.println("Trying to close data in line 110");
			data.close();
//			System.out.println("Success");
//			System.out.println("Trying to close outStream in line 112");
			outStream.close();
//			System.out.println("Success");
		} catch (Exception e) {
			System.err.println("Error while writing in RRecorder for stream " + rs.name);
			e.printStackTrace();
			rs.recording = false;
		}
	}

	private void save() {

		try {

			if (empty) {
//				System.arraycopy(bufferWrite, 0, bufferPre, 0, BUF_SZE);
				empty = false;
			}

//			outStream = new FileOutputStream(tmpFile);
//			outStream.write(bufferNew);

			if (prevC.equals("") || prevT.equals("") || rs.first) {
				prevC = c;
				prevT = t;
//				outStream.close();
				rs.first = false;
				return;
			}

			ID3v1Tag tag = new ID3v1Tag();
			Mp3File file = new Mp3File(tmpFile);

			file.setId3v1Tag(tag);
			tag.setArtist(prevC);
			tag.setTitle(prevT);

			if (RecordingMaster.checkFull(rs.id)) {
				System.out.println("Check failed");
				rs.lock = true;
				rs.stopRec();
				return;
			} else {
				RecordingMaster.addRecording(rs.id,
						new File(dir.getAbsolutePath() + "\\" + prevC + " - " + prevT + ".mp3"));

				System.out.print("Saving: ");
				System.out.println(prevC + " - " + prevT + ".mp3");

				if (prevC.contains("\\")) {
					String[] parts = prevC.split("\\");
					prevC = parts[0] + parts[1];
				}

				if (prevC.contains("/")) {
					String[] parts = prevC.split("/");
					prevC = parts[0] + parts[1];
				}

				if (prevT.contains("\\")) {
					String[] parts = prevT.split("\\");
					prevT = parts[0] + parts[1];
				}

				if (prevT.contains("/")) {
					String[] parts = prevT.split("/");
					prevT = parts[0] + parts[1];
				}

				file.save(dir.getAbsolutePath() + "\\" + prevC + " - " + prevT + ".mp3");
			}

			prevC = c;
			prevT = t;
//			System.out.println("Trying to close outStream in line 162");
			outStream.close();
//			System.out.println("Success");
			outStream = new FileOutputStream(tmpFile);
			outStream.write(bufferPre);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean check() {
		try {
			toData = new URL(infoUrl).openConnection();
			toData.connect();
			data = null;
			data = toData.getInputStream();

		} catch (Exception ex) {
			ex.printStackTrace();
			c = "";
			t = "";
			rs.recording = false;
		}

		try {
			String info = new String(data.readAllBytes());

			int creator = info.indexOf("<creator>") + 9;
			int creatorEnd = info.indexOf("</creator>");

			int title = info.indexOf("<title>") + 7;
			int titleEnd = info.indexOf("</title>");

			t = info.substring(title, titleEnd).trim();

			if (!(creatorEnd >= 0) || !(creator >= 0)) {
				if (!t.contains("-")) {
					c = Lang.get("ukn_ctr");
				} else {
					String parts[] = t.split("-");
					c = parts[0].trim();
					t = parts[1].trim();
				}
			} else {

				c = info.substring(creator, creatorEnd).trim();
			}

		} catch (Exception e) {
			e.printStackTrace();
			c = "";
			t = "";
		}

		if (prevC.equals("") || prevT.equals("")) {
			prevC = c;
			prevT = t;
			return false;
		}

		if (!c.equals(prevC) && !t.equals(prevT)) {
			System.out.println("New song started!");
			return true;
		}
		return false;
	}

}

package streamlogic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.Mp3File;

public class RadioRecorder extends Thread {

	private InputStream music;
	private File tmpFile;

	private OutputStream outStream;

	// c = creator, t = title !!
	private String prevC;
	private String prevT;

	private String currC;
	private String currT;
	
	private RadioStation rs;

	private int blocksize = 0;

	private BufferQueue bufferQ;

	private boolean first = true;

	public RadioRecorder(RadioStation rs) {
		this.rs = rs;
		this.blocksize = rs.meta.metaInt;
		this.first = true;
		bufferQ = new BufferQueue(blocksize);

		if (!rs.streamdir.exists()) {
			if (!rs.streamdir.mkdirs()) {
				JOptionPane.showMessageDialog(null, "Couldn't create the directory " + rs.streamdir + "!", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

		tmpFile = new File(rs.streamdir.getAbsolutePath() + "\\tmp.mp3");

		try {
			URLConnection toMusic = new URL(rs.meta.url).openConnection();
			toMusic.setRequestProperty("Icy-MetaData", "1");
			toMusic.setRequestProperty("Connection", "close");
			toMusic.setRequestProperty("Accept", null);
			toMusic.connect();

			music = toMusic.getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}

		this.start();
	}

	@Override
	public void run() {
		if (rs.isFull()) {
			rs.lock = true;
			return;
		}

		try {

			outStream = new FileOutputStream(tmpFile);
			bufferQ.push(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			bufferQ.incIdxs();
			updateMeta(bufferQ.getMeta(BufferQueue.MID));
			System.out.println("Current Creator: " + currC);
			System.out.println("Current Track: " + currT);
			System.out.println("Last Creator: " + prevC);
			System.out.println("Last Track: " + prevT);
			bufferQ.push(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			bufferQ.incIdxs();
			updateMeta(bufferQ.getMeta(BufferQueue.MID));
			System.out.println("Current Creator: " + currC);
			System.out.println("Current Track: " + currT);
			System.out.println("Last Creator: " + prevC);
			System.out.println("Last Track: " + prevT);

			while (rs.recording) {
				bufferQ.push(music.readNBytes(blocksize));
				bufferQ.pushMeta(readMeta());
				bufferQ.incIdxs();
				updateMeta(bufferQ.getMeta(BufferQueue.MID));
				System.out.println("Current Creator: " + currC);
				System.out.println("Current Track: " + currT);
				System.out.println("Last Creator: " + prevC);
				System.out.println("Last Track: " + prevT);

				outStream.write(bufferQ.get(BufferQueue.WRITE));

				if (check()) {
					save();
				}
			}
			music.close();
			outStream.close();
		} catch (IOException e) {
			System.err.println("Error while writing in Recorder for stream " + rs.meta.name);
			e.printStackTrace();
			rs.recording = false;
		}
	}

	private byte[] readMeta() throws IOException {
		int len = music.read();
		return music.readNBytes(len * 16);
	}

	private void updateMeta(byte[] dat) {

		String str = "";
		try {
			str = new String(dat, "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (str.length() == 0) {
			System.out.println("Null string detected, assuming no change");
			prevC = currC;
			prevT = currT;
			return;
		}

		System.out.println("Non-null string detected, updating");

		String entry1 = str.split(";")[0];
		String value = entry1.split("=")[1];
		String[] info = value.split("-");
		prevC = currC;
		prevT = currT;
		currC = info[0].trim().replace("'", "");
		currT = info[1].trim().replace("'", "");
	}

	private void save() {

		try {
			bufferQ.pushMeta(new byte[] {});
			bufferQ.incIdxs();
			if (first) {
				first = false;
				System.out.println("First song detected, ignoring...");
			} else {

				if (rs.isFull()) {
					System.out.println("Recorder for " + rs.meta.name + " is full!");
					rs.lock = true;
					rs.stopRec();
					return;
				} else {

					System.out.print("[" + rs.meta.name + "] Saving: " + prevC + " - " + prevT + ".mp3\n");
					outStream.write(bufferQ.get(BufferQueue.READ));
					outStream.write(bufferQ.get(BufferQueue.MID));

					Mp3File mp3 = new Mp3File(tmpFile);
					ID3v1Tag tag = new ID3v1Tag();

					mp3.setId3v1Tag(tag);
					tag.setArtist(prevC);
					tag.setTitle(prevT);

					String safeC = prevC.replace("*", "#").replace("<", "[").replace(">", "]").replace(":", "")
							.replace("\"", "'").replace("\\", " ").replace("/", " ").replace("|", " ").replace("?", " ")
							.trim();
					String safeT = prevT.replace("*", "#").replace("<", "[").replace(">", "]").replace(":", "")
							.replace("\"", "'").replace("\\", " ").replace("/", " ").replace("|", " ").replace("?", " ")
							.trim();

					mp3.save(rs.streamdir.getAbsolutePath() + "\\" + safeC + " - " + safeT + ".mp3");
					rs.records.add(new File(rs.streamdir.getAbsolutePath() + "\\" + safeC + " - " + safeT + ".mp3"));

				}
			}
			System.out.println("Writing start of new song...");
			outStream.close();

			outStream = new FileOutputStream(tmpFile);
			outStream.write(bufferQ.get(BufferQueue.READ));
			outStream.write(bufferQ.get(BufferQueue.MID));
			outStream.write(bufferQ.get(BufferQueue.WRITE));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean check() {

		if (currC == null || currT == null) {
			return false;
		}

		if (!currC.equals(prevC) || !currT.equals(prevT)) {
			System.out.println("[" + rs.meta.name + "] New song started: " + currC + " - " + currT);
			return true;
		}
		return false;
	}

}

package streamlogic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.Mp3File;

import settings.Settings;

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
	
	private int idxTrailing = 0;
	private int idxLeading = 1;
	byte[][] mus;

	private boolean first = false;

	public RadioRecorder(RadioStation rs) {
		this.rs = rs;
		this.blocksize = rs.meta.metaInt;
		this.first = false; // better do it twice
		mus = new byte[2][blocksize];

		if (!rs.streamdir.exists()) {
			if (!rs.streamdir.mkdirs()) {
				JOptionPane.showMessageDialog(null, "Couldn't create the directory!", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

		tmpFile = new File(rs.streamdir.getAbsolutePath() + "\\tmp.mp3");

		try {
			URLConnection toMusic = new URL(rs.meta.url).openConnection();
			toMusic.setRequestProperty("Icy-MetaData", "1");
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

			mus[idxLeading] = music.readNBytes(blocksize);
			incIdxs();

			int len = music.read();
			byte[] dat = music.readNBytes(len * 4);

			updateMeta(dat);

			while (rs.recording) {
				mus[idxLeading] = music.readNBytes(blocksize);
				len = music.read();
				dat = music.readNBytes(len * 4);
				updateMeta(dat);

				outStream.write(mus[idxTrailing]);

				incIdxs();

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

	private void updateMeta(byte[] dat) {
		// oof this'll _byte_ my ass for sure
		// String(dat) is a map with entries in key=value seperated by ;
		// split(;)[0] for first entry (track data),
		// split(=) for value of entry,
		// split(-) for author - name

		String[] str = new String(dat).trim().split(";")[0].split("=")[1].split("-");
		prevC = currC;
		prevT = currT;
		currC = str[0].trim();
		currT = str[1].trim();
	}
	
	private void incIdxs() {
		this.idxLeading++;
		this.idxLeading %=2;
		this.idxTrailing = Math.abs(idxLeading-1);
	}

	private void save() {

		try {
			if (first) {
				prevC = currC;
				prevT = currT;
				first = false;
				return;
			}

			ID3v1Tag tag = new ID3v1Tag();
			Mp3File file = new Mp3File(tmpFile);

			file.setId3v1Tag(tag);
			tag.setArtist(prevC);
			tag.setTitle(prevT);

			if (rs.isFull()) {
				System.out.println("Check failed");
				rs.lock = true;
				rs.stopRec();
				return;
			} else {
				outStream.write(mus[idxLeading]);
				file.save(rs.streamdir.getAbsolutePath() + "\\" + prevC + " - " + prevT + ".mp3");
				rs.records.add(new File(rs.streamdir.getAbsolutePath() + "\\" + prevC + " - " + prevT + ".mp3"));

				System.out.print("Saving: " + prevC + " - " + prevT + ".mp3");
			}
			
			outStream.close();
			
			outStream = new FileOutputStream(tmpFile);
			outStream.write(mus[idxTrailing]);
			outStream.write(mus[idxLeading]);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean check() {
		if (currC == null || currT == null) {
			System.err.println("Something went _horribly_ wrong while fetching metadata");
			this.interrupt();
			rs.lock = true;
			rs.err = true;
		}

		if (!currC.equals(prevC) && !currT.equals(prevT)) {
			System.out.println("New song started!");
			return true;
		}
		return false;
	}

}

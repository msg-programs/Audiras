package com.msgprograms.audiras.streamlogic;

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
import com.msgprograms.audiras.settings.Lang;

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
				JOptionPane.showMessageDialog(null, String.format(Lang.get("err_createDir"), rs.streamdir),Lang.get("err"),
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
			rs.stopRec();
			rs.hasError = true;
			rs.meta.error = Lang.get("err_conn");
		}

		this.start();
	}

	@Override
	public void run() {
		rs.recalcFull();
		if (rs.isFull) {
			return;
		}

		try {

			outStream = new FileOutputStream(tmpFile);
			bufferQ.push(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			bufferQ.push(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());

			while (rs.isRecording) {
				bufferQ.push(music.readNBytes(blocksize));
				bufferQ.pushMeta(readMeta());
				updateMeta();

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
			rs.stopRec();
			rs.hasError = true;
			rs.meta.error = Lang.get("err_conn");
		}
	}

	private byte[] readMeta() throws IOException {
		int len = music.read();
		return music.readNBytes(len * 16);
	}

	private void updateMeta() {

		String strOld = "";
		String strNew = "";
		try {
			strOld = new String(bufferQ.getMeta(BufferQueue.WRITE), "UTF-8").trim();
			strNew = new String(bufferQ.getMeta(BufferQueue.MID), "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (strOld.length() == 0 || strNew.length() == 0) {
			return;
		}

		try {
			String entryOld = strOld.split(";")[0];
			String valueOld = entryOld.split("=")[1];
			String[] infoOld = valueOld.split("-");

			String entryNew = strNew.split(";")[0];
			String valueNew = entryNew.split("=")[1];
			String[] infoNew = valueNew.split(" - ");

			prevC = infoOld[0].trim().replace("'", "");
			prevT = infoOld[1].trim().replace("'", "");
			currC = infoNew[0].trim().replace("'", "");
			currT = infoNew[1].trim().replace("'", "");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(strOld);
			System.out.println(strNew);
			rs.stopRec();
			rs.hasError = true;
			
		}
	}

	private void save() {

		try {
			if (first) {
				first = false;
			} else {

				System.out.print("[" + rs.meta.name + "] Saving: " + prevC + " - " + prevT + ".mp3\n");
				outStream.write(bufferQ.get(BufferQueue.MID));
				outStream.write(bufferQ.get(BufferQueue.READ));

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

				if (!new File(rs.streamdir.getAbsolutePath() + "\\" + safeC + " - " + safeT + ".mp3").exists()) {
					mp3.save(rs.streamdir.getAbsolutePath() + "\\" + safeC + " - " + safeT + ".mp3");
					rs.records.add(new File(rs.streamdir.getAbsolutePath() + "\\" + safeC + " - " + safeT + ".mp3"));
				}

			}

			outStream.close();

			outStream = new FileOutputStream(tmpFile);
//			outStream.write(bufferQ.get(BufferQueue.READ));

			rs.recalcFull();
			if (rs.isFull) {
//				System.out.println("Recorder for " + rs.meta.name + " is full!");
				rs.stopRec();
				return;
			}

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

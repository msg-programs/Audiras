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

	private boolean first = false;
	private static final String AD = "AD";

	public RadioRecorder(RadioStation rs) {
		this.rs = rs;
		this.blocksize = rs.meta.metaInt;
		this.first = true;

		bufferQ = new BufferQueue(0,1);

		if (!rs.streamdir.exists()) {
			if (!rs.streamdir.mkdirs()) {
				JOptionPane.showMessageDialog(null, String.format(Lang.get("err_createDir"), rs.streamdir),
						Lang.get("err"), JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

		tmpFile = new File(rs.streamdir.getAbsolutePath() + "\\tmp.mp3");

		try {
			URLConnection toMusic = new URL(rs.meta.url).openConnection();
			toMusic.setRequestProperty("Icy-MetaData", "1");
			toMusic.setRequestProperty("Connection", "close");
			toMusic.connect();

			music = toMusic.getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
			rs.stopRec();
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
			bufferQ.pushBuffer(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			bufferQ.pushBuffer(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			bufferQ.pushBuffer(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			bufferQ.pushBuffer(music.readNBytes(blocksize));
			bufferQ.pushMeta(readMeta());
			
			while (rs.isRecording) {
				bufferQ.pushBuffer(music.readNBytes(blocksize));
				bufferQ.pushMeta(readMeta());
				updateMeta();

				outStream.write(bufferQ.pop());

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
			strOld = new String(bufferQ.getMetaLo(), "UTF-8").trim();
			strNew = new String(bufferQ.getMetaHi(), "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (strOld.length() == 0 || strNew.length() == 0) {
			return;
		}
		
		// ads have the StreamTitle field set to ''
		// if this fails because of an empty string, assume current song is ad
		// and set strings to something we can check for later
		try {
			
			String entryOld = strOld.split(";")[0];
			String valueOld = entryOld.split("=")[1];
			String[] infoOld = valueOld.split("-");
			prevC = infoOld[0].trim().replace("'", "");
			// join titles with dashes back together
			prevT = String.join("-", infoOld).substring(valueOld.indexOf('-')+1).trim().replace("'", "");
		} catch (ArrayIndexOutOfBoundsException e) {
			prevC = AD;
			prevT = AD;
		}

		try {

			String entryNew = strNew.split(";")[0];
			String valueNew = entryNew.split("=")[1];
			String[] infoNew = valueNew.split("-");

			currC = infoNew[0].trim().replace("'", "");
			currT = String.join("-", infoNew).substring(valueNew.indexOf('-')+1).trim().replace("'", "");

		} catch (ArrayIndexOutOfBoundsException e) {
			currC = AD;
			currT = AD;
		}
	}

	private void save() {

		try {
			if (first) {
				first = false;
			} else if (!prevC.equals(AD) && !prevT.equals(AD)) {
				outStream.write(bufferQ.getBuffer(0));
				outStream.write(bufferQ.getBuffer(1));
				outStream.write(bufferQ.getBuffer(2));
				outStream.write(bufferQ.getBuffer(3));
				
				Mp3File mp3 = new Mp3File(tmpFile);

//				mp3.getLengthInSeconds() is buggy for some reason
				if (tmpFile.length() * 8 / 1000 / rs.meta.bitrate >= 30) {
					System.out.print("[" + rs.meta.name + "] Saving: " + prevC + " - " + prevT + ".mp3\n");

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

					File targetFile = new File(rs.streamdir.getAbsolutePath() + "\\" + safeC + " - " + safeT + ".mp3");

					if (!targetFile.exists()) {
						try {
							mp3.save(targetFile.getAbsolutePath());
							rs.records.add(targetFile);
						} catch (Exception e) {
							targetFile.delete();
							rs.restart();
							e.printStackTrace();
							return;
						}
					}
				}
			}

			outStream.close();
			outStream = new FileOutputStream(tmpFile);

			rs.recalcFull();
			if (rs.isFull) {
				System.out.println("Recorder for " + rs.meta.name + " is full!");
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

package com.msgprograms.audiras.streamlogic;

public class BufferQueue {

	private static final int BUFNUM = 4;
	
	public static final int READ = 0;
	public static final int MID_HI = 1;
	public static final int MID_LO = 2;
	public static final int WRITE = 3;

	private byte[][] buffers;
	private byte[][] meta;
	private byte[] lastNonNullMeta;
	private int size;

	public BufferQueue(int blocksize) {
		buffers = new byte[BUFNUM][blocksize];
		meta = new byte[BUFNUM][256 * 16];
		size = blocksize;
		lastNonNullMeta = new byte[256 * 16];
	}

	public void push(byte[] bytes) {
		System.arraycopy(buffers[MID_LO], 0, buffers[WRITE], 0, size);
		System.arraycopy(buffers[MID_HI], 0, buffers[MID_LO], 0, size);
		System.arraycopy(buffers[READ], 0, buffers[MID_HI], 0, size);
		System.arraycopy(bytes, 0, buffers[READ], 0, size);
	}

	public void pushMeta(byte[] bytes) {
		System.arraycopy(meta[MID_LO], 0, meta[WRITE], 0, meta[MID_LO].length);
		System.arraycopy(meta[MID_HI], 0, meta[MID_LO], 0, meta[READ].length);
		System.arraycopy(meta[READ], 0, meta[MID_HI], 0, meta[READ].length);
		if (bytes.length == 0) {
			System.arraycopy(lastNonNullMeta, 0, meta[READ], 0, lastNonNullMeta.length);
		} else {
			System.arraycopy(bytes, 0, meta[READ], 0, bytes.length);
			System.arraycopy(bytes, 0, lastNonNullMeta, 0, bytes.length);
		}
	}

	public byte[] get(int what) {
		return buffers[what];
	}

	public byte[] getMeta(int what) {
		return meta[what];
	}

}

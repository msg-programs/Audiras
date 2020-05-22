package com.msgprograms.audiras.streamlogic;

public class BufferQueue {

	public static final int READ = 0;
	public static final int MID = 1;
	public static final int WRITE = 2;

	private byte[][] buffers;
	private byte[][] meta;
	private byte[] lastNonNullMeta;
	private int size;

	public BufferQueue(int blocksize) {
		buffers = new byte[3][blocksize];
		meta = new byte[3][256 * 16];
		size = blocksize;
		lastNonNullMeta = new byte[256 * 16];
	}

	public void push(byte[] bytes) {
		System.arraycopy(buffers[MID], 0, buffers[WRITE], 0, size);
		System.arraycopy(buffers[READ], 0, buffers[MID], 0, size);
		System.arraycopy(bytes, 0, buffers[READ], 0, size);
	}

	public void pushMeta(byte[] bytes) {

		System.arraycopy(meta[MID], 0, meta[WRITE], 0, meta[MID].length);
		System.arraycopy(meta[READ], 0, meta[MID], 0, meta[READ].length);
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

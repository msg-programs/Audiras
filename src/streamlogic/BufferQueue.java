package streamlogic;

import java.io.InputStream;

public class BufferQueue {

	public static int inIdx = 0;
	public static int bufIdx = 1;
	public static int outIdx = 2;

	public static final int READ = 2;
	public static final int MID = 1;
	public static final int WRITE = 0;

	private byte[][] buffers;
	private byte[][] meta;
	private byte[] lastNonNullMeta;
	private int size;

	public BufferQueue(int blocksize) {
		buffers = new byte[3][blocksize];
		meta = new byte[3][256 * 16];
		size = blocksize;
		lastNonNullMeta = new byte[256*16];
	}

	public void push(byte[] bytes) {
		System.arraycopy(bytes, 0, buffers[inIdx], 0, size);
	}

	public void pushMeta(byte[] bytes) {
		if (bytes.length == 0) {
			System.arraycopy(lastNonNullMeta, 0, meta[inIdx], 0, lastNonNullMeta.length);
		} else {
//			System.out.println("Pushing metadata: \"" + new String(bytes) + "\"");
			System.arraycopy(bytes, 0, meta[inIdx], 0, bytes.length);
			System.arraycopy(bytes, 0, lastNonNullMeta, 0, bytes.length);
		}
	}

	public byte[] get(int what) {
		if (what == READ) {
			return buffers[inIdx];
		}
		if (what == MID) {
			return buffers[bufIdx];
		}
		if (what == WRITE) {
			return buffers[outIdx];
		}
		return null;
	}

	public byte[] getMeta(int what) {
//		System.out.println("Metastack is (RMW): \n\t" + new String(meta[inIdx]) + ", \n\t" + new String(meta[bufIdx])
//				+ ", \n\t" + new String(meta[outIdx]) + ",");

		if (what == READ) {
			return meta[inIdx];
		}
		if (what == MID) {
			return meta[bufIdx];
		}
		if (what == WRITE) {
			return meta[outIdx];
		}
		return null;
	}

	public void incIdxs() {
		inIdx = (inIdx + 1) % 3;
		bufIdx = (bufIdx + 1) % 3;
		outIdx = (outIdx + 1) % 3;
	}

}

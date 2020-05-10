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
	private int size;

	public BufferQueue(int blocksize) {
		buffers = new byte[3][blocksize];
		meta = new byte[3][256 * 16];
		size = blocksize;
	}

	public void push(byte[] bytes) {
		System.arraycopy(bytes, 0, buffers[inIdx], 0, size);
	}

	public void pushMeta(byte[] bytes) {
		System.out.println("Pushing metadata: \"" + new String(bytes)+"\"");
		System.arraycopy(bytes, 0, meta[inIdx], 0, bytes.length);
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
		System.out.println("Metastack is (RMW): \n\t" + new String(meta[inIdx]) + ", \n\t"+ new String(meta[bufIdx]) + ", \n\t"+ new String(meta[outIdx])+ ",");
		
		if (what == READ) {
			System.out.println("getting READ");
			return meta[inIdx];
		}
		if (what == MID) {
			System.out.println("getting MID");
			return meta[bufIdx];
		}
		if (what == WRITE) {
			System.out.println("getting WRITE");
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

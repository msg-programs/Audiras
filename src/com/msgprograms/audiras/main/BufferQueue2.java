package com.msgprograms.audiras.main;

import java.util.ArrayList;

public class BufferQueue2 {
	
	ArrayList<byte[]> buffers = new ArrayList<>();
	ArrayList<byte[]> meta = new ArrayList<>();
	
	private byte[] lastNonEmptyMeta = new byte[256*16];
	
	private int readLo, readHi;
	
	public BufferQueue2(int l,int h) {
		readHi = h;
		readLo = l;
	}
	
	public void pushBuffer(byte[] buf) {
		buffers.add(buf);
	}
	
	public void pushMeta(byte[] buf) {
		if (buf.length == 0) {
			meta.add(lastNonEmptyMeta);
		} else {
			meta.add(buf);
			System.arraycopy(buf, 0, lastNonEmptyMeta, 0, buf.length);
		}
	}
	
	public byte[] pop() {
		meta.remove(0);
		byte[] res = buffers.get(0);
		buffers.remove(0);
		return res;
	}
	
	public byte[] getBuffer(int which) {
		return buffers.get(which);
	}

	public byte[] getMetaHi() {
		return meta.get(this.readHi);
	}
	
	public byte[] getMetaLo() {
		return meta.get(this.readLo);
	}
	
}

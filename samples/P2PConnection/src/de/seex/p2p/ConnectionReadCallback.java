package de.seex.p2p;

public interface ConnectionReadCallback {
	void onDataRead(byte[] bytes);
}
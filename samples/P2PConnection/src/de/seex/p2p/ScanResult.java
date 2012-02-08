package de.seex.p2p;

public interface ScanResult {
	void onDeviceFound(P2PDevice device);
	
	void onScanFinished();
}

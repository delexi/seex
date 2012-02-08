package de.seex.p2p;

public interface ConnectionResult {
	void onConnectionEstablished(String deviceID);
	void onConnectionFailed(String deviceID, String reason);
}

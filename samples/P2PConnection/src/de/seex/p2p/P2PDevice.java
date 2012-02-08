package de.seex.p2p;

/**
 * Simple data class to represent the most important information about another
 * P2PDevice that a connection might be established to.
 * @author Alexander Baier
 */
public final class P2PDevice {
	
	/**
	 * A type of connection. Each class implementing the {@link P2PConnectionManager} interface
	 * should specify a type of connection in this enum.
	 */
	public enum ConType {
		BLUETOOTH, WIFI;
	}
	
	/**
	 * A set of possible states the connection to a device can be in.
	 */
	public enum ConStatus {
		NOT_CONNECTED, CONNECTING, CONNECTED, DECONNECTING;
	}

	/**
	 * An ID this device can be uniquely identified with like a mac address.
	 */
	private String id;
	
	/**
	 * The type of connection that can be used to connect to this device.
	 * @see ConType
	 */
	private ConType connectionType;
	
	/**
	 * The current status of the connection to this device.
	 */
	private ConStatus connectionStatus;

	/**
	 * A human-readable name for this device.
	 */
	private String name;
	
	public P2PDevice(String name, String id, ConType connectionType, ConStatus connectionStatus) {
		super();
		this.id = id;
		this.connectionType = connectionType;
		this.connectionStatus = connectionStatus;
		this.name = name;
	}

	public ConStatus getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(ConStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public String getId() {
		return id;
	}

	public ConType getConnectionType() {
		return connectionType;
	}

	public String getName() {
		return name;
	}
}

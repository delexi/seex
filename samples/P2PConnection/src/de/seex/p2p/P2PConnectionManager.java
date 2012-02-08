package de.seex.p2p;

import java.util.List;

public interface P2PConnectionManager {

	/**
	 * Sets up a Server listening on a separate Thread for incoming connections.
	 * Handles incoming connection requests and tries to establish a connection
	 * to them. On connection success or failure the respective methods are
	 * called on the specified {@link ConnectionResult callback}.
	 * 
	 * @param callback
	 *            the callback informing about results of the connection
	 *            process.
	 * @return
	 */
	boolean listenForIncomingConnections(ConnectionResult callback);

	/**
	 * Lists all devices that are close enough to establish a connection with.
	 * If no devices are found {@link java.util.Collections#EMPTY_LIST} is
	 * returned.
	 * 
	 * @return a list of devices that are close enough to establish a connection
	 *         with.
	 */
	boolean getAvailibleDevices(ScanResult callback);

	/**
	 * Tries to establish a connection to the specified devices. If this device
	 * has been successfully connected to another device listed in
	 * {@code devicesToConnectTo}
	 * {@link ConnectionResult#onConnectionEstablished(P2PDevice)} is called. If
	 * anything goes wrong so that no connection can be established
	 * {@link ConnectionResult#onConnectionFailed(P2PDevice, String)} is called.
	 * 
	 * @param devicesToConnectTo
	 *            the devices a connection is to be established to.
	 */
	boolean initiateConnection(List<P2PDevice> devicesToConnectTo, ConnectionResult callback);

	/**
	 * Registers a {@link ConnectionReadCallback callback} for the device
	 * belonging to the specified {@code deviceID}. The callback's
	 * {@link ConnectionReadCallback#onDataRead(byte[])} is called when the
	 * device specified by {@code deviceID} sends data over the connection with
	 * the device represented by this instance.
	 * 
	 * @param deviceID
	 *            the remote device.
	 * @param callback
	 *            the instance handling incoming data.
	 * @throws IllegalArgumentException
	 *             if either parameter is null, or if there is no connection
	 *             between this instance and the specified device.
	 */
	void registerConnectionReadCallbackFor(String deviceID, ConnectionReadCallback callback);

	/**
	 * Write specified {@code bytes} to the connection with the device specified
	 * by {@code deviceID}.
	 * 
	 * @param bytes
	 *            the data to send.
	 * @param deviceID
	 *            the remote device the bytes are sent to.
	 * @throws IllegalArgumentException
	 *             if there ist not connection between this instance and the
	 *             specified device.
	 */
	void writeTo(String deviceID, byte[] bytes);
}

package de.seex.p2p;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import de.seex.p2p.P2PDevice.ConStatus;
import de.seex.p2p.P2PDevice.ConType;

public class P2PBluetoothManager implements P2PConnectionManager {

	private static final UUID P2PBluetoothManager_UUID = UUID
	    .fromString("D90AED23-F3BF-6FB0-722DE90F78EBD7E5D90AED24-AEC9-9673-BFCAA59FC9E8B4A7");

	public enum State {
		NONE, SCANNING, CONNECTING, ACCEPTING;
	}

	private State currentState;

	private BluetoothAdapter btAdapter;
	private Activity activity;
	private Map<String, BluetoothDevice> lastScanResult;
	private Map<String, ConnectionReadCallback> registeredCallbacks;
	private Map<String, ConnectionHandlerThread> connectionHandlers;

	private BluetoothServerSocket btServerSocket;
	private Thread acceptThread;

	public P2PBluetoothManager(Activity activity) {
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		this.activity = activity;
		this.lastScanResult = new HashMap<String, BluetoothDevice>();
		this.registeredCallbacks = new HashMap<String, ConnectionReadCallback>();
		this.connectionHandlers = new HashMap<String, ConnectionHandlerThread>();
		this.currentState = State.NONE;
	}

	@Override
	public boolean getAvailibleDevices(final ScanResult callback) {
		if (this.currentState == State.SCANNING) {
			return false;
		}
		BroadcastReceiver bcReciever = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
					BluetoothDevice device = intent
					    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					callback.onDeviceFound(new P2PDevice(device.getName(), device.getAddress(),
					    ConType.BLUETOOTH, ConStatus.NOT_CONNECTED));
					lastScanResult.put(device.getAddress(), device);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
					btAdapter.cancelDiscovery();
					callback.onScanFinished();
				}
			}
		};
		activity.registerReceiver(bcReciever, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		// clean last scan's results
		lastScanResult.clear();
		btAdapter.startDiscovery();
		this.currentState = State.SCANNING;
		return true;
	}

	@Override
	public boolean initiateConnection(final List<P2PDevice> devicesToConnectTo,
	    final ConnectionResult callback) {
		if (this.currentState == State.CONNECTING) {
			return false;
		}
		Runnable connectRunnable = new Runnable() {
			@Override
			public void run() {
				for (P2PDevice p2pDev : devicesToConnectTo) {
					BluetoothDevice btDev = lastScanResult.get(p2pDev.getId());
					if (btDev != null) {
						BluetoothSocket btSocket = null;
						try {
							btSocket = btDev
							    .createRfcommSocketToServiceRecord(P2PBluetoothManager_UUID);
							btSocket.connect();
							callback.onConnectionEstablished(p2pDev.getId());
						} catch (IOException e) {
							try {
								btSocket.close();
							} catch (IOException e1) {
							}
							callback.onConnectionFailed(p2pDev.getId(), e.getMessage());
							e.printStackTrace();
						}
						handleConnection(btSocket);
					}
				}
			}
		};
		new Thread(connectRunnable).start();
		this.currentState = State.CONNECTING;
		return true;
	}

	@Override
	public boolean listenForIncomingConnections(final ConnectionResult callback) {
		if (btServerSocket == null) {
			initiateServerSocket();
		}
		if (this.currentState == State.ACCEPTING) {
			return false;
		}

		// Start Server Thread
		acceptThread = new Thread() {
			public void run() {
				BluetoothSocket socket = null;
				// listen for incoming connections, accept and handle them
				while (true) {
					try {
						socket = btServerSocket.accept();
						callback.onConnectionEstablished(socket.getRemoteDevice().getAddress());
					} catch (IOException e) {
						callback.onConnectionFailed(socket.getRemoteDevice().getAddress(),
						    e.getMessage());
						break;
					}
					if (socket != null) {
						handleConnection(socket);
					}
				}
			}
		};
		acceptThread.start();
		this.currentState = State.ACCEPTING;
		return true;
	}

	public boolean stopListeningForIncomingConnections() {
		if (this.currentState == State.ACCEPTING) {
			acceptThread.stop();
			acceptThread = null;
			this.currentState = State.NONE;
			return true;
		}
		return false;
	}

	private void initiateServerSocket() {
		BluetoothServerSocket tmp = null;
		try {
			tmp = btAdapter.listenUsingRfcommWithServiceRecord("P2PBluetoothManager",
			    P2PBluetoothManager_UUID);
		} catch (IOException e) {
		}
		btServerSocket = tmp;
	}

	private void handleConnection(BluetoothSocket btSocket) {
		ConnectionReadCallback callback = registeredCallbacks.get(btSocket.getRemoteDevice()
		    .getAddress());
		if (callback == null) {
			callback = new ConnectionReadCallback() {
				@Override
				public void onDataRead(byte[] bytes) { /* do nothing */
				}
			};
		}
		ConnectionHandlerThread handler = new ConnectionHandlerThread(btSocket, callback);
		connectionHandlers.put(btSocket.getRemoteDevice().getAddress(), handler);
		handler.start();
	}

	@Override
	public void registerConnectionReadCallbackFor(String deviceID, ConnectionReadCallback callback) {
		registeredCallbacks.put(deviceID, callback);
	}

	@Override
	public void writeTo(String deviceID, byte[] bytes) {
		ConnectionHandlerThread handler = connectionHandlers.get(deviceID);
		handler.write(bytes);
	}
}

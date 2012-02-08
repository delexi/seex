package de.seex.p2p;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class ConnectionHandlerThread extends Thread {
	private BluetoothSocket socketToHandle;
	private ConnectionReadCallback callback;
	private boolean stop;

	private final InputStream inputStream;
	private final OutputStream outputStream;

	public ConnectionHandlerThread(BluetoothSocket socketToHandle, ConnectionReadCallback callback) {
		super();
		this.socketToHandle = socketToHandle;
		this.callback = callback;
		this.stop = false;

		InputStream tmpIS = null;
		OutputStream tmpOS = null;
		try {
			tmpIS = socketToHandle.getInputStream();
			tmpOS = socketToHandle.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputStream = tmpIS;
		outputStream = tmpOS;
	}

	@Override
	public void run() {
		while (!stop) {
			byte[] buffer = new byte[1024];
			try {
				inputStream.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			callback.onDataRead(buffer);
		}
	}

	public void write(byte[] bytes) {
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cancel() {
		this.stop = true;
		try {
			socketToHandle.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

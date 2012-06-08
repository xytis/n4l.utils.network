/**
 * 
 */
package n4l.utils.network.client.tcp;

import java.io.DataInputStream;
import java.io.IOException;

import n4l.utils.network.server.Client;
import n4l.utils.network.server.tcp.TCPMessageTypes;

/**
 * @author xytis
 * 
 */
public class TCPReader extends Object implements Runnable {
	protected Client client;
	protected DataInputStream inStream;
	protected Thread thread;
	private boolean isRunning;

	public TCPReader(Client client, DataInputStream inStream) {
		this.client = client;
		this.inStream = inStream;
	}

	public void run() {
		while (isRunning) {
			try {
				int messageType = inStream.readInt();

				switch (messageType) {
				case TCPMessageTypes.CHAT:
					readMessage();
					break;

				default:
					skipMessage();
					break;
				}
			} catch (Exception e) {
				client.disconnect("Something wrong in client side...");
			}
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		isRunning = false;
		thread.interrupt();
		//thread.stop();
		thread = null;
	}

	public void readMessage() throws IOException {
		// Reverse the message packing in server.tcp.TCPClient
		int length = inStream.readInt();
		byte[] messageBytes = new byte[length];
		inStream.readFully(messageBytes);
		String messageString = new String(messageBytes, "UTF-8");

		client.sendMessage(messageString);
	}

	public void skipMessage() throws IOException {
		int length = inStream.readInt();
		inStream.skipBytes(length);
	}
}

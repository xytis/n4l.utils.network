/**
 * 
 */
package n4l.utils.network.server.tcp;

import java.io.DataInputStream;
import java.io.IOException;

import n4l.utils.network.protocol.Client;
import n4l.utils.network.protocol.Reader;

/**
 * @author xytis
 * 
 */
public class TCPReader extends Object implements Runnable, Reader {

	protected Client client;
	protected DataInputStream inStream;
	protected Thread thread;
	private boolean isRunning;

	public TCPReader(Client client, DataInputStream inStream) {
		this.client = client;
		this.inStream = inStream;
	}

	@Override
	public void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	@Override
	public void stop() {
		isRunning = false;
		// thread.interrupt();
		thread.stop();
		thread = null;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				int messageType = inStream.readInt();
				switch (messageType) {
				case TCPMessageTypes.TEXT:
					readText();
					break;

				case TCPMessageTypes.LOGIN_REQUEST:
					readLoginRequest();
					break;

				case TCPMessageTypes.LOGIN_RESPONCE:
					readLoginResponce();
					break;

				case TCPMessageTypes.LOGIN_STATUS:
					readLoginStatus();
					break;

				case TCPMessageTypes.DISCONNECT:
					readDisconnectNotification();
					break;

				default:
					skip(messageType);
					break;
				}
			} catch (Exception e) {
				client.receiveDisconnectNotification("Something wrong in client side...");
			}
		}
	}

	@Override
	public void readLoginRequest() throws IOException {
		inStream.readInt(); // discard 0 length
		client.receiveLoginRequest();
	}

	@Override
	public void readText() throws IOException {
		// Reverse the message packing in server.tcp.TCPClientListener
		int length = inStream.readInt();
		byte[] messageBytes = new byte[length];
		inStream.readFully(messageBytes);
		String messageString = new String(messageBytes, "UTF-8");

		client.receiveText(messageString);
	}

	@Override
	public void readLoginResponce() throws IOException {
		int length = inStream.readInt(); // Unused length
		byte[] messageBytes;

		length = inStream.readInt();
		messageBytes = new byte[length];
		inStream.readFully(messageBytes);
		String username = new String(messageBytes, "UTF-8");

		length = inStream.readInt();
		messageBytes = new byte[length];
		inStream.readFully(messageBytes);
		String password = new String(messageBytes, "UTF-8");

		client.receiveLoginResponce(username, password);
	}

	@Override
	public void readLoginStatus() throws IOException {
		System.err.println("Ignoring login status!");
		skip(TCPMessageTypes.LOGIN_STATUS);
	}

	@Override
	public void readDisconnectNotification() throws IOException {
		int length = inStream.readInt();
		byte[] messageBytes = new byte[length];
		inStream.readFully(messageBytes);
		String status = new String(messageBytes, "UTF-8");

		client.receiveDisconnectNotification(status);

	}

	@Override
	public void skip(Integer type) throws IOException {
		System.err.println("Skipped message: " + type + "!");
		int length = inStream.readInt();
		inStream.skipBytes(length);
	}
}

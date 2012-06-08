/**
 * 
 */
package n4l.utils.network.server.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import n4l.utils.network.server.Client;
import n4l.utils.network.server.Server;

/**
 * @author xytis
 * 
 */
public class TCPClient extends Object implements Client, Runnable {

	// Server, which should get the messages.
	protected Server server;

	// The socket connection to the user
	protected Socket socket;

	// Data streams down the socket
	protected DataInputStream in;
	protected DataOutputStream out;

	// clientName is the name the user wants to be known by
	protected String clientName;

	protected Thread thread;

	private boolean isRunning = false;

	public TCPClient(Server server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;

		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		
		server.addClient(clientName, this);
		// Request login
		login();
	}
	
	public void login()
	{
		try {
			out.writeInt(TCPMessageTypes.LOGIN);
			out.writeInt(0);
		} catch (Exception e) {
			abort(TCPMessageTypes.ERROR, "Critical failure: Login");
		}
	}

	public void disconnect(String message) {
		try {
			out.write(TCPMessageTypes.DISCONNECT);
			out.writeInt(message.length());
			out.writeBytes(message);
			socket.close();
			stop();
		} catch (Exception e) {
			abort(TCPMessageTypes.ERROR, "Critical failure: Disconnect");
		}
	}

	public void abort(int type, String message)
	{
		try {
			out.write(type);
			out.writeInt(message.length());
			out.writeBytes(message);
			
			stop();
			socket.close();
			server.removeClient(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// handleChatMessage reads an incoming chat message from the user and
	// sends it to the server. The data part of the message is just the
	// chat message itself.

	public void handleMessage() throws IOException {
		// Get the message length
		int length = in.readInt();
		byte[] chatChars = new byte[length];

		in.readFully(chatChars);
		
		server.receiveMessage(new String(chatChars, "UTF-8"));
	}
	
	public void handleLogin() throws IOException {
		// Get the message length
		int length = in.readInt();
		// Get username
		int usernameLength = in.readInt();
		byte[] usernameChars = new byte[usernameLength];
		in.readFully(usernameChars);
		// Get password
		int passwordLength = in.readInt(); // 4 useless bytes
		byte[] passwordChars = new byte[passwordLength];
		in.readFully(passwordChars);
		
		server.receiveMessage("Logging in: " + new String(usernameChars, "UTF-8") + " " + new String(passwordChars, "UTF-8"));
	}

	// If we get a message we don't understand, skip over it. That's
	// why we have the message length as part of the protocol.

	public void skipMessage() throws IOException {
		int length = in.readInt();
		in.skipBytes(length);
	}

	public void run() {	
		while (isRunning) {
			try {

				// Read the type of the next message
				int messageType = in.readInt();

				switch (messageType) {

				// If it's a chat message, read it
				case TCPMessageTypes.CHAT:
					handleMessage();
					break;
					
				case TCPMessageTypes.LOGIN:
					handleLogin();
					break;

				// For any messages whose type we don't understand, skip the
				// message
				default:
					skipMessage();
					return;
				}
			} catch (Exception e) {
				server.removeClient(clientName);
				e.printStackTrace();
				return;
			}
		}
	}

	public void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public void stop() {
		isRunning = false;
		thread.interrupt();
		thread = null;
	}

	@Override
	public void sendMessage(String message) {
		try {
			out.writeInt(TCPMessageTypes.CHAT);
			out.writeInt(message.length());
			out.writeBytes(message);
		} catch (Exception e) {
			server.removeClient(this);
		}
	}

}
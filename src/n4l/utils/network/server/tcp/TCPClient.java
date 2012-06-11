/**
 * 
 */
package n4l.utils.network.server.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import n4l.utils.network.protocol.Client;
import n4l.utils.network.protocol.Reader;
import n4l.utils.network.server.Server;

/**
 * @author xytis
 * 
 */
public class TCPClient extends Object implements Client {

	// Server, which should get the messages.
	protected Server server;

	// The socket connection to the user
	protected Socket socket;

	// Data streams down the socket
	protected DataInputStream in;
	protected DataOutputStream out;

	// client data, later possibly loaded from database
	protected String username;
	protected String password;

	protected Reader reader;

	public TCPClient(Server server, Socket socket) throws IOException {
		System.err.println("Server constructed!");
		this.server = server;
		this.socket = socket;

		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());

		this.reader = new TCPReader(this, in);
		this.reader.start();
	}

	public void abort(int type, String message) {
		try {
			out.write(type);
			out.writeInt(message.length());
			out.writeBytes(message);

			die();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void die() {
		System.err.println("Client " + this.toString() + " died.");
		reader.stop();
		server.removeClient(this);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendText(String message) throws IOException {
		System.out.println("Server say: " + message);
		out.writeInt(TCPMessageTypes.TEXT);
		out.writeInt(message.length());
		out.writeBytes(message);
	}

	@Override
	public void receiveText(String message) {
		System.out.println("Server got: " + message);
		server.handleText(message);
	}

	@Override
	public void sendLoginRequest() throws IOException {
		out.writeInt(TCPMessageTypes.LOGIN_REQUEST);
		out.writeInt(0);
	}

	@Override
	public void receiveLoginRequest() {
		try {
			sendLoginRequest();
			System.out.println("Server initialized login.");
		} catch (IOException e) {
			abort(TCPMessageTypes.TEXT, "Login failed.");
			e.printStackTrace();
		}
	}

	@Override
	public void sendLoginResponce(String username, String password)
			throws IOException {
		System.err.println("Server should not login to user!");
	}

	@Override
	public void receiveLoginResponce(String username, String password) {
		System.out.println("User " + username + " logged in.");
		this.username = username;
		this.password = password;
		server.addClient(username, this);
		try {
			sendLoginStatus(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendLoginStatus(Integer status) throws IOException {
		out.writeInt(TCPMessageTypes.LOGIN_STATUS);
		out.writeInt(4);
		out.writeInt(status);
	}

	@Override
	public void receiveLoginStatus(Integer status) {
		System.err.println("Server should not receive login status!");
	}

	@Override
	public void sendDisconnectNotification(String status) throws IOException {
		out.writeInt(TCPMessageTypes.DISCONNECT);
		out.writeInt(status.length());
		out.writeBytes(status);
	}

	@Override
	public void receiveDisconnectNotification(String status) {
		System.out.println("Client disconnected: " + this.toString()
				+ " with status: " + status);
		if (username != null) {
			server.removeClient(username);
		}
		die();
	}

}
/**
 * 
 */
package n4l.utils.network.client.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import n4l.utils.network.protocol.Client;
import n4l.utils.network.protocol.Reader;
import n4l.utils.network.server.tcp.TCPMessageTypes;

/**
 * @author xytis
 * 
 */
public class TCPClient extends Object implements Client {

	private DataInputStream in;
	private DataOutputStream out;

	private Reader reader;

	private ClientConsole console;

	public TCPClient(DataInputStream in, DataOutputStream out,
			ClientConsole console) {
		this.in = in;
		this.out = out;
		this.console = console;

		// Start up a reader thread that reads messages from the server
		reader = new TCPReader(this, in);

		reader.start();

		try {
			sendLoginRequest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendText(String line) throws IOException {
		System.out.println("I say: " + line);
		out.writeInt(TCPMessageTypes.TEXT);
		out.writeInt(line.length());
		out.writeBytes(line);
	}

	@Override
	public void receiveText(String message) {
		System.out.println("I got: " + message);
	}

	@Override
	public void sendLoginRequest() throws IOException {
		System.out.println("May I, " + this.toString()
				+ ", login to the mighty server?");
		out.writeInt(TCPMessageTypes.LOGIN_REQUEST);
		out.writeInt(0);
	}

	@Override
	public void receiveLoginRequest() {
		System.out.println("Waiting for login.");
		console.login();
	}

	@Override
	public void sendLoginResponce(String username, String password)
			throws IOException {
		System.out.println("I login with: " + username + " " + password);
		out.writeInt(TCPMessageTypes.LOGIN_RESPONCE);
		out.writeInt(username.length() + password.length() + 8);
		out.writeInt(username.length());
		out.writeBytes(username);
		out.writeInt(password.length());
		out.writeBytes(password);
	}

	@Override
	public void receiveLoginResponce(String username, String password) {
		System.err.println("Client should not receive this message!");

	}

	@Override
	public void sendLoginStatus(Integer status) throws IOException {
		System.err.println("Client should not send this message!");
	}

	@Override
	public void receiveLoginStatus(Integer status) {
		if (status == 0) {
			System.out.println("I have logged in!");
		} else {
			System.out.println("Login failed...");
		}
	}

	@Override
	public void sendDisconnectNotification(String status) throws IOException {
		System.out.println("I am disconnecting myself!");
		out.writeInt(TCPMessageTypes.DISCONNECT);
		out.writeInt(0);
	}

	@Override
	public void receiveDisconnectNotification(String message) {
		System.out.println("Server connection closed with message: " + message);
		System.exit(0);
	}
}

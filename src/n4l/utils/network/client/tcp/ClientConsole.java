/**
 * 
 */
package n4l.utils.network.client.tcp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import n4l.utils.thread.SelfThreaded;

/**
 * @author xytis
 * 
 */
public class ClientConsole implements Runnable, SelfThreaded {

	private BufferedReader userInput;

	private Socket clientSocket;

	private DataOutputStream clientOutputStream;
	private DataInputStream clientInputStream;

	private TCPClient client;

	private Thread thread;

	private Boolean isRunning;

	public ClientConsole(String hostname, Integer port) throws IOException {
		isRunning = false;

		// Connect to the server
		clientSocket = new Socket(hostname, port);

		clientOutputStream = new DataOutputStream(
				clientSocket.getOutputStream());

		clientInputStream = new DataInputStream(clientSocket.getInputStream());

		client = new TCPClient(clientInputStream, clientOutputStream, this);

		// Open console
		userInput = new BufferedReader(new InputStreamReader(System.in));
	}

	public synchronized String getUserLine() throws IOException {
		return userInput.readLine();
	}

	public void login() {
		System.out.println("Logging in...");
		try {
			client.sendLoginResponce("xytis", "none");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		this.thread = new Thread(this);
		this.isRunning = true;
		this.thread.start();
	}

	@Override
	public void stop() {
		isRunning = false;
		thread.interrupt();
		// thread.stop();
		thread = null;
	}

	public static void main(String args[]) {
		int port = 4321;

		// Allow the port to be set from the command line (-Dport=4567)

		String portStr = System.getProperty("port");
		if (portStr != null) {
			try {
				port = Integer.parseInt(portStr);
			} catch (Exception ignore) {
			}
		}

		// Allow the server's host name to be specified on the command
		// line (-Dhost=myhost.com)

		String hostname = System.getProperty("host");
		if (hostname == null)
			hostname = "localhost";
		try {
			ClientConsole console = new ClientConsole(hostname, port);
			console.start();

		} catch (Exception e) {
			System.out.println("Got exception:");
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				client.sendText(getUserLine());
			} catch (IOException e) {
				System.out.println("Got exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}

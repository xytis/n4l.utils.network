/**
 * 
 */
package n4l.utils.network.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import n4l.utils.network.server.Server;

/**
 * @author xytis
 * 
 */
public class TCPServer extends Object implements Runnable {
	protected ServerSocket serverSocket;

	protected Server server;
	protected Thread thread;

	private boolean isRunning = false;

	public TCPServer(Server server, int port) throws IOException {
		serverSocket = new ServerSocket(port);

		this.server = server;
	}

	public void run() {
		while (isRunning) {
			try {
				System.out.println("Waiting for connection.");
				Socket newConn = serverSocket.accept();
				System.out.println("Client connected.");
				TCPClient newClient = new TCPClient(server, newConn);
				newClient.start();

			} catch (Exception e) {
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
}

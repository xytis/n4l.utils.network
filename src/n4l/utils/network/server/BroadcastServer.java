/**
 * 
 */
package n4l.utils.network.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import n4l.utils.network.protocol.Client;
import n4l.utils.network.server.tcp.TCPServer;

/**
 * @author xytis
 * 
 */
public class BroadcastServer implements Server {
	private Hashtable<String, Client> clients;

	public BroadcastServer() {
		clients = new Hashtable<String, Client>();
	}

	public synchronized void addClient(String name, Client client) {
		System.err.println("Adding client: " + name);
		if (clients.get(name) != null) {
			try {
				client.sendDisconnectNotification("User name exists");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		clients.put(name, client);
	}

	public synchronized void removeClient(String name) {
		Client client = clients.get(name);
		if (client != null) {
			clients.remove(name);
			client.receiveDisconnectNotification("Bye");
		}
	}

	public synchronized void removeClient(Client client) {
		Enumeration<String> e = clients.keys();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (clients.get(key) == client) {
				clients.remove(key);
				client.receiveDisconnectNotification("Bye");
			}
		}
	}

	public synchronized void handleText(String message) {
		Enumeration<Client> e = clients.elements();
		while (e.hasMoreElements()) {
			Client client = e.nextElement();
			try {
				client.sendText(message);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public synchronized String[] getUserList() {
		Enumeration<String> e = clients.keys();

		String[] nameList = new String[clients.size()];

		int i = 0;
		while (e.hasMoreElements()) {
			nameList[i++] = (String) e.nextElement();
		}

		return nameList;
	}

	public static void main(String args[]) {
		try {
			BroadcastServer server = new BroadcastServer();

			TCPServer tcpServer = new TCPServer(server, 4321);
			System.out.println("Started TCP server");

			tcpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

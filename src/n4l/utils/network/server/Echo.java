/**
 * 
 */
package n4l.utils.network.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import n4l.utils.network.server.tcp.TCPServer;

/**
 * @author xytis
 * 
 */
public class Echo implements Server {
	private Hashtable<String, Client> clients;

	public Echo() {
		clients = new Hashtable<String, Client>();
	}

	public synchronized void addClient(String name, Client client) {
		if (clients.get(name) != null) {
			client.disconnect("User name exists");
			return;
		}

		clients.put(name, client);
	}

	public synchronized void removeClient(String name) {
		Client client = clients.get(name);
		if (client != null) {
			clients.remove(name);
			client.disconnect("Bye");
		}
	}

	public synchronized void removeClient(Client client) {
		Enumeration<String> e = clients.keys();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (clients.get(key) == client) {
				clients.remove(key);
				client.disconnect("Bye");
			}
		}
	}

	public synchronized void receiveMessage(String message) {
		Enumeration<Client> e = clients.elements();
		while (e.hasMoreElements()) {
			Client client = e.nextElement();
			client.sendMessage(message);
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
			Echo echoServer = new Echo();
		
			TCPServer tcpServer = new TCPServer(echoServer, 4321);
			System.out.println("Started TCP server");
			
			tcpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

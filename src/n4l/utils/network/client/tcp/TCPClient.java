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

import n4l.utils.network.server.Client;
import n4l.utils.network.server.tcp.TCPMessageTypes;

/**
 * @author xytis
 * 
 */
public class TCPClient extends Object implements Client{
	public TCPClient() {
	}

	// Display a message
	public void sendMessage(String message) {
		System.out.println(message);
	}

	public void disconnect(String message) {
		System.out.println("Chat server connection closed with message: "
				+ message);
		System.exit(0);
	}
	
	// sendChat sends a chat message to the TCPChatServer program

	public static void sendMessage(DataOutputStream outStream, String line)
			throws IOException {
		outStream.writeInt(TCPMessageTypes.CHAT);
		outStream.writeInt(line.length());
		outStream.writeBytes(line);
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

		String hostName = System.getProperty("host");
		if (hostName == null)
			hostName = "localhost";
		try {

			// Connect to the TCPServer program

			Socket clientSocket = new Socket(hostName, port);

			DataOutputStream clientOutputStream = new DataOutputStream(
					clientSocket.getOutputStream());

			DataInputStream clientInputStream = new DataInputStream(
					clientSocket.getInputStream());

			DataInputStream userInputStream = new DataInputStream(System.in);

			System.out.println("Connected to chat server!");
			// Prompt the user for a name
			System.out.print("What name do you want to use? ");
			System.out.flush();

			
			BufferedReader userInput = new BufferedReader(new InputStreamReader(userInputStream));
			String myName = userInput.readLine();

			// Send the name to the server
			clientOutputStream.writeUTF(myName);

			TCPClient thisClient = new TCPClient();

			// Start up a reader thread that reads messages from the server
			TCPReader reader = new TCPReader(thisClient, clientInputStream);

			reader.start();

			// Read input from System.in

			while (true) {

				String chatLine = userInput.readLine();

				sendMessage(clientOutputStream, chatLine);

			}

		} catch (Exception e) {
			System.out.println("Got exception:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}

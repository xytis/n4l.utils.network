package n4l.utils.network.server;

import n4l.utils.network.protocol.Client;

public interface Server {

	void addClient(String clientName, Client client);

	void removeClient(Client client);

	void removeClient(String clientName);

	void handleText(String message);

}

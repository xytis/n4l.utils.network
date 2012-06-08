package n4l.utils.network.server;

public interface Server {

	void addClient(String clientName, Client client);

	void removeClient(Client client);

	void removeClient(String clientName);

	void receiveMessage(String message);

}

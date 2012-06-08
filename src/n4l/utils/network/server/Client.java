package n4l.utils.network.server;

public interface Client {
	public void sendMessage(String message);

	public void disconnect(String message);
}

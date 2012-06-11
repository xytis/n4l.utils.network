/**
 * 
 */
package n4l.utils.network.protocol;

import java.io.IOException;

/**
 * @author xytis
 * 
 *         This interface describes the high level protocol used for data
 *         transfer.
 */
public interface Client {

	public void sendText(String message) throws IOException;

	public void receiveText(String message);

	public void sendLoginRequest() throws IOException;

	public void receiveLoginRequest();

	public void sendLoginResponce(String username, String password)
			throws IOException;

	public void receiveLoginResponce(String username, String password);

	public void sendLoginStatus(Integer status) throws IOException;

	public void receiveLoginStatus(Integer status);

	public void sendDisconnectNotification(String status) throws IOException;

	public void receiveDisconnectNotification(String status);

}

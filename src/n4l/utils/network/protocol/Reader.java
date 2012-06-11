/**
 * 
 */
package n4l.utils.network.protocol;

import java.io.IOException;

import n4l.utils.thread.SelfThreaded;

/**
 * @author xytis
 * 
 */
public interface Reader extends SelfThreaded {

	// Protocol defined stuff
	public void readText() throws IOException;

	public void readLoginRequest() throws IOException;

	public void readLoginResponce() throws IOException;

	public void readLoginStatus() throws IOException;

	public void readDisconnectNotification() throws IOException;

	public void skip(Integer type) throws IOException;
}

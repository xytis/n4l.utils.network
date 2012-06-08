/**
 * 
 */
package n4l.utils.network.server.tcp;

/**
 * @author xytis
 * 
 */
public interface TCPMessageTypes {
	public static final int DISCONNECT = -1;
	
	public static final int CHAT = 0;
	public static final int LOGIN = 50;
	
	public static final int ERROR = 127;
	public static final int LOGIN_FAILURE = 128;
	
}

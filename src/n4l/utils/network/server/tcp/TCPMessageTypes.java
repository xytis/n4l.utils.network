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

	public static final int TEXT = 0;
	public static final int LOGIN_REQUEST = 50;
	public static final int LOGIN_RESPONCE = 51;
	public static final int LOGIN_STATUS = 52;

	public static final int ERROR = 127;
	public static final int LOGIN_FAILURE = 128;

}

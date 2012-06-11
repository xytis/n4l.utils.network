/**
 * 
 */
package n4l.utils.network.server.tcp;

import n4l.utils.network.server.Message;

/**
 * @author xytis
 * 
 */
public class TCPMessage implements Message {
	private String m_sender;
	private String m_content;

	public TCPMessage(String sender, String content) {
		m_sender = sender;
		m_content = content;
	}

	public String content() {
		return m_content;
	}

	public String sender() {
		return m_sender;
	}
}

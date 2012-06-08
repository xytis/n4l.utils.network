/**
 * 
 */
package n4l.utils.database.auth;

import java.util.HashMap;

/**
 * @author xytis
 * 
 */

public class AuthManager {
	private static HashMap<String, String> users = new HashMap<String, String>();

	public static final int OK = 0;
	public static final int BadPassword = 1;
	public static final int BadUsername = 2;

	public static int login(String name, String passHash) {
		if (users.containsKey(name)) {
			if (users.get(name) == passHash) {
				return OK;
			}
			return BadPassword;
		}
		return BadUsername;
	}

	public static int register(String name, String passHash) {
		if (users.containsKey(name)) {
			return BadUsername;
		}
		users.put(name, passHash);
		return OK;
	}
}

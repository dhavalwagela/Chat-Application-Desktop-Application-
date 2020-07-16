/*
Name: Dhaval Wagela
Student Id: 1001769786
Net Id; dxw9786

Code Reference URL:  https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 */
package chatmessage.Server;

/**
 *
 * @author shubh
 */
import java.io.Serializable;

/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, GETMESSAGE = 3;
	private int type;
	private String message;
	public String users;
	// constructor
	ChatMessage(int type, String message, String list) {
		this.type = type;
		this.message = message;
		this.users = list;
	}
	
	// getters
	int getType() {
		return type;
	}
	String getMessage() {
		return message;
	}
	String getUsers() {return users; }
}


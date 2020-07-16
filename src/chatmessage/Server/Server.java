/*
Name: Dhaval Wagela
Student Id: 1001769786
Net Id; dxw9786

Code Reference URL:  https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java
 https://howtodoinjava.com/library/json-simple-read-write-json-examples/
 */
package chatmessage.Server;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	public ArrayList<ClientThread> al;
	// if I am in a GUI
	private chatmessage.Server.ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	JLabel textLabel;
	

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, chatmessage.Server.ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		JFrame frame = new JFrame("Connected Users");
		textLabel = new JLabel("Connected users will be displayed here",SwingConstants.CENTER);
		textLabel.setPreferredSize(new Dimension(600, 200));
		frame.getContentPane().add(textLabel, BorderLayout.CENTER);
		//Display the window.
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}

	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting

				//For showing list of connected users
				if(al.size() > 0) {
					textLabel.setText("List of the users connected " + "\n");
						if(al.size() > 3)
							al.remove(al.size()-1);
						for (int i = 0; i < al.size(); ++i) {
							ClientThread ct = al.get(i);
							textLabel.setText(textLabel.getText() + "\n         " + (i + 1) + ") " + ct.username + "\n     ");
						}
				}

				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				if(al.size() == 3) {
					ClientGUI cg = new ClientGUI("localhost", 1500);
					JOptionPane.showMessageDialog(null, "Cannot connect more than 3 users !!");
					socket.close();
				}
				for(int i = 0; i < al.size(); i++) {
					ClientThread ct = al.get(i);
					if(ct.username.equals(t.username)) {
						ClientGUI cg = new ClientGUI("localhost", 1500);
						cg.ta.setText("Welcome to the chat room");
						JOptionPane.showMessageDialog(null, "Username already exists !!");
						socket.close();
					}
				}

				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		keepGoing = false;

		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(String message, String users) {
		// add HH:mm:ss and \n to the message

		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console
		if(sg == null)
			System.out.print(messageLf);

		String str[] = users.split(",");
		List<String> userList = new ArrayList<String>();
		for(String user : str)
			userList.add(user);

		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		int count = 0;
		FileReader fr=null;
		String currentUsername = userList.get(0);

		//If no username is given then broadcast
		if(userList.size() == 1) {
			for(ClientThread clientThread : al) {
				String currentString = clientThread.username;
				if (!userList.contains(currentString))
					userList.add(currentString);
			}
		}

		//For writing the data into the file
		try {
			boolean noRecipients = true;
			JSONObject messagesWriteObject = new JSONObject();
			File filecheck = new File("Messages.json");
			for(String s : userList) {
				Date date = new Date();
				if (!s.equals(currentUsername)) {
					if (sg != null) {
						noRecipients = false;
						sg.appendRoom("To : "+ s +"\n" + messageLf); // append in the room window
					}
					if (filecheck.createNewFile() || filecheck.length() == 0) {
						messagesWriteObject.put(s, "  (" + date.toString() + ")  "+message+"\n");
					} else {
						/*Took reference from https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java  */
						/*Took reference from https://howtodoinjava.com/library/json-simple-read-write-json-examples/  */
						JSONParser parser = new JSONParser();
						Object object = parser.parse(new FileReader("Messages.json"));
						JSONObject jsonObject = (JSONObject) object;
						String previousMessages = (String) jsonObject.get(s);
						if (previousMessages != null && previousMessages.length() > 0)
							messagesWriteObject.put(s, previousMessages +"  (" + date.toString() + ")  "+message+"\n");
						else
							messagesWriteObject.put(s, "  (" + date.toString() + ")  "+message+"\n");
					}
				}
			}
			if(noRecipients)
				sg.appendRoom("To none: " + messageLf); //In case when there are no recipients then this message should be displayed on server GUI

			//Here we also want to add previous objects to the final json object which we will write in the file
			if (!filecheck.createNewFile() && filecheck.length() > 0) {

				/*Took reference from https://howtodoinjava.com/library/json-simple-read-write-json-examples/  */
				JSONParser parser = new JSONParser();
				Object object = parser.parse(new FileReader("Messages.json"));
				JSONObject jsonObject = (JSONObject) object;
				for (Object key : jsonObject.keySet()) {
					if (!messagesWriteObject.containsKey(key))
						messagesWriteObject.put(key, jsonObject.get(key));
				}
			}
			/*Took reference from https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java  */
			FileWriter file = new FileWriter("Messages.json");
			file.write(messagesWriteObject.toJSONString());
			file.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
				if (currentUsername.equals(ct.username)) {
					if (!ct.writeMsg(messageLf)) {
						al.remove(i);
						display("Disconnected Client " + ct.username + " removed from list.");
					}
				}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		chatmessage.Server.ChatMessage cm;
		// the date I connect
		String date;

		// Constructore
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (chatmessage.Server.ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				//the user to which message will be sent
				String users = cm.getUsers();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case chatmessage.Server.ChatMessage.MESSAGE:
					broadcast(username + ": " + message, username +  ","+users);
					break;
				case chatmessage.Server.ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case chatmessage.Server.ChatMessage.WHOISIN:
					writeMsg("List of the users connected " + "\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + "\n");
					}
					break;
					case chatmessage.Server.ChatMessage.GETMESSAGE:
						JSONParser parser = new JSONParser();

						//Remove message from file which has been read already
						try {

							/*Took reference from https://howtodoinjava.com/library/json-simple-read-write-json-examples/  */
							Object object = parser.parse(new FileReader("Messages.json"));
							JSONObject jsonObject = (JSONObject) object;
							String textMessages = (String) jsonObject.get(username);
							//If user has no messages which has not been viewed
							if (textMessages == null || textMessages.length() == 0) {
								JOptionPane.showMessageDialog(null, "You have no new messages");
							} else {
								writeMsg(textMessages);
								jsonObject.remove(username);

								/*Took reference from https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java  */
								FileWriter file = new FileWriter("Messages.json");
								file.write(jsonObject.toJSONString());
								file.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			//For showing list of connected users
			if(al.size() > 0) {
				textLabel.setText("List of the users connected " + "\n");
				if (al.size() > 3)
					al.remove(al.size() - 1);
				for (int i = 0; i < al.size(); ++i) {
					ClientThread ct = al.get(i);
					textLabel.setText(textLabel.getText() + "\n         " + (i + 1) + ") " + ct.username + "\n     ");
				}
			} else {
				textLabel.setText("Connected users will be displayed here");
			}
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if (sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if (sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if (socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}



The project has been referenced from
https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-
optional/
https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java
https://howtodoinjava.com/library/json-simple-read-write-json-examples/

Steps to run the project:
1) Import the project named ChatApp.
2) Run ServerGUI.java for starting the server.
3) Run ClientGUI.java for the client’s GUI.

Here for logging in with a user, we need to give a unique username to the GUI of the client and
then click on the Login button.

Then we can type any message and which will be broadcasted to all the connected clients.
For logging-out, we need to click on the Logout button.

For checking the clients’ list which is already connected to the server we need to press the
“Existing users” button and when we will start the server, then also active clients’’ list will be
displayed in a dialog box.

The “Existing users” button will be disabled when the user is not logged in but server will know
which are connected clients.

We need to run ClientGUI.java multiple times in order to add different clients to the server.
If we try to connect a new client who has the same username as any existing user, then it will
show a pop-up message that “Username already exists”.

The server can have only up to 3 clients connected to it at a single time. And if we try to connect
the server to the 4th client then it will show a pop-up message that “Cannot connect more than 3
users”.

For broadcast messages, we don’t need to give any username in the username field while
sending the message, it will automatically send the message to all the connected clients.

For multicast, we need to give usernames with ‘,’ adding between them in the username field for
ex: user1,user2,user3And for unicast, we need to simply give username in the field.

When the user is not connected and other users still try to send the message to it, the message
will be sent and the receiver will receive once it will connect and click on the ‘Get Messages’
button.

All the users will get their corresponding messages only after clicking ‘Get Messages’ button.
When we start a chat and no users have clicked the ‘Get Messages’ button, that means they
have just sent messages till now, then even after they will turn the server off and logout, then
next time when they will be logged in, they can receive messages which were sent to them
earlier.

Server GUI will contain a log of each connected user and the messages that the sent with the
corresponding recipients.

For storing messages with respect to each user, a JSON file has been used and the key has the
recipient and its value is a string. Once the user will receive its messages, the messages from
that file will be removed.

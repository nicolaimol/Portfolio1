#Readme - instructions
##How to start applications
###Server.java
```
javac Server.java
java Server port
```
If you want to print server logic for login and logout use 
```--verbos``` after port

###Client.java
```
javac Client.java
java Client host port username
```
If you want to print client logic for login and logout use
```--verbos``` after port.  
JaneDoe, Erik, Groom and Jose starts bot, they will answer you suggestions

After compiling you can get further information with -h or --help command
```
java Server -h
java Client --help
```

##How to use the applications
###Server.java
The server will start and wait for connections. Every connected socket gets a separate
thread for listening for incoming messages. The server also has a thread that
is listening after user input in the terminal. All incoming messages gets sent to all the
other connected sockets. As server, you could kick users or bots by calling ```kick "username"```.
Messages received containing fuck will automatically get kicked. You end the server calling ```quit```.
If you say ```bye "username"``` the bot will say bye and disconnect.

###Client.java
If you start the client with a username from the bot list, the bot will answer your suggestions. If you start the client
with another username you get at listening thread printing incoming messages and a thread listening to messages from the terminal

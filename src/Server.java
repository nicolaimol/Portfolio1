import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    // Map for connected sockets, using username given at login as key
    private static final Map<String, Socket> socketList = Collections.synchronizedMap(new HashMap<>());
    // List with all the threads for listening for messages to stop the threads from userinput
    private static final List<ServerSocketReadingThread> threadList = Collections.synchronizedList(new ArrayList<>());
    static ServerSocket serverSocketStatic;
    static boolean run = true;

    public static void main(String[] args) {
        if (args.length < 1) return; // Returning if no parameters are given

        // If the -h or --help are given as parameter instructions for the server is given
        if (args[0].equals("-h") || args[0].equals("--help")) {
            System.out.println("You need one parameter\n" +
                    "1. port number for server to run\n" +
                    "2. optional, --verbose to print actions in the server");
            return;
        }

        // parsing the parameter to port number
        int port = Integer.parseInt(args[0]);

        // deciding if the server should print actions
        boolean print = args.length == 2 && args[1].equals("--verbose");

        // trying to start a socket server at the given port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocketStatic = serverSocket;
            // printing port-number
            if (print) System.out.println("Server is listening at port " + port);
            // creating a sending-thread and starting it
            ServerSocketSendingThread serverThread = new ServerSocketSendingThread(socketList, threadList, print);
            serverThread.start();

            while (run) {
                // waits til user connect
                Socket socket = serverSocket.accept();
                // getting the username as standard at login
                String username = SocketUtilServer.readServerLine(socket).replace(":", "").trim();
                // printing the username
                if (print) System.out.println(username);
                // Creating and starting a listening thread with new socket.
                ServerSocketReadingThread read =
                        new ServerSocketReadingThread(socket, socketList, print);
                read.start();
                // Adding the thread to list
                threadList.add(read);
                // Adding username and socket to map
                socketList.put(username, socket);

                // printing for logged in user
                if (print) System.out.println("New client connected " + socket.toString());

            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

class ServerSocketSendingThread extends Thread {

    // Servers sending thread, listening for userinput in the terminal

    // Scanner for reading userinput
    Scanner scanner;

    // fields given in the constructor
    private final Map<String, Socket> socketMap;
    private final List<ServerSocketReadingThread> threadList;
    private final boolean print;

    // constructor for thread
    public ServerSocketSendingThread(Map<String, Socket> socketList, List<ServerSocketReadingThread> threadList, boolean print) throws IOException {
        this.socketMap = socketList;
        this.threadList = threadList;
        scanner = new Scanner(System.in);
        this.print = print;

    }

    // Methode at thread starting.
    @Override
    public synchronized void run() {
        String in;
        // has a everlasting loop
        while (true) {
            // reads from termial
            in = scanner.nextLine();

            // checking for help
            if (in.equals("-h") || in.equals("--help")) {
                // printing help-message
                System.out.println("Send messages for the users to answer! \n" +
                        "If they dont understand, try to ask in infinite or with lets, wanna, we should or with ing ending");
                continue;
            }

            // handling kicking misbehaving bots
            if (in.contains("kick")) {
                String[] users = in.split(" ");
                // printing list of users
                if (print) System.out.println(Arrays.toString(users));
                try {
                    // getting socket based on user input username
                    Socket socket = socketMap.get(users[1].trim());
                    if (socket != null) {
                        // if socket is found it is closed and removed
                        socketMap.get(users[1]).close();
                        socketMap.remove(users[1]);
                        continue;
                    }
                    System.out.println("user not found");
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            // instruction to end the server
            if (in.equals("quit")) {
                for (ServerSocketReadingThread socket : threadList) {
                    try {
                        // stopping all the threads
                        socket.setRun(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // closing all thread
                Iterator<Socket> iterator = socketMap.values().iterator();
                while (iterator.hasNext()) {
                    Socket socket = iterator.next();
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                socketMap.clear();
                if (print) System.out.println("Sockets closed");
                // stopping program
                System.exit(0);
                return;
            }

            // if no action is found, message is sent to all connected servers
            for (Socket socket: socketMap.values()) {
                SocketUtilServer.send(socket, in, "Server");

            }

        }
    }
}

class ServerSocketReadingThread extends Thread {
    // class for reading from socket

    // Fields given in constructor
    Socket socket;
    Map<String, Socket> socketList;
    AtomicBoolean run = new AtomicBoolean(true);
    boolean print;

    public AtomicBoolean getRun() {
        return run;
    }

    public void setRun(boolean set) {
        run.set(set);
    }

    // methode for kicking user, and closing socket.
    public void kickUser(Socket socket) {
        try {
            socket.close();
            socketList.values().remove(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Constructor
    public ServerSocketReadingThread(Socket socket, Map<String, Socket> socketList, boolean print) {
        this.socket = socket;
        this.socketList = socketList;
        this.print = print;
    }

    // When receiving messages they are sent to all the other sockets connected
    public void send(Socket socketInn, String msg, String username) {
        for (Socket socket : socketList.values()) {
            if (socketInn != socket) {
                SocketUtilServer.send(socket, msg, username);
            }
        }
    }

    // Methode call when thread is started
    @Override
    public synchronized void run() {
        // if a socket is received it is closed and should be removed
        Socket socketRead = SocketUtilServer.readServer(socket, this);
        try {
            if (socketRead != null) {
                socketRead.close();
                socketList.values().remove(socketRead);
                if (print && !run.get()) System.out.println(socketRead.toString() + " closed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


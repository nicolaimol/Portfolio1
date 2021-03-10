import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final Map<String, Socket> socketList = Collections.synchronizedMap(new HashMap<>());
    private static final List<ServerSocketReadingThread> threadList = Collections.synchronizedList(new ArrayList<>());
    static ServerSocket serverSocketStatic;
    static boolean run = true;

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocketStatic = serverSocket;
            System.out.println("Server is listening at port " + port);
            ServerSocketSendingThread serverThread = new ServerSocketSendingThread(socketList, threadList);
            serverThread.start();

            while (run) {
                Socket socket = serverSocket.accept();
                String username = SocketUtilServer.readServerLine(socket).replace(":", "").trim();
                System.out.println(username);
                ServerSocketReadingThread read =
                        new ServerSocketReadingThread(socket, socketList);
                read.start();
                threadList.add(read);
                socketList.put(username, socket);

                System.out.println("New client connected " + socket.toString());

            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

class ServerSocketSendingThread extends Thread {

    Scanner scanner;

    private final Map<String, Socket> socketMap;
    private final List<ServerSocketReadingThread> threadList;

    public ServerSocketSendingThread(Map<String, Socket> socketList, List<ServerSocketReadingThread> threadList) throws IOException {
        this.socketMap = socketList;
        this.threadList = threadList;
        scanner = new Scanner(System.in);

    }

    @Override
    public synchronized void run() {
        String in;
        while (true) {
            in = scanner.nextLine();

            if (in.contains("kick")) {
                String[] users = in.split(" ");
                System.out.println(Arrays.toString(users));
                try {
                    Socket socket = socketMap.get(users[1]);
                    if (socket != null) {
                        socketMap.get(users[1]).close();
                        socketMap.remove(users[1]);
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (in.equals("quit")) {
                for (ServerSocketReadingThread socket : threadList) {
                    try {
                        socket.setRun(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (Socket socket: socketMap.values()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Sockets closed");
                try {
                    Server.serverSocketStatic.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
                return;
            }

            for (Socket socket: socketMap.values()) {
                SocketUtilServer.send(socket, in, "Server");

            }

        }
    }
}

class ServerSocketReadingThread extends Thread {
    Socket socket;
    Map<String, Socket> socketList;
    AtomicBoolean run = new AtomicBoolean(true);

    public AtomicBoolean getRun() {
        return run;
    }

    public void setRun(boolean set) {
        run.set(set);
    }

    public void kickUser(Socket socket) {
        try {

            socket.close();
            socketList.values().remove(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServerSocketReadingThread(Socket socket, Map<String, Socket> socketList) {
        this.socket = socket;
        this.socketList = socketList;
    }

    public void send(Socket socketInn, String msg, String username) {
        for (Socket socket : socketList.values()) {
            if (socketInn != socket) {
                SocketUtilServer.send(socket, msg, username);
            }
        }
    }


    @Override
    public synchronized void run() {
        Socket socketRead = SocketUtilServer.readServer(socket, this);
        try {

            socketList.values().remove(socketRead);
            if (socketRead != null) {
                socketRead.close();
                System.out.println(socketRead.toString() + " closed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


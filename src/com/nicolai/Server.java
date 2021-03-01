package com.nicolai;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static List<Socket> socketList = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening at port " + port);
            ServerSocketSendingThread serverThread = new ServerSocketSendingThread(socketList);
            serverThread.start();

            while (true) {
                Socket socket = serverSocket.accept();
                ServerSocketReadingSocket read =
                        new ServerSocketReadingSocket(socket, socketList);
                read.start();
                socketList.add(socket);

                System.out.println("New client connected " + socket.toString());

                /*
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                writer.println(new Date().toString());
                 */
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

class ServerSocketSendingThread extends Thread {

    Scanner scanner;

    private List<Socket> socketList = new ArrayList<>();

    public ServerSocketSendingThread(List<Socket> socketList) throws IOException {
        this.socketList = socketList;
        scanner = new Scanner(System.in);

    }

    @Override
    public synchronized void run() {
        String in;
        while (true) {
            in = scanner.nextLine();

            for (Socket socket: socketList) {
                SocketUtil.send(socket, in, "Server");

            }

        }
    }
}

class ServerSocketReadingSocket extends Thread {
    Socket socket;
    List<Socket> socketList;

    public ServerSocketReadingSocket(Socket socket, List<Socket> socketList) {
        this.socket = socket;
        this.socketList = socketList;
    }

    public void send(Socket socketInn, String msg, String username) {
        for (Socket socket : socketList) {
            if (socketInn != socket) {
                SocketUtil.send(socket, msg, username);
            }
        }
    }


    @Override
    public synchronized void run() {
        Socket socketRead = SocketUtil.readServer(socket, this);
        try {
            socketList.remove(socketRead);
            socketRead.close();
            System.out.println(socketRead.toString() + " closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

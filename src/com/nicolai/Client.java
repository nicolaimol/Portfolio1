package com.nicolai;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length < 3) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        try (Socket socket = new Socket(hostname, port)) {
            ClientThread client = new ClientThread(socket, username);
            client.start();
            while (true) {
                SocketUtil.read(socket);
                if (socket.isClosed()) {
                    System.out.println("Connection closed");
                    break;
                }
                /*
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String time = reader.readLine();

                System.out.println(time);
                 */
            }


        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

class ClientThread extends Thread {
    private Socket socket;
    private Scanner scanner;
    private String username;

    public ClientThread(Socket socket, String username) {
        this.socket = socket;
        this.scanner = new Scanner(System.in);
        this.username = username;
    }

    @Override
    public synchronized void run() {
        String in;
        while (true) {
            in = scanner.nextLine();
            if (in.equals("quit")) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SocketUtil.send(socket, in, username);

        }
    }
}

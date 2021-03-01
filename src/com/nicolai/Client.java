package com.nicolai;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        if (args.length < 3) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        ClientEnum clientEnum;

        String[] bot = {"testBot"};
        if (Arrays.asList(bot).contains(username)) {
            clientEnum = ClientEnum.BOT;
        } else {
            clientEnum = ClientEnum.USER;
        }

        // Starting the connection to the socket
        try (Socket socket = new Socket(hostname, port)) {
            switch (clientEnum) {
                // Code for handling writing for bot
                case BOT:
                    System.out.println("Bot");
                    while (true) {
                        String input = SocketUtil.readClient(socket);
                        if (input.equals("Close")) {
                            System.out.println("Connection closed");
                            return;
                        }

                        //System.out.println(Arrays.toString(inputArray));

                        String msg = Bot.testBot(input);
                        if (msg != null) {

                            SocketUtil.send(socket, msg, username);
                            System.out.println("Message sent");

                        }

                    }

                // Code for handling writing for a user
                case USER:
                    System.out.println("User");
                    ClientThread client = new ClientThread(socket, username);
                    client.start();
                    while (true) {
                        String input = SocketUtil.readClient(socket);
                        if (input.equals("Close")) {
                            System.out.println("Connection closed");
                            return;
                        }
                    }
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
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SocketUtil.send(socket, in, username);

        }
    }
}

enum ClientEnum {
    USER, BOT;
}

class Bot {

    private static String[] makeArray (String input) {
        return input.split(" ");
    }


    public static String testBot(String input) {
        String[] inputArray = makeArray(input);

        if (inputArray[0].equals("Server:")) {
            //System.out.println("Message received from server");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "I would love to " + inputArray[5];
        }
        return null;
    }
}

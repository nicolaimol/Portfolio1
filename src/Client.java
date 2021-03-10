import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class Client {

    public static void main(String[] args) {

        if (args.length < 3) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        ClientEnum clientEnum;

        String[] bot = {"testBot", "Erik"};
        if (Arrays.asList(bot).contains(username)) {
            clientEnum = ClientEnum.BOT;
        } else {
            clientEnum = ClientEnum.USER;
        }

        // Starting the connection to the socket
        try (Socket socket = new Socket(hostname, port)) {
            SocketUtilClient.send(socket, "", username);
            switch (clientEnum) {
                // Code for handling writing for bot
                case BOT:
                    System.out.println("Bot");
                    while (true) {
                        String input = SocketUtilClient.readClient(socket);
                        if (input.equals("Close")) {
                            System.out.println("Connection closed");
                            return;
                        }

                        String msg = switch (username) {
                            case "testBot" -> ClientBot.testBot(input);
                            case "Erik" -> ClientBot.botErik(input);
                            default -> null;
                        };

                        if (msg != null) {

                            SocketUtilClient.send(socket, msg, username);
                            System.out.println("Message sent");

                        }

                    }

                // Code for handling writing for a user
                case USER:
                    System.out.println("User");
                    ClientThread client = new ClientThread(socket, username);
                    client.start();
                    while (true) {

                        String input = SocketUtilClient.readClient(socket);
                        if (input.equals("Close")) {
                            System.out.println("Connection closed");
                            System.exit(0);
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
    private final Socket socket;
    private final Scanner scanner;
    private final String username;

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SocketUtilClient.send(socket, in, username);

        }
    }
}

enum ClientEnum {
    USER, BOT
}

class ClientBot {

    private static String[] makeArray (String input) {
        return input.split(":");
    }


    public static String testBot(String input) {
        String[] inputArray = makeArray(input);

        if (inputArray[0].equals("Server")) {
            //System.out.println("Message received from server");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String[] suggestions = Test.findAction(input);

            return "I would love to " + suggestions[0] + " " + suggestions[1];
        }
        return null;
    }

    public static String botErik(String input) {
        String[] inputArray = makeArray(input);

        if (inputArray[0].equals("Server")) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "I don't want to do that ):";
        }
        return null;
    }
}


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client {

    public static void main(String[] args) {

        if (args.length < 3) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        ClientEnum clientEnum;

        String[] bot = {"testBot", "Erik", "Groom", "Jose"};
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
                            case "Groom" -> ClientBot.botGroom(input);
                            case "Jose" -> ClientBot.botJosé(input);
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

    private static final List<String[]> actionList = new ArrayList<>();

    private static final Random random = new Random(System.currentTimeMillis());


    public static String testBot(String input) {
        String[] inputArray = makeArray(input);

        if (inputArray[0].equals("Server")) {
            //System.out.println("Message received from server");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] suggestions = WordAnalysing.findAction(input);

            if (suggestions[0].equals("greeting")) {
                if (suggestions[1].equals("hi")) {
                    return "Howdy";
                }
                else {
                    return "Nm";
                }
            }

            return "I would love to " + suggestions[0] + " " + suggestions[1];
        }
        return null;
    }

    public static String botErik(String input) {
        String[] inputArray = makeArray(input);
        String[] actions = WordAnalysing.findAction(input);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (inputArray[0].equals("Server")) {


            if (actions[0].equals("greeting")) {
                if (actions[1].equals("hi")) {
                    return "Hey,i hete Erik\uD83E\uDD20";
                }
                else {
                    return "Bored af!\uD83D\uDE43\uD83D\uDD2B";
                }
            }

            return "I don't want to do that \uD83D\uDE12";
        }
        if (inputArray[0].equals("Jose")) {
            if (inputArray[1].trim().equals("¡Español por favor!")) {
                return "NO HABLO EPSAÑOL JOSE\uD83E\uDD2C";
            }
        }
        return null;
    }

    public static String botGroom(String input) {
        String[] inputArray = makeArray(input);

        String[] actions = WordAnalysing.findAction(input);

        int randomly = random.nextInt(6);

        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignored) {

        }

        if (inputArray[0].equalsIgnoreCase("Server") && actions.length == 2 ) {


            if (actions[0] != null) {

                if (actions[0].equals("greeting")) {
                    if (actions[1].equals("hi")) {
                        return "✨Heeeeey✨ \uD83D\uDC95";
                    }
                    else {
                        return "Bored af!";
                    }
                }


                if (actionList.contains(actions)) {
                    return "Didnt you already ask about that?";
                }
                else {
                    actionList.add(actions);
                    if (randomly < 3) {
                        return actions[0] + "ing " + actions[1] + " sounds lovely today";
                    } else {
                        return "Nah that's boring";
                    }
                }
            } else {
                return "What do you mean?";
            }
        }
        else if (inputArray[0].equalsIgnoreCase("Server") && actions.length == 5) {
            if (actions[4].equals("or")) {
                if (randomly < 3) {
                    return "How nice of you to for once give us a choice.\n" + actions[0] + actions[1] + " sounds lovely";
                } else {
                    return "How nice of you to for once give us a choice.\n" + actions[2] + actions[3] + " sounds lovely";
                }
            } if (actions[4].equals("and")) {
            }

        }
        else if (inputArray[0].equals("Erik") && inputArray[1].contains("I don't")) {
            int random1 = random.nextInt(10);
            return random1 < 9 ? null : "You are so negative Erik";
        }

        return null;


    }

    public static String botJosé(String input) {
        String[] inputArray = makeArray(input);
        String[] actionsArray = inputArray[1].trim().split(" ");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }

        if (!inputArray[0].equals("Server")) {
            return null;
        }

        if (actionsArray.length == 1) {
            return "Hola☀️";
        }
        else {
            int next = random.nextInt(4);
            return switch (next) {
                case 0 -> "¡No hablo ingles!";
                case 1 -> "¡No entiendo!";
                case 2 -> "¿Qué?";
                case 3 -> "¡Español por favor!";
                default -> "¡No se!";
            };
        }
    }
}


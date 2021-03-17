import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client {

    public static void main(String[] args) {

        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            System.out.println("You need tre parameters.\n" +
                    "1. host (localhost)\n" +
                    "2. port number\n" +
                    "3. username (Erik, Groom, Jose are used ass bots)\n" +
                    "end with --verbose to se the dynamic behind the bots answers");
        }

        if (args.length < 3) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        ClientEnum clientEnum;
        boolean print = args.length == 4 && args[3].equals("--verbose");

        String[] bot = {"JaneDoe", "Erik", "Groom", "Jose"};
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
                            case "JaneDoe" -> ClientBot.botJaneDoe(input, print);
                            case "Erik" -> ClientBot.botErik(input, print);
                            case "Groom" -> ClientBot.botGroom(input, print);
                            case "Jose" -> ClientBot.botJosé(input, print);
                            default -> null;
                        };

                        if (msg != null) {

                            SocketUtilClient.send(socket, msg, username);
                            System.out.println("Message sent");
                            if (WordAnalysing.findAction(input)[0].equals("okay")) {
                                socket.close();
                                return;
                            }
                            if (msg.equals("bye") || msg.equalsIgnoreCase("buy guys") || msg.equals("hasta luego")) {
                                socket.close();
                                return;
                            }

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


    public static String botJaneDoe(String input, boolean print) {
        String[] inputArray = makeArray(input);

        if (inputArray[0].equals("Server")) {
            //System.out.println("Message received from server");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] suggestions = WordAnalysing.findAction(input);
            if (print) System.out.println(Arrays.toString(suggestions));

            if (suggestions[0].equals("okay")) {
                return "Okay bye";
            }

            if (suggestions[0].equals("bye") && suggestions[1].equalsIgnoreCase("testBot")) {
                return "bye";
            }

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

    public static String botErik(String input, boolean print) {
        String[] inputArray = makeArray(input);
        String[] actions = WordAnalysing.findAction(input);

        if (print) System.out.println(Arrays.toString(actions));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (inputArray[0].equals("Server") && actions[0] != null) {

            if (actions[0].equals("bye") && actions[1].equalsIgnoreCase("erik")) {
                return "bye guys";
            }

            if (actions[0].equals("okay")) {
                return "Have fun, talk to you later";
            }


            if (actions[0].equals("greeting")) {
                if (actions[1].equals("hi")) {
                    return "Hey,i hete Erik\uD83E\uDD20";
                }
                else {
                    return "Bored af!\uD83D\uDE43\uD83D\uDD2B";
                }
            }

            if (actionList.contains(actions)) {
                return "I still don't want to do that \uD83D\uDE12";
            }
            actionList.add(actions);
            if (actions.length == 2) {
                int ransomAns = random.nextInt(3);
                return switch (ransomAns) {
                    case 0 -> "I don't want to do that \uD83D\uDE12";
                    case 1 -> "Nah, " + actions[0] + "ing is shit";
                    default -> "Fuck it";
                };
            }
            else if (actions.length == 5) {
                if (actions[4].equals("or")) {
                    int ransomAns = random.nextInt(3);
                    return switch (ransomAns) {
                        case 0 -> "I don't want to do either of that \uD83D\uDE12";
                        case 1 -> "Nah, " + actions[0] + "ing is shit. " + actions[2] + "ing is shit especially " + actions[3];
                        default -> "Fuck it";
                    };
                }
                if (actions[4].equals("and")) {
                    int ransomAns = random.nextInt(3);
                    return switch (ransomAns) {
                        case 0 -> actions[0] + " " + actions[1] + " is for nerds";
                        case 1 -> "Nah, " + actions[0] + "ing is shit. " + actions[2] + "ing is shit especially " + actions[3];
                        default -> "I am actually busy today";
                    };
                }
            }



        }
        if (inputArray[0].equals("Jose")) {
            if (inputArray[1].trim().equals("¡Español por favor!")) {
                return "NO HABLO EPSAÑOL JOSE\uD83E\uDD2C";
            }
        }
        return null;
    }

    public static String botGroom(String input, boolean print) {
        String[] inputArray = makeArray(input);

        String[] actions = WordAnalysing.findAction(input);
        if (print) System.out.println(Arrays.toString(actions));

        int randomly = random.nextInt(6);
        if (print) System.out.println(randomly < 3 ? "Positive": "Negative");

        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignored) {

        }

        if (actions.length == 1 && actions[0].equals("okay")) {
            return "See you soon";
        }

        if (inputArray[0].equalsIgnoreCase("Server") && actions.length == 2 ) {


            if (actions[0] != null) {

                if (actions[0].equals("greeting")) {
                    if (actions[1].equals("hi")) {
                        return "✨Heeeeey✨ \uD83D\uDC95";
                    }
                    else {
                        return "Not much!";
                    }
                }

                if (actions[0].equals("bye") && actions[1].equalsIgnoreCase("groom")) {
                    return "bye";
                }


                if (actionList.contains(actions)) {
                    return "Didnt you already ask about that?";
                }
                else {
                    actionList.add(actions);
                    if (randomly < 3) {
                        return switch (random.nextInt(3)) {
                            case 0 -> actions[0] + "ing " + actions[1] + " sounds lovely today";
                            case 1 -> "With pleasure";
                            default -> "I will happily " + actions[0] + " " + actions[1];
                        };
                    } else {
                        return switch (random.nextInt(3)) {
                            case 0 -> "Nah that's boring";
                            case 1 -> "I hate " + actions[0] + "ing " + actions[1];
                            default -> "Its a no from me";
                        };
                    }
                }
            } else {
                return "What do you mean?";
            }
        }
        else if (inputArray[0].equalsIgnoreCase("Server") && actions.length == 5) {
            if (actions[4].equals("or")) {
                if (randomly < 3) {
                    int randomPos = random.nextInt(3);
                    return switch (randomPos) {
                        case 0 -> "How nice of you to for once give us a choice. " + actions[0] + actions[1] + " sounds lovely";
                        case 1 -> "Hitting the big drum today! " + actions[2] + "ing " + actions[3] + " is thw shit";
                        default -> "Both is fine tbh";
                    };

                } else {
                    int randomNeg = random.nextInt(3);
                    return switch (randomNeg) {
                        case 0 -> "How nice of you to for once give us a choice. " + actions[2] + actions[3] + " sucks tho";
                        case 1 -> "Wow so \"creative\". both " + actions[0] + " " + actions[1] + " and " + actions[2]
                                + " " + actions[3] + " is lame af";
                        default -> "I really dont care...";
                    };

                }
            } if (actions[4].equals("and")) {
                if (randomly < 3) {
                    int randomPos = random.nextInt(3);
                    return switch (randomPos) {
                        case 0 -> "YES, lets " + actions[0] + " " + actions[1] + " and " + actions[2] + " " + actions[3];
                        case 1 -> "Its a perfect day for " + actions[2] + "ing " + actions[3];
                        default -> "Common lets goooo";
                    };

                } else {
                    int randomNeg = random.nextInt(3);
                    return switch (randomNeg) {
                        case 0 -> "Sure we could " + actions[0] + " " + actions[1] + ", but not " + actions[2] + " " +actions[3];
                        case 1 -> "I really dont feel like that today";
                        default -> "I couldn't care less...";
                    };

                }
            }

        }
        else if (inputArray[0].equals("Erik") && inputArray[1].contains("I don't")) {
            int random1 = random.nextInt(10);
            return random1 < 9 ? null : "You are so negative Erik";
        }

        return null;


    }

    public static String botJosé(String input, boolean print) {
        String[] inputArray = makeArray(input);
        String[] actionsArray = WordAnalysing.findAction(input);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }

        if (!inputArray[0].equals("Server")) {
            return null;
        }

        if (actionsArray[0].equals("okay")) {
            return "Vale, adiós";
        }

        if (actionsArray[0].equals("greeting") && actionsArray[1].equals("hi")) {
            if (print) System.out.println(Arrays.toString(actionsArray));
            return "Hola☀️";
        } else if (actionsArray[0].equals("greeting") && actionsArray[1].equals("whats up")){
            if (print) System.out.println(Arrays.toString(actionsArray));
            return "¿no mucho, que tal tu?";
        }

        if (actionsArray[0].equals("bye") && actionsArray[1].equalsIgnoreCase("jose")) {
            return "Hasta luego chicos";
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



import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SocketUtilServer {


    public static void send(Socket socket, String msg, String username) {
        OutputStream output = null;
        try {
            output = socket.getOutputStream();

            PrintWriter writer = new PrintWriter(output, true);
            //BufferedWriter bufferedWriter = new BufferedWriter(writer);

            writer.println(username + ": " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Socket readServer (Socket socket, ServerSocketReadingThread readingSocket) {
        while (readingSocket.getRun().get()) {

            try {
                String input = null;
                if (!socket.isClosed()) {
                    input = readPrivate(socket);
                }
                if (input == null) {
                    return socket;
                }

                if (input.toLowerCase().contains("fuck")) {
                    readingSocket.socketList.forEach((key, value) -> {

                        if (socket == value) {
                            System.out.println(key + " was kicked for bad behaviour");
                            readingSocket.send(socket, key + " was kicked for bad behaviour", "Server");
                        }
                    });
                    readingSocket.kickUser(socket);
                    continue;
                }

                String[] message = input.split("[:]");
                readingSocket.send(socket, message[1].trim(), message[0]);

                System.out.println("\r" + input);


            } catch (IOException  e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static String readServerLine(Socket socket) throws IOException {
        return readPrivate(socket);
    }

    private static String readPrivate(Socket socket) throws IOException {

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String input = reader.readLine();
            return input;
        } catch (Exception e) {
            return null;
        }

    }
}

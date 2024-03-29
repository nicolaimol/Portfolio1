
import java.io.*;
import java.net.Socket;

public class SocketUtilClient {

    public static void send(Socket socket, String msg, String username) {
        OutputStream output = null;
        try {
            output = socket.getOutputStream();

            PrintWriter writer = new PrintWriter(output, true);

            writer.println(username + ": " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readPrivate(Socket socket) throws IOException {

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }

    }

    public static String readClient(Socket socket) {
        while (true) {

            try {

                String input = readPrivate(socket);
                if (input == null) {
                    return "Close";
                }

                System.out.println("\r" + input);
                return input;

            } catch (IOException e) {
                return "Close";
                //e.printStackTrace();
            }

        }
    }


}

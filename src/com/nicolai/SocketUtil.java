package com.nicolai;

import java.io.*;
import java.net.Socket;
import java.util.List;
import com.nicolai.ServerSocketReadingSocket;

public class SocketUtil {

    public static void send(Socket socket, String msg, String username) {
        OutputStream output = null;
        try {
            output = socket.getOutputStream();

            PrintWriter writer = new PrintWriter(output, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            writer.println(username + ": " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readPrivate(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String input = reader.readLine();
        return input;
    }

    public static Socket read(Socket socket) {
        while (true) {

            try {

                String input = readPrivate(socket);
                if (input == null) {
                    return socket;
                }

                System.out.println(input);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static Socket read(Socket socket, ServerSocketReadingSocket readingSocket) {
        while (true) {
            
            try {

                String input = readPrivate(socket);
                if (input == null) {
                    return socket;
                }

                String[] inputArray = input.split(":");
                readingSocket.send(socket, inputArray[1], inputArray[0].trim());

                System.out.println(input);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}

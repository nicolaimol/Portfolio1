import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8081)) {
            while (true) {
                InputStream inputStreamReader = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamReader));

                String input = bufferedReader.readLine();
                System.out.println(input);

            }
        } catch (IOException e) {

        }

    }
}

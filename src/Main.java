import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            System.out.println(random.nextInt(3));
        }

        /*
        Scanner scanner = new Scanner(System.in);
        System.out.println(scanner.hasNext());

         */

        /*
        try (Socket socket = new Socket("localhost", 8081)) {
            while (true) {
                InputStream inputStreamReader = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamReader));

                String input = bufferedReader.readLine();
                System.out.println(input);

            }
        } catch (IOException e) {

        }
         */

    }
}

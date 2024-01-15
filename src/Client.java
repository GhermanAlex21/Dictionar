import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    static void trimiteMesaj(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

        Scanner scanner = new Scanner(System.in);
        String inputStr = null;

        while (!(inputStr = scanner.nextLine()).equals("exit")) {
            out.write(inputStr + "\n");
            out.flush();

            String primit = in.readLine();
            System.out.println(primit);
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 4444);
            System.out.println("Bine ați venit la dicționar! Vă rugăm să interogați, adăugați sau eliminați cuvinte!");
            trimiteMesaj(socket);
            System.out.println("Conectat la server cu succes!");
        } catch (UnknownHostException e) {
            System.out.println("Eroare! A apărut o eroare de gazdă!");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O!");
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Eroare! A apărut o eroare de socket!");
                }
            }
        }
    }
}
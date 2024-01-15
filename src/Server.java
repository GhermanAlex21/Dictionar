import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    ExecutorService pool = null;

    public static void main(String[] args) {
        Server dic_server = new Server();
        dic_server.startServer();
    }

    public void startServer() {
        ServerSocket listeningSocket = null;
        Socket clientSocket = null;

        Dictionar.creeazaDictionar();

        pool = Executors.newFixedThreadPool(5);

        try {
            listeningSocket = new ServerSocket(4444);
            int i = 0;

            while (true) {
                System.out.println("Serverul ascultă pe portul 4444 pentru o conexiune");

                clientSocket = listeningSocket.accept();
                i++;
                System.out.println("Conexiunea cu clientul numărul " + i + " a fost acceptată:");
                System.out.println("Portul de la distanță: " + clientSocket.getPort());
                System.out.println("Numele gazdei de la distanță: " + clientSocket.getInetAddress().getHostName());
                System.out.println("Portul local: " + clientSocket.getLocalPort());

                MultiThreadServer runnable = new MultiThreadServer(clientSocket, i, this);
                pool.execute(runnable);
            }

        } catch (SocketException ex) {
            System.out.println("Eroare! A apărut o eroare de gazdă! ");
        } catch (IOException e) {
            System.out.println("Eroare! A apărut o eroare I/O! ");
        } finally {
            if (listeningSocket != null) {
                try {
                    listeningSocket.close();
                } catch (IOException e) {
                    System.out.println("Eroare! A apărut o eroare de socket! ");
                }
            }
        }
    }
}
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor implements Runnable {

    private ServerSocket server;

    public Servidor(int port) throws IOException {
        this.server = new ServerSocket();
        server.bind(new InetSocketAddress("localhost", port));
    }


    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    @Override
    public void run() {
        InetAddress inet = this.server.getInetAddress();
        while (!Thread.currentThread().isInterrupted()) {
            Console.log("Esperando conexão");
            Socket cliente = null;
            try {
                cliente = this.server.accept();
                Console.log("Cliente conectou");
                Thread t = new Thread(new Comunicacao(cliente));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}


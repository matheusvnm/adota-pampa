import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Servidor implements Runnable {

    private ServerSocket server;
    private InetSocketAddress inet;
    Servidor(int port) throws IOException {
        this.server = new ServerSocket();
        this.inet = new InetSocketAddress("localhost", port);
        server.bind(this.inet);
    }


    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public String getCompleteAdress(){

        return this.inet.getHostName() + ":"+ this.inet.getPort();
    }

    @Override
    public void run() {
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


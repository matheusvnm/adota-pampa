import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class Principal {
    public static void main(String[] args) {


        final String ZNODEPATH = "/NodoDeRedes";
        ZKManager manager = new ZKManagerImpl();
        byte[] data = "Servidor de Redes".getBytes();

        try {
            Console.log("Criando nodo...");
            manager.create(ZNODEPATH, data);
            Console.log("Nodo criado.");
        } catch (KeeperException e) {
            Console.log("Esse nodo já existe! Continuando programa");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void servidores() {

        try {
            Servidor s1 = new Servidor(4422);
            Servidor s2 = new Servidor(3011);
            Servidor s3 = new Servidor(1321);
            new Thread(s1).start();
            new Thread(s2).start();
            new Thread(s3).start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

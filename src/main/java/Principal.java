import java.io.IOException;

public class Principal {
    public static void main(String[] args) {

        // Há bugs relacionados ao logger do Apache. Logo retirei ele para não haver bugs.
        ZKManager manager = new ZKManagerImpl();


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

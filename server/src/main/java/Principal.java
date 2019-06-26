import java.io.IOException;

public class Principal {
    public static void main(String[] args) {


        String ZNODEPATH = "/NodoCentral";
        Executor executorManager = null;
        try {
            Console.log("Criando nodo...");
            executorManager = new Executor("localhost:2181", null, null);
            Console.log("Nodo criado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert executorManager != null;
        executorManager.create(ZNODEPATH);
        new Thread(executorManager).start();

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

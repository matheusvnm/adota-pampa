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

    }


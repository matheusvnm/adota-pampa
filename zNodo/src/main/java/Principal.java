import java.io.IOException;

public class Principal {
    public static void main(String[] args) {

        //Armazena na variável ZNODEPATH o caminho para o directorio do zNodo
        String ZNODEPATH = "/NodoCentral";
        Executor executorManager = null;
        try {
            Console.log("Criando nodo...");
            //Cria um executor que irá instanciar o ZooKeeper, passando hostPort por parametro
            executorManager = new Executor("localhost:2181", null, null);
            Console.log("Nodo criado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert executorManager != null;
        //Instancia um zNode passando o path por parametro
        executorManager.create(ZNODEPATH);
        //Instancia e inicia uma Thread passando o executor como parametro
        new Thread(executorManager).start();
    }
 }


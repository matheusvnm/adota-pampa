import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Principal {
    public static void main(String[] args) {

        ZooKeeper zKeeper;
        String path = "/NodoCentral";
        try {
            //Instancia o zookeeper passando hostPort e um timeout value
            zKeeper = new ZooKeeper("localhost:2181", 3000, null);
            //Instancia três servidores em portas diferentes
            Servidor s1 = new Servidor(4422);
            Servidor s2 = new Servidor(3011);
            Servidor s3 = new Servidor(1321);
            //Instancia e inicia uma Thread pra cada servidor
            new Thread(s1).start();
            new Thread(s2).start();
            new Thread(s3).start();
            //Adiciona em uma String o endereço completo de cada servidor concatenado com hostPort
            String data = s1.getCompleteAdress() + " " +s2.getCompleteAdress() + " " + s3.getCompleteAdress();
            //Verifica se existe uma versão do zKeeper instanciada no path passado por parametro e retorna a versão do zKeeper
            //Armazena a versão em uma variavel int chamada version
            int version = zKeeper.exists(path , true).getVersion();
            //
            zKeeper.setData(path, data.getBytes(), version);

        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }



    }

}

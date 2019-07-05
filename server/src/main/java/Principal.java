import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Principal {
    public static void main(String[] args) {

        ZooKeeper zKeeper;
        String path = "/NodoCentral";
        try {
            zKeeper = new ZooKeeper("localhost:2181", 3000, null);
            Servidor s1 = new Servidor(4422);
            Servidor s2 = new Servidor(3011);
            Servidor s3 = new Servidor(1321);
            new Thread(s1).start();
            new Thread(s2).start();
            new Thread(s3).start();
            String data = s1.getCompleteAdress() + " " +s2.getCompleteAdress() + " " + s3.getCompleteAdress();
            int version = zKeeper.exists(path , true).getVersion();
            zKeeper.setData(path, data.getBytes(), version);

        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

    }

}

import org.apache.log4j.varia.NullAppender;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class ZKManagerImpl implements ZKManager {
    private static ZooKeeper zkeeper;
    private static ZKConnection zkConnection;

    public ZKManagerImpl() {
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
        initialize();
    }

    private void initialize() {
        try {
            Console.log("Nova conexão foi criada");
            zkConnection = new ZKConnection();
            String host = "localhost";
            Console.log("Conexão no servidor: " + host);
            zkeeper = zkConnection.connect(host);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            zkConnection.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zkeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public Object getZNodeData(String path, boolean watchFlag) {
        byte[] b = null;
        try {
            b = zkeeper.getData(path, null, null);
            Console.log("Criação bem sucedida do nodo!\n" + "Caminho: " + path);
            return new String(b, "UTF-8");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Console.log("Erro na criação dos dados do nodo");
        return null;
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);
    }
}
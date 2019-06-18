import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

class ZKConnection {
    private ZooKeeper zoo;
    CountDownLatch connectionLatch = new CountDownLatch(1);

    ZooKeeper connect(String host) throws IOException, InterruptedException {
        Console.log("Criando conexão com Zookeeper!");
        this.zoo = new ZooKeeper(host, 2000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });
        Console.log("Conexão estabilizada com Zookeeper!");
        this.connectionLatch.await();
        return zoo;
    }

    void close() throws InterruptedException {
        zoo.close();
    }

}

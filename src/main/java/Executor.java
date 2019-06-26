import java.io.*;
import java.nio.charset.StandardCharsets;

import org.apache.zookeeper.*;

public class Executor implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    private DataMonitor dataMonitor;
    private ZooKeeper zKeeper;
    private String fileName;
    private String[] exec;
    private Process child;

    Executor(String hostPort, String fileName, String[] exec) throws IOException {
        this.fileName = fileName;
        this.exec = exec;
        zKeeper = new ZooKeeper(hostPort, 3000, this);
    }

    public void process(WatchedEvent event) {
        dataMonitor.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dataMonitor.isDead()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    public void exists(byte[] data) {
        if (data == null) {
            if (child != null) {
                Console.log("Destruindo o processo");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            child = null;
        } else {
            if (child != null) {
                Console.log("Interrompendo o child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                fileOutputStream.write(data);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Console.log("Iniciando o child");
                child = Runtime.getRuntime().exec(exec);
                new StreamWriter(child.getInputStream(), System.out);
                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void create(String path) {
        try {
            this.zKeeper.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            Console.log("Nodo já existente! Continuando execução do programa");
        } catch (InterruptedException e) {
            Console.log("[ERRO] Conexão encerrada!");
        } finally {
            dataMonitor = new DataMonitor(this.zKeeper, path, null, this);
        }

    }

    public Object getZNodeData(String path) {
        byte[] b = null;
        try {
            b = this.zKeeper.getData(path, null, null);
            return new String(b, StandardCharsets.UTF_8);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        int version = this.zKeeper.exists(path, true).getVersion();
        this.zKeeper.setData(path, data, version);
    }


    static class StreamWriter extends Thread {
        OutputStream os;

        InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        public void run() {
            byte[] b = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Executor implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    private String zNode;
    private DataMonitor dataMonitor;
    private ZooKeeper zKeeper;
    private String fileName;
    private String[] exec;
    private Process child;

    public Executor(String hostPort, String zNode, String fileName, String[] exec) throws IOException {
        this.fileName = fileName;
        this.exec = exec;
        zKeeper = new ZooKeeper(hostPort, 3000, this);
        dataMonitor = new DataMonitor(zKeeper, zNode, null, this);
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

    public String getzNode() {
        return zNode;
    }

    public void setzNode(String zNode) {
        this.zNode = zNode;
    }



}
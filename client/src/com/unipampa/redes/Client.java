package com.unipampa.redes;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        BotServerConnector botServerConnector = new BotServerConnector();
        try {
            botServerConnector.connect();
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}


class BotServerConnector {s


    void connect() throws IOException, KeeperException, InterruptedException {
        final String ZNODEPATH = "/NodoCentral";
        ZooKeeper zKeeper = new ZooKeeper("localhost", 3000, null);
        try {
            Socket socket = new Socket(InetAddress.getByName("localhost"), 2181);
            socket.setKeepAlive(true);
            System.out.println("Resposta: \n------------");
            String condicao = "";
            Scanner teclado = new Scanner(System.in);
            while (teclado.hasNextLine() && !condicao.equals("end")) {
                condicao = teclado.nextLine();
                zKeeper.setData(ZNODEPATH, condicao.getBytes(), zKeeper.exists(ZNODEPATH, false).getVersion());
            }
            socket.close();
            System.out.println("\n ClienteSide--------------Fim da Conexão");
        } catch (SocketException e) {
            System.out.println("O servidor não conseguiu responder, possívelmente caiu ou está offline");
        }
    }
}

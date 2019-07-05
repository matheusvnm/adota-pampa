package com.unipampa.redes;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        BotServerConnector botServerConnector = new BotServerConnector();
        try {
            botServerConnector.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


class BotServerConnector {
    void connect() throws IOException {


        try {
            String path = "/NodoCentral";
            byte[] data;
            ZooKeeper zKeeper;
            zKeeper = new ZooKeeper("localhost:2181", 3000, null);
            data = zKeeper.getData(path, null, null);
            String dados = new String(data, StandardCharsets.UTF_8); ;
            String[] parts = dados.split(" ");
            String servidorUM = parts[0];
            String servidorDois = parts[1];
            String servidorTres = parts[2];



            Socket connectionToserver = new Socket(InetAddress.getByName("localhost"), 2181);
            connectionToserver.setKeepAlive(true);



            PrintWriter saidaParaOServidor = new PrintWriter(connectionToserver.getOutputStream());
            String token = "8713681euihkwjasdgauydjhawdklad";
            saidaParaOServidor.println("CONN TOKEN: " + token);
            saidaParaOServidor.flush();

            BufferedReader respostaServidor = new BufferedReader(new InputStreamReader(connectionToserver.getInputStream()));
            String linhaDeResposta;
            System.out.println("Resposta: \n------------");
            Scanner s = new Scanner(System.in);
            while ((linhaDeResposta = respostaServidor.readLine()) != null && !linhaDeResposta.equals("end")) {

                if (linhaDeResposta.equals("needComand")) {
                    System.out.print("--------------\nDigite algo: ");
                    String entradaUsuario = s.next() + " " + s.nextLine();
                    System.out.print("\n");
                    saidaParaOServidor.println(entradaUsuario);
                    saidaParaOServidor.flush();
                } else {
                    System.out.println(linhaDeResposta);
                }
            }
            connectionToserver.close();
            System.out.println("\nClienteSide--------------Fim da Conexão");
        } catch (IOException | InterruptedException | KeeperException e) {
            System.out.println("O servidor não conseguiu responder, possívelmente caiu ou está offline");
        }
    }



}

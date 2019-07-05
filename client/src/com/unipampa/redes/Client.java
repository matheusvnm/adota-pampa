package com.unipampa.redes;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;
import java.net.*;
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

        boolean dropTrue = false;
        do {
            try {
                String path = "/NodoCentral";
                byte[] data;
                PrintWriter saidaParaOServidor = null;
                ZooKeeper zKeeper;
                zKeeper = new ZooKeeper("localhost:2181", 3000, null);
                data = zKeeper.getData(path, null, null);
                String dados = new String(data, StandardCharsets.UTF_8);
                String[] parts = dados.split(" ");
                Socket connectionToserver = null;
                boolean foundServer = false;
                for (String server : parts) {
                    System.out.println("Entrou");
                    String[] temp = server.split(":");
                    String name = temp[0];
                    int port = Integer.parseInt(temp[1]);
                    try {
                        connectionToserver = new Socket(InetAddress.getByName(name), port);
                        saidaParaOServidor = new PrintWriter(connectionToserver.getOutputStream());
                        String token = "8713681euihkwjasdgauydjhawdklad";
                        saidaParaOServidor.println("CONN TOKEN: " + token);
                        saidaParaOServidor.flush();
                        foundServer = true;
                        System.out.println("Server connected: " + server);
                        break;
                    } catch (Exception e) {
                        continue;
                    }
                }
                assert connectionToserver != null;
                connectionToserver.setKeepAlive(true);

                if (foundServer) {
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
                    if(linhaDeResposta.equals("end")){
                    System.out.println("\nClienteSide--------------Fim da Conexão");
                    } else {
                        throw new InterruptedException("O servidor caiu. Reconectando...");
                    }
                } else {
                    System.out.println("Não foi possivel encontrar um servidor");
                }
            } catch (KeeperException | InterruptedException e) {
                dropTrue = true;
                System.out.println("O servidor não conseguiu responder, possívelmente caiu ou está offline");
            }
        } while (dropTrue);
    }
}

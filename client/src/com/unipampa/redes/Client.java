package com.unipampa.redes;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


class BotServerConnector {
    void connect() throws IOException {

        try {
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
        } catch (SocketException e) {
            System.out.println("O servidor não conseguiu responder, possívelmente caiu ou está offline");
        }
    }
}

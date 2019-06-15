import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

class ExternalConnection {

    public void sendAnimal(AnimalEnumerator tipoAnimal, Animal animal) throws PostgresConnectionErrorException {
        try {
            Socket connectionToserver = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
            connectionToserver.setTcpNoDelay(true);
            connectionToserver.setKeepAlive(true);
            PrintWriter saidaParaOServidor = new PrintWriter(new OutputStreamWriter(connectionToserver.getOutputStream(), StandardCharsets.UTF_8));


            ObjectMapper objectMapper = new ObjectMapper();
            String params = objectMapper.writeValueAsString(animal);
            Console.log(params);
            saidaParaOServidor.println("POST " + tipoAnimal.getUrl() + " HTTP/1.1");
            saidaParaOServidor.println("Host: 127.0.0.1:8888");
            saidaParaOServidor.println("User-Agent: BotServer");
            saidaParaOServidor.println("Content-type: application/json");
            saidaParaOServidor.println("Accept: */*");
            saidaParaOServidor.println("Content-Length: " + params.length() + "");
            saidaParaOServidor.println("");
            saidaParaOServidor.println(params);
            saidaParaOServidor.flush();

            Console.log("GET " + tipoAnimal.getUrl() + " HTTP/1.1");
            Console.log("Host: 127.0.0.1:8888");
            Console.log("User-Agent: BotServer");
            Console.log("Content-type: application/json");
            Console.log("Accept: */*");
            Console.log("Content-Length: " + params.length() + "");
            Console.log("");
            Console.log(params);
            Console.log("\n--------------Inicio");

            BufferedReader respotaDoServidor = new BufferedReader(new InputStreamReader(connectionToserver.getInputStream()));


            String linhaDeResposta;

            boolean error = true;
            while ((linhaDeResposta = respotaDoServidor.readLine()) != null) {
                if (linhaDeResposta.contains("OK")) {
                    Console.log("Tudo certo");
                    error = false;
                }
                Console.log("Aqueduct:: " + linhaDeResposta);
            }

            connectionToserver.close();
            System.out.println("\nBotToAqueduct--------------Fim da Conexão");
            if (error)
                throw new PostgresConnectionErrorException("algum erro aconteceu ao conectar com o banco, veja o console");
        } catch (SocketException e) {
            System.out.println("O servidor de dados não conseguiu responder, possívelmente caiu ou está offline");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Animal> getAnimais(AnimalEnumerator tipoAnimal) throws PostgresConnectionErrorException {
        try {
            Socket connectionToserver = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
            connectionToserver.setTcpNoDelay(true);
            connectionToserver.setKeepAlive(true);
            PrintWriter saidaParaOServidor = new PrintWriter(new OutputStreamWriter(connectionToserver.getOutputStream(), StandardCharsets.UTF_8));


            saidaParaOServidor.println("GET " + tipoAnimal.getUrl() + " HTTP/1.1");
            saidaParaOServidor.println("Host: 127.0.0.1:8888");
            saidaParaOServidor.println("User-Agent: BotServer");
            saidaParaOServidor.println("Content-type: application/json");
            saidaParaOServidor.println("Accept: */*");
            saidaParaOServidor.println("");
            saidaParaOServidor.println("");
            saidaParaOServidor.flush();

            Console.log("GET " + tipoAnimal.getUrl() + " HTTP/1.1");
            Console.log("Host: 127.0.0.1:8888");
            Console.log("User-Agent: BotServer");
            Console.log("Content-type: application/json");
            Console.log("Accept: */*");
            Console.log("");
            Console.log("");


            Console.log("\n--------------Inicio");

            BufferedReader respotaDoServidor = new BufferedReader(new InputStreamReader(connectionToserver.getInputStream()));


            String linhaDeResposta;

            boolean proximaLinhaContemDados = false;
            boolean error = true;
            String dados = "";
            while ((linhaDeResposta = respotaDoServidor.readLine()) != null) {
                if (linhaDeResposta.contains("OK")) {
                    Console.log("Tudo certo");
                    error = false;
                }

                if (linhaDeResposta.equals("") || linhaDeResposta.equals("\r\n")) {
                    Console.log("proxima dados");
                    proximaLinhaContemDados = true;
                } else if (proximaLinhaContemDados) {
                    dados = linhaDeResposta;
                }
                Console.log("Aqueduct:: " + linhaDeResposta);
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Animal> myObjects = null;
            if (!error)
                myObjects = mapper.readValue(dados, mapper.getTypeFactory().constructCollectionType(List.class, Animal.class));

            connectionToserver.close();
            System.out.println("\nBotToAqueduct--------------Fim da Conexão");
            if (error)
                throw new PostgresConnectionErrorException("algum erro aconteceu ao conectar com o banco, veja o console");
            return myObjects;
        } catch (SocketException e) {
            System.out.println("O servidor de dados não conseguiu responder, possívelmente caiu ou está offline");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Animal> getAnimaisPorDono(String dono, String telefone) throws PostgresConnectionErrorException {


        try {
            Socket connectionToserver = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
            connectionToserver.setTcpNoDelay(true);
            connectionToserver.setKeepAlive(true);
            PrintWriter saidaParaOServidor = new PrintWriter(new OutputStreamWriter(connectionToserver.getOutputStream(), StandardCharsets.UTF_8));


            saidaParaOServidor.println("GET /gato?nomeDono=" + dono + "&telefoneDono=" + telefone + " HTTP/1.1");
            saidaParaOServidor.println("Host: 127.0.0.1:8888");
            saidaParaOServidor.println("User-Agent: BotServer");
            saidaParaOServidor.println("Content-type: application/json");
            saidaParaOServidor.println("Accept: */*");
            saidaParaOServidor.println("");
            saidaParaOServidor.println("");
            saidaParaOServidor.flush();

            Console.log("\n--------------Inicio");

            BufferedReader respotaDoServidor = new BufferedReader(new InputStreamReader(connectionToserver.getInputStream()));
            String linhaDeResposta;
            boolean proximaLinhaContemDados = false;
            boolean error = true;
            String dados = "";
            while ((linhaDeResposta = respotaDoServidor.readLine()) != null) {
                if (linhaDeResposta.contains("OK")) {
                    Console.log("Tudo certo");
                    error = false;
                }
                if (linhaDeResposta.equals("") || linhaDeResposta.equals("\r\n")) {
                    Console.log("proxima dados");
                    proximaLinhaContemDados = true;
                } else if (proximaLinhaContemDados) {
                    dados = linhaDeResposta;
                }
                Console.log("Aqueduct:: " + linhaDeResposta);
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Animal> myObjects = null;
            if (!error)
                myObjects = mapper.readValue(dados, mapper.getTypeFactory().constructCollectionType(List.class, Animal.class));

            connectionToserver.close();
            System.out.println("\nBotToAqueduct--------------Fim da Conexão");
            if (error)
                throw new PostgresConnectionErrorException("algum erro aconteceu ao conectar com o banco, veja o console");
            return myObjects;
        } catch (SocketException e) {
            System.out.println("O servidor de dados não conseguiu responder, possívelmente caiu ou está offline");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void setAnimalAdotado(int id) throws PostgresConnectionErrorException {
        noResponseNeeded("PUT", id);
    }

    public void deletarAnimal(int id) throws PostgresConnectionErrorException {
        noResponseNeeded("DELETE", id);
    }

    private void noResponseNeeded(String method, int id) throws PostgresConnectionErrorException {
        try {
            Socket connectionToserver = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
            connectionToserver.setTcpNoDelay(true);
            connectionToserver.setKeepAlive(true);
            PrintWriter saidaParaOServidor = new PrintWriter(new OutputStreamWriter(connectionToserver.getOutputStream(), StandardCharsets.UTF_8));
            saidaParaOServidor.println(method + " /gato/" + id + " HTTP/1.1");
            saidaParaOServidor.println("Host: 127.0.0.1:8888");
            saidaParaOServidor.println("User-Agent: BotServer");
            saidaParaOServidor.println("Content-type: application/json");
            saidaParaOServidor.println("Accept: */*");
            saidaParaOServidor.println("");
            saidaParaOServidor.println("");
            saidaParaOServidor.flush();

            Console.log("\n--------------Inicio");

            BufferedReader respotaDoServidor = new BufferedReader(new InputStreamReader(connectionToserver.getInputStream()));
            String linhaDeResposta;
            boolean error = true;
            while ((linhaDeResposta = respotaDoServidor.readLine()) != null) {
                if (linhaDeResposta.contains("OK")) {
                    Console.log("Tudo certo");
                    error = false;
                }
                Console.log("Aqueduct:: " + linhaDeResposta);
            }
            connectionToserver.close();
            System.out.println("\nBotToAqueduct--------------Fim da Conexão");
            if (error)
                throw new PostgresConnectionErrorException("algum erro aconteceu ao conectar com o banco, veja o console");
        } catch (SocketException e) {
            System.out.println("O servidor de dados não conseguiu responder, possívelmente caiu ou está offline");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    class PostgresConnectionErrorException extends Exception {
        PostgresConnectionErrorException(String error) {
            super(error);
        }

    }
}

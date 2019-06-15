import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class Comunicacao implements Runnable {

    private Socket cliente;

    private EstadoDaAplicacao estadoDaAplicacao = EstadoDaAplicacao.NOT_SET;

    private AnimalEnumerator animalEnumerator = AnimalEnumerator.NOT_SET;

    private StatusDoacao statusDoacao;
    private StatusAdocao statusAdocao;

    private boolean endConnection = false;
    private List<Animal> listaAnimais;
    private Animal animal;
    private Animal animalSelecionado;

    private String dono;
    private String telefone;

    public Comunicacao(Socket cliente) {
        this.cliente = cliente;
    }

    /**
     * metodo para retornar para o cliente que o comando nao foi entendido
     * @param comandoDoCliente
     * @param respostaParaOCliente
     */
    private void naoEntendiOComando(String comandoDoCliente, PrintStream respostaParaOCliente) {
        respostaParaOCliente.println("Server Response: Não entendi o seu comando");
    }


    /**
     * metodo para interpretar o comando do cliente de acordo com o estado da aplicação
     * @param comandoDoCliente
     * @param respostaParaOCliente
     */

    private void interpretarComandoERetornarResultado(String comandoDoCliente, PrintStream respostaParaOCliente) {
        comandoDoCliente = comandoDoCliente.trim();
        try {
            /*Interpreta os comandos de acordo com o estado*/
            switch (estadoDaAplicacao) {
                /*Estado de doação*/
                case DOANDO:
                    /*Caso ainda nao tenha sido selecionado um animal*/
                    if (animalEnumerator == AnimalEnumerator.NOT_SET) {
                        if (comandoDoCliente.equals("\\gato")) {
                            animalEnumerator = AnimalEnumerator.GATO;
                            animal = new Gato();
                            respostaParaOCliente.println(("Informe o nome do animal (\\nome)\n"));
                        } else if (comandoDoCliente.equals("\\cachorro")) {
                            animalEnumerator = AnimalEnumerator.CACHORRO;
                        } else
                            naoEntendiOComando(comandoDoCliente, respostaParaOCliente);
                    } else {
                        /*Caso um animal tenha sido selecionado*/
                        switch (statusDoacao) {
                            case ESPERANDO_NOME:
                                if (comandoDoCliente.startsWith("\\nome")) {
                                    animal.setName(comandoDoCliente.split("\\s+")[1]);
                                    statusDoacao = StatusDoacao.ESPERANDO_FOTO;
                                    respostaParaOCliente.println("Agora informe a foto do animal (\\foto <par>) ");
                                } else {
                                    respostaParaOCliente.println("Por favor, informe o nome do animal (\\nome <par>) ");
                                }
                                break;
                            case ESPERANDO_FOTO:
                                if (comandoDoCliente.startsWith("\\foto")) {
                                    animal.setImage(comandoDoCliente.split("\\s+")[1]);
                                    statusDoacao = StatusDoacao.ESPERANDO_IDADE;
                                    respostaParaOCliente.println("Agora informe a idade do animal (em meses) (\\idade <par>)");
                                } else {
                                    respostaParaOCliente.println("Por favor, informe a foto do animal (\\foto <par>) ");
                                }
                                break;
                            case ESPERANDO_IDADE:
                                if (comandoDoCliente.startsWith("\\idade")) {
                                    animal.setIdade(comandoDoCliente.split("\\s+")[1]);
                                    statusDoacao = StatusDoacao.ESPERANDO_GENERO;
                                    respostaParaOCliente.println("Agora informe o genero do animal (\\genero <par>)");
                                } else {
                                    respostaParaOCliente.println("Por favor, informe a idade do animal (\\idade <par>) ");
                                }
                                break;
                            case ESPERANDO_GENERO:
                                if (comandoDoCliente.startsWith("\\genero")) {
                                    animal.setGenero(comandoDoCliente.split("\\s+")[1]);
                                    statusDoacao = StatusDoacao.ESPERANDO_CONTATO;
                                    respostaParaOCliente.println("Agora informe a o contato do dono do animal (\\contato <nome><telefone>) ");
                                } else {
                                    respostaParaOCliente.println("Por favor, informe o genero do animal (\\genero <par>) ");
                                }
                                break;
                            case ESPERANDO_CONTATO:
                                if (comandoDoCliente.startsWith("\\contato")) {
                                    animal.setNomeDono(comandoDoCliente.split("\\s+")[1]);
                                    animal.setTelefoneDono(comandoDoCliente.split("\\s+")[2]);
                                    respostaParaOCliente.println("Animal cadastrado com sucesso. Espere alguem entra em contato.");
                                    ExternalConnection externalConnection = new ExternalConnection();
                                    externalConnection.sendAnimal(animalEnumerator, animal);
                                    endConnection = true;
                                } else {
                                    respostaParaOCliente.println("Por favor, informe o contato do dono do animal (\\contato <nome><telefone>)");
                                }
                                break;
                        }
                    }
                    break;
                case ADOTANDO:
                    /*Se um tipo de animal não tiver sido selecionado*/
                    if (animalEnumerator == null || animalEnumerator == AnimalEnumerator.NOT_SET) {
                        if (comandoDoCliente.equals("\\gato")) {
                            animalEnumerator = AnimalEnumerator.GATO;
                            statusAdocao = StatusAdocao.LISTANDO_OPCOES;
                        } else if (comandoDoCliente.equals("\\cachorro")) {
                            animalEnumerator = AnimalEnumerator.CACHORRO;
                            statusAdocao = StatusAdocao.LISTANDO_OPCOES;
                        } else
                            naoEntendiOComando(comandoDoCliente, respostaParaOCliente);
                    }
                    /*caso um animal tenha sido selecionado, enumera eles*/
                    if (animalEnumerator != null && animalEnumerator != AnimalEnumerator.NOT_SET && statusAdocao == StatusAdocao.LISTANDO_OPCOES) {
                        ExternalConnection externalConnection = new ExternalConnection();
                        listaAnimais = externalConnection.getAnimais(animalEnumerator);

                        respostaParaOCliente.println("Os listaAnimais disponíveis são os seguintes: ");
                        for (int i = 0; i < listaAnimais.size(); i++) {
                            respostaParaOCliente.println(i + "- " + listaAnimais.get(i).toStringSimple());
                        }
                        respostaParaOCliente.println("Qual você deseja ver as informações?: (\\opcao <par>) ");

                        statusAdocao = StatusAdocao.ESPERANDO_OPCAO;
                        break;
                    /*caso ja tenham sido exibidos os animais, pergunta as opções*/
                    } else if (animalEnumerator != null && animalEnumerator != AnimalEnumerator.NOT_SET && statusAdocao == StatusAdocao.ESPERANDO_OPCAO) {
                        try {
                            if(!comandoDoCliente.startsWith("\\opcao")){
                                respostaParaOCliente.println("Voce deve informar a \\opcao <par>");
                                break;
                            }
                            if (Integer.parseInt(comandoDoCliente.split("\\s+")[1]) >= listaAnimais.size() || Integer.parseInt(comandoDoCliente.split("\\s+")[1]) < 0) {
                                respostaParaOCliente.println("Indice Indisponível");
                                break;
                            }
                            animalSelecionado = listaAnimais.get(Integer.parseInt(comandoDoCliente.split("\\s+")[1]));
                            respostaParaOCliente.println("Informações Completas:" + animalSelecionado.toStringDetalhado());
                            statusAdocao = StatusAdocao.ESPERANDO_SIM_NAO_OUTRO;
                            respostaParaOCliente.println("Deseja o contato do dono desse animal? (\\sim \\nao \\verOutro <par>)");
                        } catch (NumberFormatException number) {
                            respostaParaOCliente.println("Deve-se informar um numero");
                        }
                    } else if (animalEnumerator != null && animalEnumerator != AnimalEnumerator.NOT_SET && statusAdocao == StatusAdocao.ESPERANDO_SIM_NAO_OUTRO) {
                        try {
                            if(!comandoDoCliente.startsWith("\\sim") && !comandoDoCliente.startsWith("\\nao") && !comandoDoCliente.startsWith("\\verOutro")){
                                respostaParaOCliente.println("Voce deve escrever \\sim ou \\nao ou \\verOutro <par> ");
                                break;
                            }
                            if (comandoDoCliente.startsWith("\\sim")) {
                                respostaParaOCliente.println("Contato:" + animalSelecionado.toStringSoContato());
                                respostaParaOCliente.println("Esperamos ver você novamente :)");
                                endConnection = true;
                            } else if (comandoDoCliente.startsWith("\\nao")) {
                                respostaParaOCliente.println("Tchau :)");
                                endConnection = true;
                            } else if (comandoDoCliente.startsWith("\\verOutro")) {
                                if (Integer.parseInt(comandoDoCliente.split("\\s+")[1]) >= listaAnimais.size() || Integer.parseInt(comandoDoCliente.split("\\s+")[1]) < 0) {
                                    respostaParaOCliente.println("Indice Indisponível");
                                    break;
                                }
                                animalSelecionado = listaAnimais.get(Integer.parseInt(comandoDoCliente.split("\\s+")[1]));
                                respostaParaOCliente.println("Informações Completas:" + animalSelecionado.toStringDetalhado());
                                Console.log(Arrays.toString(comandoDoCliente.split("\\s+")));
                                statusAdocao = StatusAdocao.ESPERANDO_SIM_NAO_OUTRO;
                                respostaParaOCliente.println("Deseja o contato do dono desse animal? (\\sim \\nao \\verOutro <par>)");
                            }

                        } catch (NumberFormatException number) {
                            respostaParaOCliente.println("Deve-se informar um numero");
                        }
                    }
                    break;
                case EDITAR_STATUS:
                    if (comandoDoCliente.startsWith("\\adotado")) {
                        try {
                            if (Integer.parseInt(comandoDoCliente.split("\\s+")[1]) >= listaAnimais.size() || Integer.parseInt(comandoDoCliente.split("\\s+")[1]) < 0) {
                                respostaParaOCliente.println("Indice Indisponível");
                                break;
                            }
                            animalSelecionado = listaAnimais.get(Integer.parseInt(comandoDoCliente.split("\\s+")[1]));
                            new ExternalConnection().setAnimalAdotado(animalSelecionado.getId());
                            endConnection=true;
                            respostaParaOCliente.println("Animal foi marcado como adotado com sucesso");
                        }catch (NumberFormatException number){
                            respostaParaOCliente.println("Você deve fornecer um numero");
                        }
                    } else if (comandoDoCliente.startsWith("\\excluir")) {

                        try {
                            if (Integer.parseInt(comandoDoCliente.split("\\s+")[1]) >= listaAnimais.size() || Integer.parseInt(comandoDoCliente.split("\\s+")[1]) < 0) {
                                respostaParaOCliente.println("Indice Indisponível");
                                break;
                            }
                            animalSelecionado = listaAnimais.get(Integer.parseInt(comandoDoCliente.split("\\s+")[1]));
                            new ExternalConnection().deletarAnimal(animalSelecionado.getId());
                            endConnection=true;
                            respostaParaOCliente.println("Animal foi marcado como adotado com sucesso");
                        }catch (NumberFormatException number){
                            respostaParaOCliente.println("Você deve fornecer um numero");
                        }

                    } else naoEntendiOComando(comandoDoCliente, respostaParaOCliente);
                    break;
                case NOT_SET:
                    if (comandoDoCliente.equals("\\doar")) {
                        estadoDaAplicacao = EstadoDaAplicacao.DOANDO;
                        statusDoacao = StatusDoacao.ESPERANDO_NOME;
                        respostaParaOCliente.println("Qual o tipo de animal que deseja doar? (\\gato ou \\cachorro)");
                    } else if (comandoDoCliente.equals("\\adotar")) {
                        respostaParaOCliente.println("Qual o tipo de animal que deseja adotar? (\\gato ou \\cachorro)");
                        estadoDaAplicacao = EstadoDaAplicacao.ADOTANDO;
                    } else if (comandoDoCliente.startsWith("\\modificarStatus")) {
                        dono = comandoDoCliente.split("\\s+")[1];
                        telefone = comandoDoCliente.split("\\s+")[2];
                        listaAnimais = new ExternalConnection().getAnimaisPorDono(dono, telefone);
                        respostaParaOCliente.println("Os listaAnimais para edição disponíveis são os seguintes: ");
                        for (int i = 0; i < listaAnimais.size(); i++) {
                            respostaParaOCliente.println(i + "- " + listaAnimais.get(i).toStringSimple());
                        }
                        respostaParaOCliente.println("Qual você deseja fazer? :(\\excluir <par>  \\adotado) ");
                        estadoDaAplicacao = EstadoDaAplicacao.EDITAR_STATUS;

                    } else naoEntendiOComando(comandoDoCliente, respostaParaOCliente);

            }
        } catch (ArrayIndexOutOfBoundsException E) {
            respostaParaOCliente.println("Falout algum parametro no comando, preencha corretamente");
        } catch (ExternalConnection.PostgresConnectionErrorException e) {
            endConnection = true;
            respostaParaOCliente.println(e.getMessage());
            e.printStackTrace();
        }
        if (animal != null) Console.log("animal: " + animal.toString());
        else Console.log("animal nulo");
    }

    /**
     * Metodo que fica recebendo as mensagens do cliente e controlando as respotas dos comandos interpretados.
     */
    @Override
    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(cliente.getInputStream());
            BufferedReader entrada = new BufferedReader(inputStreamReader);
            PrintStream saida = new PrintStream(cliente.getOutputStream());
            String linha = entrada.readLine().trim();
            try {
                while (linha != null && !endConnection) {
                    Console.log(linha);
                    if (linha.startsWith("CONN TOKEN")) {
                        saida.println("O que você deseja fazer? \\doar, \\adotar ou \\modificarStatus <nome><telefone>) ");
                    } else if (linha.startsWith("\\")) {
                        interpretarComandoERetornarResultado(linha, saida);
                    } else if (linha.equals("exit")) {
                        saida.println("Server Response: Bye :)");
                        break;
                    } else {
                        saida.println("Server Response: " + linha);
                    }
                    if (!endConnection) {
                        saida.println("needComand");
                        Console.log("----esperando comando");

                    } else {
                        break;
                    }
                    linha = entrada.readLine().trim();
                }
            } catch (SocketException e) {
                Console.log("Cliente forçou a saida: " + e.getMessage());
            }
            saida.println("end");
            Console.log("Cliente Desconectou");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





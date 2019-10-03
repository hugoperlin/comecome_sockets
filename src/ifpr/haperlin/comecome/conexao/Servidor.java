package ifpr.haperlin.comecome.conexao;

import ifpr.haperlin.comecome.model.Controlador;
import ifpr.haperlin.comecome.model.Jogador;
import ifpr.haperlin.comecome.utils.NomeInvalido;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor implements Runnable {

    private ServerSocket servidor;
    private int PORT=12700;
    private String HOST="localhost";

    private ExecutorService executor = Executors.newScheduledThreadPool(20);


    public Servidor() throws IOException {

        servidor = new ServerSocket(PORT);

    }

    public void run(){
        while (true){
            try{
                System.out.println("Aguardando conexao...");
                Socket cliente  = servidor.accept();
                System.out.println("Conectado..."+cliente.getInetAddress());
                Jogador j = Controlador.instance.novoJogador(cliente);

                executor.execute(j);



            }catch (IOException e){

            }catch (NomeInvalido e){

            }

        }



    }






}

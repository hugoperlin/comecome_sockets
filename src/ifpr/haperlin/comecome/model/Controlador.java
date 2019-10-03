package ifpr.haperlin.comecome.model;

import ifpr.haperlin.comecome.conexao.Servidor;
import ifpr.haperlin.comecome.utils.NomeInvalido;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controlador {

    private Lock lock;
    private ObservableList<Jogador> jogadores;
    private ObservableList<Comida> comidas;
    private Pane mundo=null;
    private Set<Paint> coresUsadas;
    private Servidor servidor;
    private Thread threadServidor;


    public static Controlador instance = new Controlador();

    private Controlador(){
        jogadores = FXCollections.observableArrayList();
        comidas = FXCollections.observableArrayList();
        lock = new ReentrantLock();
        coresUsadas = new HashSet<>();

        try{
            servidor = new Servidor();
            threadServidor = new Thread(servidor);
            threadServidor.start();
        }catch (IOException e){
            e.printStackTrace();
        }


    }




    public void setMundo(Pane mundo){
        this.mundo = mundo;
    }

    public ObservableList<Comida> getComidas(){
        return comidas;
    }

    public ObservableList<Jogador> getJogadores(){
        return jogadores;
    }


    public void removeJogador(Jogador j){
        lock.lock();
        jogadores.remove(j);
        lock.unlock();
    }

    public Jogador novoJogador(Socket socket) throws IOException,NomeInvalido {
        Paint cor;
        do{
            cor = Color.color(Math.random(),Math.random(),Math.random());
        }while (coresUsadas.contains(cor));


        Jogador j = new Jogador(""+((int)Math.random()*100),Math.random()*mundo.getWidth(),Math.random()*mundo.getHeight(),10,cor,socket);
        adicionaJogador(j);

        /*Platform.runLater(()->{
            mundo.getChildren().add(j);
        });*/

        return j;
    }


    public void adicionaJogador(Jogador j){
        lock.lock();
        jogadores.add(j);
        lock.unlock();
    }

    public void removeComida(Comida c){
        comidas.remove(c);
    }

    public void adicionaComida(Comida c){
        comidas.add(c);
    }


    public void detectaColisoes(){

        jogadores.forEach(jogador -> {

            comidas.forEach(comida->{
                if(jogador.getBoundsInParent().intersects(comida.getBoundsInParent())){
                    jogador.comeu(comida);
                    comida.setComida();
                }
            });

            jogadores.forEach(outroJogador -> {
                if(jogador != outroJogador){
                    if(jogador.estaVivo() && outroJogador.estaVivo()){
                        if(jogador.getBoundsInParent().intersects(outroJogador.getLayoutBounds())){
                            if(jogador.getRadius()>outroJogador.getRadius()){
                                outroJogador.morreu();
                            }else{
                                jogador.morreu();
                            }
                        }
                    }

                }
            });
        });


    }


    public void movimenta(double speed){
        jogadores.forEach(jogador -> {

            if(jogador.estaVivo()) {
                jogador.doMove(speed);
                if (jogador.getTranslateX() > mundo.getWidth()) {
                    jogador.setTranslateX(0);
                } else if (jogador.getTranslateX() < 0) {
                    jogador.setTranslateX(mundo.getWidth());
                }
                if (jogador.getTranslateY() > mundo.getHeight()) {
                    jogador.setTranslateY(0);
                } else if (jogador.getTranslateY() < 0) {
                    jogador.setTranslateY(mundo.getHeight());
                }
            }
        });
    }


    public void adicionaComida(){

        if(comidas.size()<100){

            double tamanho=0;
            do{
                tamanho = Math.random()*10;
            }while (tamanho < 5);

            Comida c = new Comida(Math.random()*mundo.getWidth(),Math.random()*mundo.getHeight(),tamanho,Math.random()<0.05);
            comidas.add(c);


        }


    }


    public void limpa(){
        lock.lock();
        jogadores.removeIf(jogador -> !jogador.estaVivo());
        comidas.removeIf(comida -> comida.foiComida());
        lock.unlock();
    }




}

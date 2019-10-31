package ifpr.haperlin.comecome.model;

import ifpr.haperlin.comecome.utils.Move;
import ifpr.haperlin.comecome.utils.NomeInvalido;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.Socket;


public class Jogador extends Circle implements Runnable{



    private String nome;
    private Move direction = Move.None;
    private boolean vivo = true;


    private Socket socket;
    private BufferedReader entrada;
    private BufferedWriter saida;

    public Jogador(String nome, double x, double y, double tamanho, Paint cor, Socket socket) throws NomeInvalido,IOException {
        if(nome.length() > 3){
            throw new NomeInvalido();
        }

        this.nome = nome;
        this.setRadius(tamanho);
        setTranslateX(x);
        setTranslateY(y);
        setFill(cor);

        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.saida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    public void comeu(Comida comida){
        if(comida.isEnvenenada()){
            vivo = false;
        }else{
            setRadius(getRadius()+comida.getRadius()*0.1);
        }
    }

    public boolean estaVivo(){
        return vivo;
    }


    public void setMove(Move direction){
        this.direction = direction;
    }

    public void doMove(double speed){
        switch (this.direction){
            case Left:
                moveLeft(speed);
                break;
            case Rigth:
                moveRight(speed);
                break;
            case Up:
                moveUp(speed);
                break;
            case Down:
                moveDown(speed);
                break;
        }
        //this.direction = Move.None;
    }

    void moveLeft(double speed) {
        setTranslateX(getTranslateX() - 5*speed);
    }

    void moveRight(double speed) {
        setTranslateX(getTranslateX() + 5*speed);
    }

    void moveUp(double speed) {
        setTranslateY(getTranslateY() - 5*speed);
    }

    void moveDown(double speed) {
        setTranslateY(getTranslateY() + 5*speed);
    }

    public String getNome(){
        return nome;
    }

    public void run(){

        try{
            String nome = entrada.readLine();
            System.out.println(nome);
            this.nome = nome;
            saida.write("ok\n");
            saida.flush();
            while(vivo){

                String msg = entrada.readLine();
                if(msg.equals("sair")){
                    break;
                }else {

                    try {
                        setMove(Move.valueOf(msg));
                        saida.write("ok\n");
                        saida.flush();
                    } catch (Exception e) {
                        saida.write("Inválido\n");
                        saida.flush();
                    }
                }
            }

            saida.write("Morreu!! Game Over\n");
            saida.flush();

        }catch (IOException e){
            e.printStackTrace();
            vivo = false;
        }


        fecha();


    }

    public void morreu(){
        vivo = false;
    }

    private void fecha(){
        try{
            entrada.close();
            saida.close();
            socket.close();
        }catch (IOException e){

        }
    }


}

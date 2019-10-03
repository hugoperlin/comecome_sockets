package ifpr.haperlin.comecome.control;

import ifpr.haperlin.comecome.model.Comida;
import ifpr.haperlin.comecome.model.Controlador;
import ifpr.haperlin.comecome.model.Jogador;
import ifpr.haperlin.comecome.utils.Move;
import ifpr.haperlin.comecome.utils.NomeInvalido;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {


    @FXML
    private Pane pane;

    private Text nomeJogadores;

    public void initialize(){


        Controlador.instance.setMundo(pane);

        nomeJogadores = new Text();
        nomeJogadores.setText("Jogadores OnLine");
        nomeJogadores.setX(20);
        nomeJogadores.setY(20);

        pane.getChildren().add(nomeJogadores);


        //monitora mudanças na lista de jogadores e atualiza o mundo de acordo com a mudança
        Controlador.instance.getJogadores().addListener(new ListChangeListener<Jogador>() {
            @Override
            public void onChanged(Change<? extends Jogador> change) {
                change.next();
                if(change.wasAdded()){
                    Platform.runLater(()->pane.getChildren().add(change.getAddedSubList().get(0)));
                }else if(change.wasRemoved()){
                    Platform.runLater(()->pane.getChildren().remove(change.getRemoved().get(0)));
                }
            }
        });

        //monitora mudanças na lista de comidas e atualiza o mundo de acordo com a mudança
        Controlador.instance.getComidas().addListener(new ListChangeListener<Comida>() {
            @Override
            public void onChanged(Change<? extends Comida> change) {
                change.next();
                if(change.wasAdded()){
                    Platform.runLater(()->pane.getChildren().add(change.getAddedSubList().get(0)));
                }else if(change.wasRemoved()){
                    Platform.runLater(()->pane.getChildren().remove(change.getRemoved().get(0)));
                }
            }
        });



        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastUpdate ;

            private double speed = 50 ; // pixels per second

            @Override
            public void start() {
                lastUpdate = System.nanoTime();
                super.start();
            }

            @Override
            public void handle(long now) {
                long elapsedNanoSeconds = now - lastUpdate ;
                double elapsedSeconds = elapsedNanoSeconds / 1_000_000_000.0 ;
                update(elapsedSeconds*speed);
                lastUpdate = now;
            }
        };

        animationTimer.start();


    }


    private void update(double speed){


        //adiciona comida ou veneno no mundo
        Controlador.instance.adicionaComida();

        //movimenta os jogadores utilizando um passo de tempo
        Controlador.instance.movimenta(speed);

        //detecta colisoes entre os jogador<->jogador e jogador<->comida
        Controlador.instance.detectaColisoes();

        //limpa as coisas que morreram ou foram comidas
        Controlador.instance.limpa();


    }


}

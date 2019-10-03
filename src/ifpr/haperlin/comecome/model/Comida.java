package ifpr.haperlin.comecome.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;



public class Comida extends Circle {


    private boolean envenenada=false;
    private boolean comida=false;


    public Comida(double x, double y, double tamanho, boolean envenenada){

        setTranslateX(x);
        setTranslateY(y);
        setRadius(tamanho);

        this.envenenada = envenenada;

        if(envenenada){
            setFill(Color.GREEN);
        }else{
            setFill(Color.RED);
        }
    }


    public boolean isEnvenenada(){
        return envenenada;
    }

    public void setComida(){
        comida = true;
    }

    public boolean foiComida(){
        return comida;
    }




}

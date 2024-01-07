/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import common.IToJsonObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONObject;

/**
 *
 * @author juanangel
 */
public class Bee extends AbstractGameObject {
    
    //Lista de objetos para que pueda  cojer las flores
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<>();
    
    public Bee(){}
    
    public Bee(Position position) {
        super(position);    
    }
      
    public Bee(Position position, int value){
        super(position, value, 1);
    }
    
    public Bee(Position position, int value, int life){
        super(position, value, life);
    }
    
    public Bee(Position position, int value, int life, ConcurrentLinkedQueue<IGameObject> gObjs) {
        super(position, value, life);    
        this.gObjs = gObjs;
    }
    
    public Bee(JSONObject obj){
        super(obj);
    }    
    
    public void printBee(){
        System.out.println(this.toJSONObject());
    }
    
    public void moveRandomly(){
        
        int x_actual = getPosition().getX();
        int y_actual = getPosition().getY();
        
        //Cogemos un numero aleatorio entre -1, 0 y 1
        int x_random = (int) (Math.random() * 3) - 1;
        int y_random = (int) (Math.random() * 3) - 1;

        // Actualizamos la posicion
        int nueva_x = x_actual + x_random;
        int nueva_y = y_actual + y_random;

        setPosition(new Position(nueva_x, nueva_y));
        
    }
}

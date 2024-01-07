/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import common.IToJsonObject;
import org.json.JSONObject;


/**
 *
 * @author juanangel
 */
public class Fly extends AbstractGameObject {
    
    public Fly(){}
    
    public Fly(Position position) {
        super(position);    
    }
      
    public Fly(Position position, int value){
        super(position, value, 1);
    }
    
    public Fly(Position position, int value, int life){
        super(position, value, life);
    }
    
    public Fly(JSONObject obj){
        super(obj);
    }   
    
    public void printFly(){
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

        // Comprobamos que la nueva posición no esta fuera de los limites del tablero (480 = CANVAS_WIDTH y 40 = boxSize)
        nueva_x = Math.max(0, Math.min(nueva_x, 480 / 40 - 1));
        nueva_y = Math.max(0, Math.min(nueva_y, 480 / 40 - 1));

        setPosition(new Position(nueva_x, nueva_y));
        
    }
}

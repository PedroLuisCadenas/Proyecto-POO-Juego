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
public class Spider extends AbstractGameObject {
    
    public Spider(){}
    
    public Spider(Position position) {
        super(position);    
    }
      
    public Spider(Position position, int value){
        super(position, value, 1);
    }
    
    public Spider(Position position, int value, int life){
        super(position, value, life);
    }
    
    public Spider(JSONObject obj){
        super(obj);
    }   
    
    public void printSpider(){
        System.out.println(this.toJSONObject());
    }
    
    public void seguirRidingHood(RidingHood_2 rh){
        
        //Guardamos las posiciones actuales de la araña y de caperucita
        int spider_X = position.getX();
        int spider_Y = position.getY();
        int rh_X = rh.getPosition().getX();
        int rh_Y = rh.getPosition().getY();
        
        //Calculamos la diferencia entre las posiciones de la araña y caperucita
        int diferencia_x = rh_X - spider_X;
        int diferencia_y = rh_Y - spider_Y;
        
        //Sumamos esa diferencia a una variable de nueva posición y la ponemos en la variable posición de la araña
        int nueva_X = spider_X + Integer.compare(diferencia_x, 0);
        int nueva_Y = spider_Y + Integer.compare(diferencia_y, 0);
        position = new Position(nueva_X, nueva_Y);
    }
           
}


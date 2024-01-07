/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Pedro Luis
 */

//Clase de prueba de los movimientos del objeto fly
public class TestFlees extends JFrame implements KeyListener, ActionListener {

    // KeyBoard
    public static final int UP_KEY    = 38;
    public static final int DOWN_KEY  = 40;
    public static final int RIGTH_KEY = 39;
    public static final int LEFT_KEY  = 37;
    public static final int SPACE_KEY = 32;
    int lastKey = DOWN_KEY;
    
    // Game Panel and 
    public static final int CANVAS_WIDTH = 480;    
    int boxSize = 40;
    int row, col;
    GameCanvas canvas;
    JPanel canvasFrame;
    JLabel dataLabel;
    
    // Timer
    Timer timer;
    int tick = 200;
    
    // Game Variables
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<>();
    RidingHood_2 ridingHood = new RidingHood_2(new Position(0,0), 1, 1);

    
    public TestFlees() throws Exception{

       super("TestFlees");
       //Creamos el botón para pausar y reanudar la partida
       JButton pausar = new JButton("Pausar/Reanudar");
       
       // Game Initializations.
       gObjs.add(ridingHood);
       loadNewBoard();
  
       // Window initializations.
       dataLabel = new JLabel(ridingHood.toString());
       dataLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)); 
       dataLabel.setPreferredSize(new Dimension(120,40));
       dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
       canvas = new GameCanvas(CANVAS_WIDTH, boxSize);
       canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_WIDTH));
       canvas.setBorder(BorderFactory.createLineBorder(Color.blue));
       
       canvasFrame = new JPanel();
       canvasFrame.setPreferredSize(new Dimension(CANVAS_WIDTH + 40, CANVAS_WIDTH + 40));
       canvasFrame.add(canvas);
       
       getContentPane().add(canvasFrame);
       getContentPane().add(dataLabel, BorderLayout.SOUTH);
       //Añadimos el botón de pausar en la parte superior de la pantalla
       getContentPane().add(pausar, BorderLayout.NORTH);
       //Añadimos la acción de que pause la partida y el temporizador
       pausar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                if (timer.isRunning()){
                    timer.stop();
                }
                else{
                    timer.start();
                }
            }
        });
       
       setSize (CANVAS_WIDTH + 40, CANVAS_WIDTH + 80);
       setResizable(false);
       setVisible(true);         
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
       
       addKeyListener(this);
       this.setFocusable(true);
       timer = new Timer(tick, this);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    // Version 1
    @Override
    public void keyPressed(KeyEvent ke) {
        lastKey = ke.getKeyCode(); 
        if (lastKey == SPACE_KEY){
            if (timer.isRunning()){
                    timer.stop();
                }
                else{
                    timer.start();
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    /**
     * Se invoca en cada tick de reloj
     * @param ae 
     */  
    @Override
    public void actionPerformed(ActionEvent ae) {
       
        
        // Moving Caperucita
        ridingHood.moveToNextPosition();
        
        //Recorremos la lista de gObjs hasta encontrar una mosca y hacemos que se mueva aleatoriamente
        for (IGameObject gObj : gObjs) {
            if (gObj instanceof Fly fly) {
                fly.moveRandomly();
            }
        }
        
        // Check if Caperucita is in board limits
        setInLimits();
        
        // Logic to change to a new screen.
        if (processCell() == 1){
            loadNewBoard();
        }
        
        // Updating graphics and labels
        dataLabel.setText(ridingHood.toString());
        canvas.drawObjects(gObjs);
    }

    /*
    Procesa la celda en la que se encuentra caperucita.
    Si Caperucita está sobre un blossom añade su valor al de Caperucita
    y lo elimina del tablero.
    Devuelve el número de blossoms que hay en el tablero.
    */    
    private int processCell(){
        Position rhPos = ridingHood.getPosition();
        
        //Recorremos la lista y si el objeto es una flor añadimos su valor y lo quitamos de la lista de objetos
        for (IGameObject gObj: gObjs){
            if(gObj != ridingHood && rhPos.isEqual(gObj.getPosition())){
                if(gObj instanceof Blossom blossom){
                    int v = ridingHood.getValue() + gObj.getValue();
                    ridingHood.setValue(v);
                    gObjs.remove(blossom);
                }
            }
        }
        
        //Comprobamos que en la lista no queda ninguna flor y hacemos que pase de pantalla (para no tener que toparse con cualquier otro objeto)
        if(gObjs.stream().noneMatch(obj -> obj instanceof Blossom)){
            gObjs.removeIf(obj -> obj instanceof Fly); //Eliminamos la mosca del tablero para pasar a la siguiente pantalla
            return 1;
        }
        
        //Devuelve 0 si aun queda alguna flor en pantalla
        return 0;
    }
    
    /*
    Comprueba que Caperucita no se sale del tablero.
    En caso contrario corrige su posición
    */
    private void setInLimits(){
        
        int lastBox = (CANVAS_WIDTH/boxSize) - 1;
        
        if (ridingHood.getPosition().getX() < 0){
            ridingHood.position.x = 0;
        }
        else if ( ridingHood.getPosition().getX() > lastBox ){
            ridingHood.position.x = lastBox;
        }
        
        if (ridingHood.getPosition().getY() < 0){
            ridingHood.position.y = 0;
        }
        else if (ridingHood.getPosition().getY() > lastBox){
            ridingHood.position.y = lastBox;
        } 
    }
    
    /*
    Carga un nuevo tablero
    */
    private void loadNewBoard(){
        
        gObjs.add(new Blossom(new Position(2,2), 10, 10));
        gObjs.add(new Blossom(new Position(2,8), 4, 10));
        gObjs.add(new Blossom(new Position(8,8), 10, 10));
        gObjs.add(new Fly(new Position(8,2), 4, 10));
              
    }
    
    public static void main(String [] args) throws Exception{
       TestFlees gui = new TestFlees();
    }
}

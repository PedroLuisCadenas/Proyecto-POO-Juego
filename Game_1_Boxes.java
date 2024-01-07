/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

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
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author pedroluis
 */
public class Game_1_Boxes extends JFrame implements KeyListener, ActionListener {

    // KeyBoard
    public static final int UP_KEY    = 38;
    public static final int DOWN_KEY  = 40;
    public static final int RIGTH_KEY = 39;
    public static final int LEFT_KEY  = 37;
    public static final int SPACE_KEY = 32;
    int lastKey = DOWN_KEY;
    
    // Game Panel and game info panels
    public static final int CANVAS_WIDTH = 480;    
    int boxSize = 40;
    int row, col;
    GameCanvas_2 canvas;
    JPanel canvasFrame;
    
    
    // Timer
    Timer timer;
    int tick = 200;
        
    // Game Variables
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<>();
    RidingHood_2 ridingHood = new RidingHood_2(new Position(0,0), 1, 1);
    int screenCounter = 0;

    
    public Game_1_Boxes() throws Exception{

       super("Game_1_Boxes");
       //Creamos el botón para pausar y reanudar la partida
       JButton pausar = new JButton("Pausar/Reanudar");
       
       // Game Initializations.
       gObjs.add(ridingHood);
       loadNewBoard(0);
       
       // Window initializations.
       canvas = new GameCanvas_2(CANVAS_WIDTH, boxSize);
       canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_WIDTH));
       canvas.setBorder(BorderFactory.createLineBorder(Color.blue));
       
       canvasFrame = new JPanel();
       canvasFrame.setPreferredSize(new Dimension(CANVAS_WIDTH + 40, CANVAS_WIDTH + 40));
       canvasFrame.add(canvas);
       //Añadimos el botón de pausa al canvasFrame
       canvasFrame.add(pausar);
       getContentPane().add(canvasFrame);
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
    
    public void actionPerformed (ActionEvent ae) {
        
        // Actions on Caperucita
        setDirection(lastKey);
        
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
            screenCounter++;
            ridingHood.incLifes(1);
            loadNewBoard(screenCounter);
        };
        
        // Updating graphics and labels
        canvas.drawObjects(gObjs);
    }
    
    /*
    Procesa la celda en la que se encuentra caperucita.
    */
    private int processCell(){
        return 0;
    }
    
    /*
    Fija la dirección de caperucita.
    Caperucita se moverá en esa dirección cuando se invoque
    su método moveToNextPosition.
    */
    private void setDirection(int lastKey){
        switch (lastKey) {
            case UP_KEY:  
                ridingHood.moveUp();
                break;
            case DOWN_KEY:
                ridingHood.moveDown();                    
                break;
            case RIGTH_KEY:
                ridingHood.moveRigth();
                break;
            case LEFT_KEY:
                ridingHood.moveLeft();
                break; 
        }
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
    private void loadNewBoard(int counter){
        switch(counter){
            case 0: 
            default:
              gObjs.add(new Blossom(new Position(2,2), 10, 10));
              gObjs.add(new Blossom(new Position(2,8), 4, 10));
              gObjs.add(new Blossom(new Position(8,8), 10, 10));
              gObjs.add(new Blossom(new Position(8,2), 4, 10));  
        }        
    }
    
    
    
    public static void main(String [] args) throws Exception{
       Game_1_Boxes gui = new Game_1_Boxes();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import common.FileUtilities;
import static common.IToJsonObject.TypeLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Pedro Luis
 */
public class Game_Boxes extends JFrame implements KeyListener, ActionListener {

    // KeyBoard
    public static final int UP_KEY    = 38;
    public static final int DOWN_KEY  = 40;
    public static final int RIGTH_KEY = 39;
    public static final int LEFT_KEY  = 37;
    public static final int SPACE_KEY = 32;
    int lastKey = DOWN_KEY;
    int modo = 0; //Variable para saber el modo en el que se mueve caperucita (manual = 0, auto = 1)
    
    // Game Panel and 
    public static final int CANVAS_WIDTH = 480;    
    int boxSize = 40;
    int row, col;
    GameCanvas_2 canvas;
    JPanel canvasFrame;
    JLabel dataLabel;
    
    // Timer
    Timer timer;
    int tick = 200;
    
    // Game Variables
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<>();
    RidingHood_2 ridingHood = new RidingHood_2(new Position(0,0), 1, 1, gObjs);
    int screenCounter = 0;

    
    public Game_Boxes() throws Exception{

       super("Game_Boxes");
       //Creamos el botón para pausar y reanudar la partida
       JButton pausar = new JButton("Pausar/Reanudar");
       pausar.setPreferredSize(new Dimension(140,40)); //Ajustamos su tamaño
       //Creamos el botón para cambiar el modo de caperucita
       JButton modo_b = new JButton("Manual/Auto");
       modo_b.setPreferredSize(new Dimension(120,40)); //Ajustamos su tamaño
       //Creamos los botones de carga y guarda de partida
       JButton guardar = new JButton("Guardar");
       JButton cargar = new JButton("Cargar");
       guardar.setPreferredSize(new Dimension(95,40)); //Ajustamos su tamaño
       cargar.setPreferredSize(new Dimension(95,40)); //Ajustamos su tamaño
       
       //Creamos un panel para los botones
       JPanel botones = new JPanel(new FlowLayout());
       //Los añadimos al panel
       botones.add(modo_b);
       botones.add(pausar);
       botones.add(guardar);
       botones.add(cargar);
       
       // Game Initializations.
       gObjs.add(ridingHood);
       loadNewBoard(0);
  
       // Window initializations.
       dataLabel = new JLabel(ridingHood.toString());
       dataLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)); 
       dataLabel.setPreferredSize(new Dimension(120,40));
       dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
       canvas = new GameCanvas_2(CANVAS_WIDTH, boxSize);
       canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_WIDTH));
       canvas.setBorder(BorderFactory.createLineBorder(Color.blue));
       
       canvasFrame = new JPanel();
       canvasFrame.setPreferredSize(new Dimension(CANVAS_WIDTH + 40, CANVAS_WIDTH + 40));
       canvasFrame.add(canvas);
       
       getContentPane().add(canvasFrame);
       getContentPane().add(dataLabel, BorderLayout.SOUTH);
       //Añadimos el botón de pausar en la parte superior de la pantalla
       getContentPane().add(botones, BorderLayout.NORTH);
       
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
        
        modo_b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                if(modo == 0){
                    modo = 1;
                }
                else {
                    modo = 0;
                }
            }
        });
        
        guardar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                guardarJuego();
            }
        });
        
        cargar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                cargarJuego();
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
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    /**
     * Se invoca en cada tick de reloj
     * @param ae 
     */  
    @Override
    public void actionPerformed(ActionEvent ae) {
       
        // Actions on Caperucita
        setDirection(lastKey);
        
        // Moving Caperucita
        if(modo == 0){
            ridingHood.moveToNextPosition();
        }
        else if(modo == 1){
            ridingHood.moveToNextPosition_auto();
        }
        
        //Recorremos la lista de gObjs hasta encontrar una mosca y hacemos que se mueva aleatoriamente
        for (IGameObject gObj : gObjs) {
            if (gObj instanceof Fly fly) {
                fly.moveRandomly();
                //Si las posiciones coinciden le restamos vida a caperucita y eliminamos la mosca del tablero
                if(fly.getPosition().isEqual(ridingHood.getPosition())){
                    ridingHood.decLifes(1);
                    gObjs.remove(fly);
                }
            }
            
            //Hacemos que las abejas vayan hacia las flores
            else if (gObj instanceof Bee bee){
                bee.moveRandomly();
                //Si las posiciones coinciden le restamos vida a caperucita y eliminamos la abeja del tablero
                if(bee.getPosition().isEqual(ridingHood.getPosition())){
                    ridingHood.decLifes(1);
                    gObjs.remove(bee);
                }
            }    
            
            else if (gObj instanceof Spider spider){
                spider.seguirRidingHood(ridingHood);
                //Si las posiciones coinciden le restamos vida a caperucita y la araña desaparece
                if(spider.getPosition().isEqual(ridingHood.getPosition())){
                    ridingHood.decLifes(1);
                    gObjs.remove(spider);
                }
            }
        }
        
        // Check if Caperucita is in board limits
        setInLimits();
        
        // Logic to change to a new screen.
        if (processCell() == 1){
            screenCounter++;
            ridingHood.incLifes(1);
            loadNewBoard(screenCounter);
        }
        
        //Si caperucita llega a 0 vidas se elimina a caperucita, muestra el mensaje y se para el juego
        if (ridingHood.getLifes() == 0){
            gObjs.remove(ridingHood);
            System.out.println("HAS PERDIDO");
            timer.stop();
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
            
            //Si es una abeja recorremos otra vez la lista hasta encontrar una flor y si sus posiciones coinciden, eliminamos esa flor
            else if (gObj instanceof Bee bee){
                for (IGameObject gObj2 : gObjs){
                    if(gObj2 instanceof Blossom blossom){
                        if(bee.getPosition().isEqual(blossom.getPosition())){
                            gObjs.remove(blossom);
                        }
                    }
                }
            }
        }
        
        eliminarAbejas(); //Eliminamos a las abejas del tablero si se salen de los limites
        
        //Comprobamos que en la lista no queda ninguna flor y hacemos que pase de pantalla (para no tener que toparse con cualquier otro objeto)
        if(gObjs.stream().noneMatch(obj -> obj instanceof Blossom)){
            gObjs.removeIf(obj -> obj instanceof Fly); //Eliminamos la mosca del tablero para que al pasar a la siguiente pantalla no se mantenga
            gObjs.removeIf(obj -> obj instanceof Bee); //Eliminamos la abeja del tablero para que al pasar a la siguiente pantalla no se mantenga
            gObjs.removeIf(obj -> obj instanceof Spider); //Eliminamos la araña del tablero para que al pasar a la siguiente pantalla no se mantenga
            return 1;
        }
        
        //Devuelve 0 si aun queda alguna flor en pantalla
        return 0;
    }
    
    //Método para eliminar las abejas que se salgan de los limites del tablero
    private void eliminarAbejas() {
        int lastBox = (CANVAS_WIDTH / boxSize) - 1;
    
        gObjs.removeIf(obj -> obj instanceof Bee && 
                (obj.getPosition().getX() < 0 || obj.getPosition().getX() > lastBox ||
                 obj.getPosition().getY() < 0 || obj.getPosition().getY() > lastBox));
    }
    
    //Método para guardar la partida
    private void guardarJuego(){
        try{
            //Creamos un objeto JSON donde guardaremos todos los datos del juego
            JSONObject datosJuego = new JSONObject();
            
            //Introducimos el screenCounter para saber la pantalla por la que va y a caperucita para saber sus datos
            datosJuego.put("screenCounter", screenCounter);
            datosJuego.put("ridingHood",ridingHood.toJSONObject());
            
            //Creamos un array JSON para guardar todos los objetos de la pantalla 
            JSONArray objetosJuego = new JSONArray();
            for (IGameObject gObj : gObjs) {
                if(gObj instanceof Bee bee) {
                    objetosJuego.put(bee.toJSONObject());
                }
                else if(gObj instanceof Fly fly){
                    objetosJuego.put(fly.toJSONObject());
                }
                else if(gObj instanceof Spider spider){
                    objetosJuego.put(spider.toJSONObject());
                }
                else if(gObj instanceof Blossom blossom){
                    objetosJuego.put(blossom.toJSONObject());
                }
            }
            
            //Pasamos los objetos a los datos
            datosJuego.put("objetosJuego", objetosJuego);
            
            //Escribimos los datos obtenidos en un fichero (crear fichero previamente)
            FileUtilities.writeToFile( datosJuego.toString(), "src/main/resources/juego.json");
            
            
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //Método para cargar la partida
    private void cargarJuego(){
        try{
            //Creamos un array JSON que nos pasa todo lo que haya en el archivo indicado
            JSONArray jsonString = FileUtilities.readJsonsFromFile("src/main/resources/juego.json");
            
            //Comprobamos si esta vacío
            if(jsonString != null && jsonString.length() != 0){
                //Creamos un objeto JSON donde guardaremos los datos
                JSONObject datosJuego = jsonString.getJSONObject(0);
                
                //Sobreescribimos los nuevos datos en los actuales 
                screenCounter = datosJuego.getInt("screenCounter");
                                
                gObjs.clear(); //Limpiamos el array de objetos antes de empezar a poner los nuevos
                
                JSONArray objetosJuego = datosJuego.getJSONArray("objetosJuego");
                
                //Recorremos el array JSON y vamos añadiendo cada objeto en el array de objetos
                for(int i = 0; i<objetosJuego.length(); i++){
                    JSONObject datosObjeto = objetosJuego.getJSONObject(i);
                    IGameObject gameObject = crearGameObjectDeJSON(datosObjeto);
                    if(gameObject != null) {
                        gObjs.add(gameObject);
                    }
                }
                //Cargamos los datos sobre la variable y añadimos a caperucita de nuevo a la lista de objetos
                ridingHood = new RidingHood_2 (datosJuego.getJSONObject("ridingHood"));
                gObjs.add(ridingHood);
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //Método para pasar identificar cada objeto del fichero y devolverlo
    private IGameObject crearGameObjectDeJSON(JSONObject objData) {
        try {
            // Identificar el tipo de objeto y crear la instancia correspondiente
            if (objData.has(TypeLabel)) {
                String typeLabel = objData.getString(IGameObject.TypeLabel);
                switch (typeLabel) {
                    case "Blossom":
                        return new Blossom(objData);
                    case "Fly":
                        return new Fly(objData);
                    case "Bee":
                        return new Bee(objData);
                    case "Spider":
                        return new Spider(objData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        switch(counter % 3){
            case 0: 
              gObjs.add(new Blossom(getRandomPosition(10,10), 10, 10));
              gObjs.add(new Blossom(getRandomPosition(10,10), 4, 10));
              gObjs.add(new Blossom(getRandomPosition(10,10), 10, 10));
              for(int i = 0; i<counter; i++){
                  gObjs.add(new Fly(getRandomPosition(10, 10), 10, 10));
              }
              break;
            case 1:
              gObjs.add(new Blossom(getRandomPosition(10,10), 10, 10));
              gObjs.add(new Blossom(getRandomPosition(10,10), 4, 10));
              gObjs.add(new Blossom(getRandomPosition(10,10), 10, 10));
              for(int i = 0; i<counter; i++){
                  gObjs.add(new Bee(getRandomPosition(10, 10), 10, 10, gObjs));
              }
              break;
            case 2:
              gObjs.add(new Blossom(getRandomPosition(10,10), 10, 10));
              gObjs.add(new Blossom(getRandomPosition(10,10), 4, 10));
              gObjs.add(new Blossom(getRandomPosition(10,10), 10, 10));
              for(int i = 1; i<counter; i++){
                  gObjs.add(new Spider(getRandomPosition(10, 10), 10, 10));
              }
              counter = 1;
              break;
        }        
    }
    
    public Position getRandomPosition(int mX, int mY){
        int x = (int)(mX * Math.random());
        int y = (int)(mY * Math.random());
        return new Position(x, y);
    }
    
    public static void main(String [] args) throws Exception{
       Game_Boxes gui = new Game_Boxes();       
    }
}

import processing.core.PApplet;
import processing.core.PImage;

/**The main class that handles using processing**/
public class Main extends PApplet {

    /**The main game class that will do everything for the game of minesweeper**/
    private Game game;

    /**The images that will be used to draw the game**/
    private PImage[] images = new PImage[24];

    /**
     * The main routine used to start up the processing PApplet
     * @param args unused
     */
    public static void main(String[] args) {
        PApplet.main("Main",args);
    }

    /**This method is called once to set up the settings for what processing is going to draw**/
    public void settings() {
        size(800, 600);
        fullScreen();
    }

    /**This method is called once to initialize the game class**/
    public void setup(){
        for (int i = 0; i < 9; i++) {
            images[i] = loadImage("images/openTile"+i+".png");
        }
        images[9] = loadImage("images/closedTile.png");
        images[10] = loadImage("images/flaggedTile.png");
        images[11] = loadImage("images/openBomb.png");
        images[12] = loadImage("images/closedBomb.png");
        images[13] = loadImage("images/reset.png");
        images[14] = loadImage("images/resetWin.png");
        images[15] = loadImage("images/resetLost.png");
        images[16] = loadImage("images/settingsCog.png");
        images[17] = loadImage("images/questionedTile.png");
        images[18] = loadImage("images/badFlag.png");
        for (int i = 0; i < 4; i++) {
            images[19 + i] = loadImage("images/explosion"+i+".png");
        }
        images[23] = loadImage("images/blank.png");
        game = new Game(this, images);
        game.setUpGame(8,8,8);
    }

    /**This method runs once every frame and tells the game to draw itself**/
    public void draw(){
        game.draw();
    }

    /**This method runs when a mouse button is released and can be used
     * to call the left click function within the game*/
    public void mouseReleased(){
        if(mouseButton == LEFT){
            game.leftClick(mouseX,mouseY);
        }
        game.unHoverAll();
    }

    /**This method runs when a mouse button is pressed and can be used
     * to call the right click function within the game*/
    public void mousePressed(){
        if(mouseButton == RIGHT){
            game.rightClick(mouseX,mouseY);
        }
        if(mouseButton == LEFT){
            game.hoverLeftClick(mouseX,mouseY);
        }
    }

    /**This method is called when the mouse is moved while holding a button
     *and is used in order to show the hovered tiles*/
    public void mouseDragged(){
        if(mouseButton == LEFT){
            game.unHoverAll();
            game.hoverLeftClick(mouseX,mouseY);
        }
    }

    /**This method is called when a key is pressed.
     * If "a" is pressed, the ai is activated for one sweep
     */
    public void keyPressed(){
        if(key=='a'){
            AI.playTurn(game); //THE AI IS UNFINISHED
        }
    }

}

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

/**The game class is used to handle everything
 * that goes on in a game of minesweeper**/
public class Game {

    /**The PApplet that the game can use to draw**/
    private PApplet p;

    /**The images that are used in the game**/
    private PImage[] images;

    /**Integer that indicates the state of the game**/
    private int gameState;
    private final int NOTSTARTED = 0;
    private final int RUNNING = 1;
    private final int LOSSSTOPPED = 2;
    private final int WINSTOPPED = 3;
    private final int INSETTINGS = 4;

    /**Time the first click was made**/
    private int startTime;

    /**The current time in an ongoing game**/
    private int currentTime;

    /**Any explosions in the game**/
    private ArrayList<Explosion> explosions;

    /**The board of tiles that the game creates as the playing board**/
    private Board board;

    /**The number of rows in the board**/
    private int boardRows;

    /**The number of columns in the board**/
    private int boardColumns;

    /**The x value to draw the board at**/
    private int boardX;

    /**The y value to start drawing the board at**/
    private int boardY;

    /**The size of the tiles that will be drawn**/
    private int tileSize;

    /**The number of bombs in the game**/
    private int numberBombs;

    /**
     * Default constructor for a game
     * @param p the PApplet that can be used to draw
     * @param imgs the images that can  be drawn
     */
    public Game(PApplet p, PImage[] imgs){
        this.p = p;
        this.images = imgs;
        this.gameState = NOTSTARTED;
    }

    /**
     * Method to set up a game, will ultimately be called through user interaction with
     * some visual element
     * @param numBombs the number of bombs the user wants in the game (must be less than r*c)
     * @param r the number of rows the user wants in the game (must be greater than 0)
     * @param c the number of columns the user wants in the game (must be greater than 0)
     */
    public void setUpGame(int numBombs, int r, int c){

        numberBombs = numBombs;
        boardRows = r;
        boardColumns = c;

        explosions = new ArrayList<>();

        setBoard(r, c);

        tileSize = (int) Math.min(p.height*0.75/boardRows , p.width*0.9/boardColumns);
        boardX = (int)(p.width*0.05 + (p.width*0.9 - boardColumns*tileSize)/2);
        boardY = (int) ((int) p.height*0.2);

        gameState = RUNNING;
        startTime = 0;
    }

    /**
     * This method resets the game when the user initiates a reset
     */
    private void reset(){
        gameState = NOTSTARTED;
        setUpGame(numberBombs,boardRows,boardColumns);
    }

    /**
     * This method toggles whether the game state is INSETTINGS
     */
    private void toggleSettings(){
        if(gameState == NOTSTARTED || gameState == RUNNING){
            gameState = INSETTINGS;
        }else if(gameState == INSETTINGS){
            setUpGame(numberBombs, boardRows, boardColumns);
        }
    }

    /**
     * Sets the board up
     * @param r the number of rows
     * @param c the number of columns
     */
    private void setBoard(int r, int c){
        board = new Board(r,c,numberBombs,p,images);
    }

    /**
     * This method draws the current game, including the board and any timers, counters, or buttons
     */
    public void draw(){

        if(gameState == RUNNING) {
            gameState = board.getGameState();
        }
        if(gameState == RUNNING) {
            drawRunning();
        }else if(gameState == LOSSSTOPPED){
            explosions.add(drawLoss());
            gameState = NOTSTARTED;
        }else if(gameState == WINSTOPPED){
            drawWin();
            gameState = NOTSTARTED;
        }else if(gameState == INSETTINGS){
            drawSettings();
        }else if(gameState == NOTSTARTED){
            for (int i = explosions.size()-1; i > -1; i--) {
                if(explosions.get(i).explode()){
                    explosions.remove(i);
                    gameState = LOSSSTOPPED;
                    drawLoss();
                    gameState = NOTSTARTED;
                }
            }
        }


    }

    /**
     * This method draws an in-progress game
     */
    private void drawRunning(){
        p.background(0);
        drawTimer(true);
        drawCounter();
        drawSettingsButton();
        drawResetButton();
        board.draw(boardX, boardY, tileSize, gameState);
    }

    /**
     * This method draws the settings menu
     */
    private void drawSettings(){
        p.background(0);
        p.fill(255,255,255);
        drawSettingsButton();
        p.textSize(16);
        p.text("Left click to increment/decrement by one\nRight click to increment/decrement by 5",p.width/2,25);
        p.textSize(20);
        p.text("Rows",p.width/5f,2*p.height/5f + p.height/25f);
        p.text("Columns",2*p.width/5f,2*p.height/5f + p.height/25f);
        p.text("Bombs",3*p.width/5f,2*p.height/5f + p.height/25f);
        p.textSize(40);
        p.text(boardRows,p.width/5f,2*p.height/5f + p.height/10f);
        p.text(boardColumns,2*p.width/5f,2*p.height/5f + p.height/10f);
        p.text(numberBombs,3*p.width/5f,2*p.height/5f + p.height/10f);
        for (int i = 1; i <= 3; i++) {
            p.triangle(i*p.width/5f,2*p.height/5f,(i+1)*p.width/5f,2*p.height/5f,(2*i+1)*p.width/10f,p.height/5f);
        }
        for (int i = 1; i <= 3; i++) {
            p.triangle(i*p.width/5f,3*p.height/5f,(i+1)*p.width/5f,3*p.height/5f,(2*i+1)*p.width/10f,4*p.height/5f);
        }

    }

    /**
     * This method draws a win screen
     */
    private void drawWin(){
        p.background(0,100,0);
        p.fill(255);
        board.draw(boardX, boardY, tileSize, gameState);
        p.fill(0,255,0);
        drawTimer(false);
        drawCounter();
        drawResetButton();
        drawSettingsButton();

    }

    /**
     * This method draws a loss screen
     * @return the explosion generated by the loss
     */
    private Explosion drawLoss(){
        p.background(150,0,0);
        p.fill(255,0,0);
        drawTimer(false);
        drawCounter();
        drawResetButton();
        drawSettingsButton();
        p.fill(255);
        return board.draw(boardX, boardY, tileSize, gameState);
    }

    /**
     * Method that updates the score counter
     */
    private void drawCounter(){
        p.noStroke();
        p.fill(255);

        int score = numberBombs - board.getFlagCount();
        p.textSize(60);
        p.text(score, 50, p.height/10);
        p.textSize(30);
        p.text("BOMBS", 50, p.height/10+40);
    }

    /**
     * Method that updates the timer
     * @param update if the time should be updated or not
     */
    private void drawTimer(boolean update){
        p.noStroke();
        p.fill(255);

        if(update) {
            currentTime = (p.millis() - startTime) / 1000;
        }

        if(startTime==0){
            currentTime = 0;
        }

        int textWidth = 45*Integer.toString(currentTime).length();
        p.textSize(60);
        p.text(currentTime, p.width-50-textWidth, p.height/10);
        p.textSize(30);
        p.text("TIMER", p.width-140,p.height/10+40);
    }

    /**
     * Method that draws the reset/start game button
     */
    private void drawResetButton(){
        p.fill(255);
        p.textSize(20);

        int side = (p.height / 5) - (p.height / 30);
        int x = p.width/2-side/2;
        int y = p.height/60;

        if(gameState==RUNNING) {
            p.image(images[13], x, y, side, side);
        }
        if(gameState==WINSTOPPED){
            p.image(images[14], x, y, side, side);
        }
        if(gameState==LOSSSTOPPED){
            p.image(images[15], x, y, side, side);
        }
    }

    private void drawSettingsButton(){

        int side = ((p.height / 5) - (p.height / 30))/2;
        int x = p.width/2-2*side-10;
        int y = p.height/60;

        p.image(images[16],x,y,side,side);
    }

    /**
     * Method to return the current board
     * @return the board
     */
    public Board getBoard(){
        return board;
    }

    /**
     * Method that is called when left clicked
     * @param x x location of the click
     * @param y y location of the click
     */
    public void leftClick(int x, int y){

        if(gameState == RUNNING) {

            if(startTime==0){
                startTime = p.millis();
            }

            int r = (int) Math.floor((y - boardY) / ((double) tileSize));
            int c = (int) Math.floor((x - boardX) / ((double) tileSize));
            if (r >= 0 && r < boardRows && c >= 0 && c < boardColumns) {
                board.leftClick(r, c);
            }
        }

        if(gameState == INSETTINGS){

            int sbSide = ((p.height / 5) - (p.height / 30))/2;
            int sbX = p.width / 2 - 2*sbSide - 10;
            int sbY = p.height / 60;
            if (x >= sbX && x <= sbX + sbSide
                    && y >= sbY && y <= sbY + sbSide) {
                toggleSettings();
            }

            int xBlock = x/(p.width/5);
            int yBlock = y/(p.height/5);
            if(xBlock == 1){
                if(yBlock == 1){
                    boardRows = Math.min(40, boardRows+1);
                }
                if(yBlock == 3){
                    boardRows = Math.max(2, boardRows-1);
                    numberBombs = Math.min(numberBombs, boardRows * boardColumns -1);
                }
            }
            if(xBlock == 2){
                if(yBlock == 1){
                    boardColumns = Math.min(40, boardColumns+1);
                }
                if(yBlock == 3){
                    boardColumns = Math.max(2, boardColumns -1);
                    numberBombs = Math.min(numberBombs, boardRows * boardColumns -1);
                }
            }
            if(xBlock == 3){
                if(yBlock == 1){
                    numberBombs = Math.min(boardRows * boardColumns -1,numberBombs+1);
                }
                if(yBlock == 3){
                    numberBombs = Math.max(numberBombs-1, 1);
                }
            }

        }else { //Not in settings, can use the reset button
            int buttonSide = (p.height / 5) - (p.height / 30);
            int buttonX = p.width / 2 - buttonSide / 2;
            int buttonY = p.height / 60;
            if (x >= buttonX && x <= buttonX + buttonSide
                    && y >= buttonY && y <= buttonY + buttonSide) {
                reset();
            }
            int sbSide = ((p.height / 5) - (p.height / 30))/2;
            int sbX = p.width / 2 - 2*sbSide - 10;
            int sbY = p.height / 60;
            if (x >= sbX && x <= sbX + sbSide
                    && y >= sbY && y <= sbY + sbSide) {
                toggleSettings();
            }
        }

    }

    /**
     * Method that is called when left click hovering
     * @param x x location of the click
     * @param y y location of the click
     */
    public void hoverLeftClick(int x, int y){

        if(gameState == RUNNING) {

            if(startTime==0){
                startTime = p.millis();
            }

            int r = (int) Math.floor((y - boardY) / ((double) tileSize));
            int c = (int) Math.floor((x - boardX) / ((double) tileSize));
            if (r >= 0 && r < boardRows && c >= 0 && c < boardColumns) {
                board.hover(r, c);
            }
        }

    }

    /**
     * Unhovers all hovered tiles
     */
    public void unHoverAll(){
        board.unHoverAll();
    }

    /**
     * Method that is called when right clicked
     * @param x x location of the click
     * @param y y location of the click
     */
    public void rightClick(int x, int y){
        if(gameState == RUNNING) {

            if(startTime==0){
                startTime = p.millis();
            }

            int r = (int) Math.floor((y - boardY) / ((double) tileSize));
            int c = (int) Math.floor((x - boardX) / ((double) tileSize));
            if (r >= 0 && r < boardRows && c >= 0 && c < boardColumns) {
                board.rightClick(r, c);
            }
        }

        if(gameState == INSETTINGS){

            int sbSide = ((p.height / 5) - (p.height / 30))/2;
            int sbX = p.width / 2 - 2*sbSide - 10;
            int sbY = p.height / 60;
            if (x >= sbX && x <= sbX + sbSide
                    && y >= sbY && y <= sbY + sbSide) {
                toggleSettings();
            }

            int xBlock = x/(p.width/5);
            int yBlock = y/(p.height/5);
            if(xBlock == 1){
                if(yBlock == 1){
                    boardRows = Math.min(40, boardRows+5);
                }
                if(yBlock == 3){
                    boardRows = Math.max(2, boardRows-5);
                    numberBombs = Math.min(numberBombs, boardRows * boardColumns -1);
                }
            }
            if(xBlock == 2){
                if(yBlock == 1){
                    boardColumns = Math.min(40, boardColumns+5);
                }
                if(yBlock == 3){
                    boardColumns = Math.max(2, boardColumns -5);
                    numberBombs = Math.min(numberBombs, boardRows * boardColumns -1);
                }
            }
            if(xBlock == 3){
                if(yBlock == 1){
                    numberBombs = Math.min(boardRows * boardColumns -1,numberBombs+5);
                }
                if(yBlock == 3){
                    numberBombs = Math.max(numberBombs-5, 1);
                }
            }

        }
    }

}

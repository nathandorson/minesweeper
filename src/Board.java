import processing.core.PApplet;
import processing.core.PImage;

/**The class that is used to handle everything in the board, mainly tiles**/
public class Board {

    /**The PApplet that the board can use to draw**/
    private PApplet p;

    /**The images that are used in the game**/
    private PImage[] images;

    /**The number of rows in the board**/
    private int rows;

    /**The number of columns the board has**/
    private int columns;

    /**The number of bombs that the board has**/
    private int numBombs;

    /**True if placeBombs has been called, otherwise false to indicate that bombs should still be placed on the first click**/
    private boolean areBombsSet;

    /**The two dimensional array of tiles that
     * "is" the board **/
    private Tile[][] tiles;

    /**
     * The constructor that sets all the instance variables in a board
     * @param rows rows/ aka height of the board
     * @param columns columns/ aka width of the board
     * @param numBombs the number of bombs in this board
     * @param imgs the images that can be drawn
     * @param p PApplet that the board gets passed so it can draw
     */
    public Board(int rows, int columns, int numBombs, PApplet p, PImage[] imgs){
        this.rows = rows;
        this.columns = columns;
        this.numBombs = numBombs;
        areBombsSet = false;
        tiles = new Tile[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                tiles[r][c] = new Tile();
            }
        }
        this.p = p;
        this.images = imgs;
    }

    /**
     * Places bombs in the board, to be used after the first click to ensure no bomb is hit on the first click
     * @param rowToAvoid the row to avoid placing a bomb
     * @param columnToAvoid the column to avoid placing a bomb
     */
    private void placeBombs(int rowToAvoid, int columnToAvoid){
        int bombsPlaced = 0;
        while(bombsPlaced < numBombs){
            int rPlace = (int)(((double)rows)*Math.random());
            int cPlace = (int)(((double)columns)*Math.random());
            if(!tiles[rPlace][cPlace].checkIfBomb() && (rPlace != rowToAvoid || cPlace != columnToAvoid)){
                tiles[rPlace][cPlace].setAsBomb();
                bombsPlaced++;
            }
        }
        areBombsSet = true;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                tiles[r][c].countNeighboringBombs(tiles, r, c);
            }
        }
    }

    /**
     * The method that draws the board, which will loop through each tile in the
     * 2d array in order to draw each tile
     * @param x the x coordinate to begin at
     * @param y the y coordinate to begin at
     * @param tSize the tile size to use for changing the draw locations
     * @param gameState the current state of the game, used to slightly alter functionality
     * @return an explosion, if one was created that frame
     */
    public Explosion draw(int x, int y, int tSize, int gameState){
        int explosionX = 0;
        int explosionY = 0;

        final int RUNNING = 1;
        final int LOSSSTOPPED = 2;
        final int WINSTOPPED = 3;

        p.fill(255);
        p.textSize(20);

        int tileSize = tSize;
        int drawX = x;
        int drawY = y;
        for (Tile[] row : tiles) {
            for (Tile t: row) {
                PImage tileImage = images[9];
                if(gameState==LOSSSTOPPED){
                    if(t.checkIfBomb()){
                        tileImage = images[12];
                    }
                }
                if(t.isHovered()){
                    tileImage = images[0];
                }
                if(t.isOpen()){
                    if(t.checkIfBomb()){
                        tileImage = images[11];
                        explosionX = drawX - tileSize;
                        explosionY = drawY - tileSize;
                    }else{
                        tileImage = images[t.getNumNeighboringBombs()];
                    }
                }
                if(t.isFlagged()){
                    tileImage = images[10];
                    if(gameState==LOSSSTOPPED && !t.checkIfBomb()){
                        tileImage = images[18];
                    }
                }
                if(t.isQuestioned()){
                    tileImage = images[17];
                }
                if(gameState==WINSTOPPED){
                    if(t.checkIfBomb()){
                        tileImage = images[10];
                        while(!t.isFlagged()){
                            t.toggleState();
                        }
                    }
                }

                p.image(tileImage,drawX,drawY,tileSize,tileSize);

                drawX += tileSize;
            }
            drawX = x;
            drawY += tileSize;
        }
        if(gameState == LOSSSTOPPED){
            return new Explosion(explosionX,explosionY,tileSize*3,p,images);
        }
        return null;
    }

    /**
     * Counts the number of flagged tiles on the board
     * @return the number of flags
     */
    public int getFlagCount(){
        int f = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if(tiles[r][c].isFlagged()){
                    f++;
                }
            }
        }
        return f;
    }

    /**
     * Method run on left click that will attempt to left click a tile
     * @param r row of click
     * @param c column of click
     */
    public void leftClick(int r, int c){
        if(r >= 0 && r < rows && c>= 0 && c < columns) {
            if(!tiles[r][c].isFlagged()) {
                if (!areBombsSet) {
                    placeBombs(r, c);
                }
                revealTiles(r, c);
            }
        }
    }

    /**
     * Method run on left click hover that will attempt to hover over a tile
     * @param r row of click
     * @param c column of click
     */
    public void hover(int r, int c){
        if(r >= 0 && r < rows && c>= 0 && c < columns) {
            if(tiles[r][c].isClosed()) {
                tiles[r][c].hover();
            }
        }
    }

    /**
     * Unhovers all hovered tiles
     */
    public void unHoverAll(){
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                tiles[r][c].unHover();
            }
        }
    }

    /**
     * reveals a certain tile, chains to other nearby tiles if there are no bombs nearby, or if clicked
     * on a tile with the proper amount of nearby flags
     * @param row the row of the tile to reveal
     * @param col the column of the tile to reveal
     */
    private void revealTiles(int row, int col){

        final int NONE = 0;
        final int LIMITED = 1;
        final int FULL = 2;
        int sweepType = NONE;

        Tile t = tiles[row][col];
        if(t.isOpen() && t.getNumNeighboringBombs() == t.numNeighboringFlags(tiles,row,col)){
            sweepType = LIMITED;
        }

        tiles[row][col].reveal();

        if (t.getNumNeighboringBombs() == 0){
            sweepType = FULL;
        }

        if(sweepType != NONE){
            int startSweepR = Math.max(row-1,0);
            int endSweepR = Math.min(row+2, rows);
            int startSweepC = Math.max(col-1,0);
            int endSweepC = Math.min(col+2, columns);
            for (int r = startSweepR; r < endSweepR; r++) {
                for (int c = startSweepC; c < endSweepC; c++) {
                    if(tiles[r][c].isClosed()) {
                        if(sweepType == FULL){
                            revealTiles(r, c);
                        }else{
                            tiles[r][c].reveal();
                            if(tiles[r][c].getNumNeighboringBombs()==0){
                                revealTiles(r,c);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Method run on right click that will attempt to right click a tile
     * @param r row of click
     * @param c column of click
     */
    public void rightClick(int r, int c){
        if(r >= 0 && r < rows && c>= 0 && c < columns) {
            tiles[r][c].toggleState();
        }
    }

    /**
     * Method run on simulated right click that will attempt to right click a tile
     * @param r row of click
     * @param c column of click
     */
    public void aiRightClick(int r, int c){
        if(r >= 0 && r < rows && c>= 0 && c < columns) {
            tiles[r][c].flag();
        }
    }

    /**
     * Method to return the tiles for use in the AI
     * @return the tiles on the board
     */
    public Tile[][] getTiles(){
        return tiles;
    }

    /**
     * Returns the game state after checking all of the tiles to see if the game has been lost or won
     * @return the state of the game
     */
    public int getGameState(){

        final int RUNNING = 1;
        final int LOSSSTOPPED = 2;
        final int WINSTOPPED = 3;

        int openTiles = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Tile t = tiles[r][c];
                if(t.isOpen()){
                    if(t.checkIfBomb()){
                        return LOSSSTOPPED;
                    }else{
                        openTiles++;
                    }
                }
            }
        }

        if((rows*columns)-openTiles == numBombs){
            return WINSTOPPED;
        }

        return RUNNING;
    }

}

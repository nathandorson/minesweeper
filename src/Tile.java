/**
 * The tile class that holds all the information and functions of a tile
 * in a game of minesweeper
 */
public class Tile {

    /**Gives info about the state of the tile
     * 0 = covered, 1 = flagged, 2 = uncovered, 3 = questioned**/
    private int state;
    private final static int QUESTIONED = 3;
    private final static int OPEN = 2;
    private final static int FLAGGED = 1;
    private final static int CLOSED = 0;

    private boolean isHovered;

    /**True if tile is a bomb, otherwise false**/
    private boolean isBomb;

    /**The number of bombs in the 8 tile ring
     * around the tile**/
    private int numNeighboringBombs;

    /**
     * The default constructor for the tile class
     */
    public Tile(){
        state = CLOSED;
        isBomb = false;
        isHovered = false;
    }

    /**Sets this tile to "be" a bomb tile**/
    public void setAsBomb(){
        isBomb = true;
    }

    /**
     * Sets numNeighboringBombs equal to the amount of neighboring bombs
     * @param board the board the tile is in
     * @param row the row of this tile
     * @param col the column of this tile
     */
    public void countNeighboringBombs(Tile[][] board, int row, int col){
        numNeighboringBombs = 0;
        int rowMin = Math.max(0, row-1);
        int rowMax = Math.min(board.length, row+2);
        int colMin = Math.max(0, col-1);
        int colMax = Math.min(board[0].length, col+2);
        for (int r = rowMin; r < rowMax; r++) {
            for (int c = colMin; c < colMax; c++) {
                if(board[r][c].checkIfBomb()){
                    numNeighboringBombs++;
                }
            }
        }
    }

    /**
     * returns an int equal to the amount of neighboring flags
     * @param board the board the tile is in
     * @param row the row of this tile
     * @param col the column of this tile
     * @return the number of neighboring (8 tile square) flagged tiles
     */
    public int numNeighboringFlags(Tile[][] board, int row, int col){
        int numNeighboringFlags = 0;
        int rowMin = Math.max(0, row-1);
        int rowMax = Math.min(board.length, row+2);
        int colMin = Math.max(0, col-1);
        int colMax = Math.min(board[0].length, col+2);
        for (int r = rowMin; r < rowMax; r++) {
            for (int c = colMin; c < colMax; c++) {
                if(board[r][c].isFlagged()){
                    numNeighboringFlags++;
                }
            }
        }
        return numNeighboringFlags;
    }

    /**
     * returns an int equal to the amount of neighboring closed tiles
     * @param board the board the tile is in
     * @param row the row of this tile
     * @param col the column of this tile
     * @return the number of neighboring (8 tile square) closed tiles
     */
    public int numNeighboringClosed(Tile[][] board, int row, int col){
        int numClosed = 0;
        int rowMin = Math.max(0, row-1);
        int rowMax = Math.min(board.length, row+2);
        int colMin = Math.max(0, col-1);
        int colMax = Math.min(board[0].length, col+2);
        for (int r = rowMin; r < rowMax; r++) {
            for (int c = colMin; c < colMax; c++) {
                if(board[r][c].isClosed()){
                    numClosed++;
                }
            }
        }
        return numClosed;
    }

    /**Sets this tile to being hovered over**/
    public void hover(){
        if(state == CLOSED) {
            isHovered = true;
        }
    }

    /**Sets this tile to being hovered over**/
    public void unHover(){
        isHovered = false;
    }

    /**Sets this tile to be open unless if it is closed and not flagged**/
    public void reveal(){
        if(state == CLOSED){
            state = OPEN;
        }
    }

    /**Sets this tile to covered if questioned, to questioned if flagged, and to flagged if covered**/
    public void toggleState(){
        if(state == CLOSED){
            state = FLAGGED;
        }else if(state == FLAGGED){
            state = QUESTIONED;
        }else if(state == QUESTIONED){
            state = CLOSED;
        }
    }

    /**Sets this tile to flagged if covered**/
    public void flag(){
        if(state == CLOSED){
            state = FLAGGED;
        }
    }

    /**Returns true if tile is in the flagged state
     * @return if the tile is flagged**/
    public boolean isFlagged(){ return (state == FLAGGED); }

    /**Returns true if tile is in the questioned state
     * @return if the tile is questioned**/
    public boolean isQuestioned(){ return (state == QUESTIONED); }

    /**Returns true if tile is in the closed state
     * @return if the tile is closed**/
    public boolean isClosed(){ return (state == CLOSED);}

    /**Return true if tile is is the open state
     * @return if the tile is open**/
    public boolean isOpen(){ return (state == OPEN); }

    /**Returns if the tile is a bomb or not
     * @return if the tile is a bomb**/
    public boolean checkIfBomb(){ return isBomb; }

    /**Returns if the tile is hovered or not
     * @return if the tile is being hovered over by the mouse**/
    public boolean isHovered(){ return isHovered; }

    /**Returns the amount of neighboring bombs
     * @return the number of neighboring (8 tile square) tiles that are bombs**/
    public int getNumNeighboringBombs(){ return numNeighboringBombs; }

}

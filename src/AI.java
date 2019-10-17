/**
 * An AI that will attempt to play minesweeper
 * UNFINISHED
 */
public final class AI {

    /**Private constructor so that AI cannot be instantiated except by itself**/
    private AI(){ }

    /**
     * Method to play a turn in a given game
     * @param g the given game
     */
    public static void playTurn(Game g){
        Board b = g.getBoard();
        Tile[][] tiles = b.getTiles();
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                Tile t = tiles[r][c];
                if(t.isOpen() && t.getNumNeighboringBombs() == t.numNeighboringFlags(tiles,r,c)){
                    b.leftClick(r,c);
                    tiles = b.getTiles();
                }
                if(t.isOpen() && t.getNumNeighboringBombs()>0 && t.numNeighboringClosed(tiles, r, c) + t.numNeighboringFlags(tiles, r, c) == t.getNumNeighboringBombs()){
                    for (int row = r-1; row < r+2; row++) {
                        for (int col = c-1; col < c+2; col++) {
                            b.aiRightClick(row, col);
                            tiles = b.getTiles();
                        }
                    }
                }


            }
        }
    }

}

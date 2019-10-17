import processing.core.PApplet;
import processing.core.PImage;

/**
 * The class used to draw explosions on the game board
 */
public class Explosion {

    /**The x coordinate of this explosion*/
    private int x;

    /**The y coordinate of this explosion*/
    private int y;

    /**The size of this explosion*/
    private int size;

    /**The time this explosion was created*/
    private int start;

    /**The main PApplet so the explosion can draw itself*/
    private PApplet p;

    /**The images to use to draw*/
    private PImage[] images;

    /**
     * The constructor for an explosion that sets all its instance variables
     * @param x the top left x coord
     * @param y the top left y coord
     * @param size the size of the explosion (3x3 tiles)
     * @param p the PApplet
     * @param images the image array
     */
    public Explosion(int x, int y, int size, PApplet p, PImage[] images){
        this.x = x;
        this.y = y;
        this.size = size;
        this.start = p.millis();
        this.p = p;
        this.images = images;
    }

    /**
     * Runs one tick of "explosion" for this explosion. Draws it on screen.
     * @return if the explosion is done
     */
    public boolean explode(){
        int exTick = (p.millis()-start)/250;
        if(exTick > 3){
            return true;
        }
        p.image(images[19+exTick],x,y,size,size);
        return false;
    }

}

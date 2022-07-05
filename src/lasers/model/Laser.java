package lasers.model;
/**
 * @author: George Link
 * @author: Marshall Teichman
 * @file: Laser.java
 * @language: Java 14
 *
 * a class used to represent a laser on the board at a specific set of coordinates.
 */
public class Laser extends Card{
    /**
     *a constructor used to represent a laser in the safe.
     * @param row the laser's x placement
     * @param col the laser's y placement
     */
    public Laser(int row, int col){
        super(row, col);
    }
}
package lasers.model;

/**
 * @author: George Link
 * @author: Marshall Teichman
 * @file: Pillar.java
 * @language: Java 14
 *
 * A class used to represent a pillar in the safe's layout that requires an
 * arbitrary amount of lasers to be adjacent.
 */
public class Pillar extends Card{
    private int nLasers; //the amount of lasers this pillar requires to verify

    /**
     * a contrsuctor used to represent a pillar inside the safe.
     * @param row where the pillar is located in the safe on the x axis
     * @param col where the pillar is located in the safe on the y axis
     * @param neededLasers the amount of lasers that have to be placed adjacent to this pillar.
     */
    public Pillar( int row, int col, int neededLasers){
        super(row, col);
        this.nLasers = neededLasers;
    }

    /**
     * a getter method for the number of lasers this pillar requires
     * @return an integer representing the amount of lasers this pillar needs.
     */
    public int getnLasers() {
        return this.nLasers;
    }

}
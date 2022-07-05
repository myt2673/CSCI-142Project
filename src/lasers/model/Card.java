package lasers.model;
/**
 * @Author: George Link
 * @author: Marshall Teichman
 * @file: Card.java
 * @language Java14
 *
 * Creates a card object that stores its position on the board as well as a list of objects it collides with.
 */

import java.util.ArrayList;

public class Card{

    /**
     * Enum for all the card types
     */
    public enum cType{
         PILLAR,
         LASER,
         BEAM,
         NONE

    }

    protected cType cardType = cType.NONE;
    private int row;        // Row of card
    private int col;        // Col of card
    protected ArrayList<Card> overlap = new ArrayList<>();

    /**
     * a constructor that represents a place within the safe interior.
     * @param row the x coordinate of the Card
     * @param col the y coordinate of the Card.
     */
    public Card(int row, int col){
        this.row = row;
        this.col = col;
    }

    /**
     * a constructor that represents a card with a specified type from a beam, laser, pillar, or nothing.
     * @param row the horizontal location of the card
     * @param col the vertical location of the card.
     * @param cardType what type of card this is, i.e. (laser, pillar, beam, or floor space)
     */
    public Card(int row, int col, cType cardType){
        this.row = row;
        this.col = col;
        this.cardType = cardType;
    }

    /** Getters **/
    public cType getCardType(){
        return this.cardType;
    }

    /**
     * Test to see if this card is a given type.
     * @param ctype Type of card being compared
     * @return True if they match.
     */
    public boolean isType(cType ctype){
        if(this.cardType == ctype){
            return true;
        }else {
            return false;
        }
    }

    /**
     *
     * @return the card's row(x) coordinate in the safe.
     */
    public int getRow(){
        return this.row;
    }

    /**
     *
     * @return the card's col(y) coordinate in the safe.
     */
    public int getCol() {
        return this.col;
    }

    /**
     * an accessor method to get all of the possible states a card could have, i.e. a laser over a beam.
     * @return an ArrayList of cards representing all the possible cards that could happen on this card.
     */
    public ArrayList<Card> getOverlap(){
        return this.overlap;
    }


    /**
     * returns true if the overlap is empty
     * @return True if overlap is not empty
     */
    public boolean overlapNotEmpty(){
        return overlap.size() != 0;
    }

    /** Setters **/
    public void setCardType(cType cardType){
        this.cardType = cardType;
    }
    public void addOverlap(Card card){
        this.overlap.add(card);
    }
}
package lasers.model;
/**
 * @Author: George Link
 * @author: Marshall Teichman
 * @file: Safe.java
 * @language Java14
 *
 * Creates a safe object and manipulates the placement of cards on it
 */

import lasers.model.Card.cType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static lasers.model.Card.cType.*;

public class Safe {
    int maxCol; //maximum height of the safe interior.
    int maxRow; //maximum width of the safe interior.
    public Card[][] SafeLayout = new Card[this.maxRow][this.maxCol]; //2d Card array that functions as the safe's 'board'

    /**
     * Constructor that represents the safe and the dimensions of the safe.
     * **/
    public Safe(int row, int col){
        this.maxRow = row;
        this.maxCol = col;
        makeSafe();
    }

    /**
     * Makes a blank SafeLayout based on teh safe's internal dimensions w/ no lasers or pillars.
     */
    public void makeSafe(){
        Card[][] tempSafe = new Card[this.maxRow][this.maxCol];
        for(int row = 0; row < this.maxRow; row++){
            for(int col = 0; col < this.maxCol; col++){
                Card newCard = new Card(row, col);
                tempSafe[row][col] = newCard;
            }
        }
        this.SafeLayout = tempSafe;
    }
    /**
     * takes in a .txt SafeFile and sets the Safe's layout to match the given file
     * @param SafeFile the SafeFile to set the Safe's Layout to.
     * @throws IOException for use in case the method fails to read from the file.
     */
    public void makeSafe(String SafeFile) throws IOException {
        //opens the inputted file into a new BufferedReader and reads the first line to 'skip' over it,
        //since the coordinates are already defined, and creates a temporary Card[][] SafeLayout.
        File file = new File(SafeFile);
        BufferedReader input = new BufferedReader(new FileReader(file));
        input.readLine();
        Card[][] tempSafe = new Card[this.maxRow][this.maxCol];

        //reads the file line by line, splitting each line by whitespaces into a String[].
        for(int row = 0; row < this.maxRow; row++){
            String[] currentLine = input.readLine().split(" ");
            //for each string element in the line/ String[],
            //analyze that element.
            for(int col = 0; col < this.maxCol; col++){
                String currentCard = currentLine[col];
                Card newCard;
                switch (currentCard){
                    // check if the element is a floor Card("."), an arbitrary pillar("X")
                    // or a pillar with a required amount of lasers.
                    // place it into [row][col] in the temporary layout.
                    case("."):
                        //create and add a floor card/NONE
                        newCard = new Card(row, col);
                        newCard.setCardType(NONE);
                        tempSafe[row][col] = newCard;
                        break;
                    case("X"):
                        //create and add a pillar that can support any amount of lasers.
                        newCard = new Pillar(row, col,10);
                        newCard.setCardType(PILLAR);
                        tempSafe[row][col] = newCard;
                        break;
                    default:
                        //create and add a pillar that requires the specified amount of lasers.
                        int numLasers = Integer.parseInt(currentLine[col]);
                        newCard = new Pillar(row, col,numLasers);
                        newCard.setCardType(PILLAR);
                        tempSafe[row][col] = newCard;

                }
            }
        }
        //make the SafeLayout the temporary layout.
        this.SafeLayout = tempSafe;
    }


    /**
     * a method that prints out the safe in a 2d grid format, with rows and columns numbered.
     * **/
    public void printSafe(){
        //prints the numbering for the columns.
        System.out.print("   ");
        for(int i = 0; i < this.maxCol ; i++ ){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.print("   ");
        for(int i = 0; i < this.maxCol ; i++ ){
            System.out.print("- ");
        }
        System.out.println();

        for(int row = 0; row < this.maxRow; row++){
            //for each row print the row's number.
            System.out.print(row + "| ");
            for(int col = 0; col < this.maxCol; col++){
                //for each card from left to right, top to bottom.
                cType type = this.SafeLayout[row][col].getCardType();
                switch(type){
                    case NONE:
                        //if the tile is a floor, print a ". " to represent empty floor space.
                        System.out.print(". ");
                        break;
                    case PILLAR:
                        //if the tile is a pillar, either print "X " if the tile is arbitrary.
                        Pillar piller = (Pillar)this.SafeLayout[row][col];
                        if(piller.getnLasers() > 4){
                            System.out.print("X ");
                        }else {
                            //print the required amount of lasers needed.
                            System.out.print(piller.getnLasers() + " ");
                        }
                        break;
                    case LASER:
                        //print "L " if the Card is a laser.
                        System.out.print("L ");
                        break;
                    case BEAM:
                        //if the Card is a beam, print "* ".
                        System.out.print("* ");
                        break;
                }
            }
            //prints a new line for the next row of cards to be printed on.
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Places a specified card at the inputted coordinates.
     * @param row Row being placed
     * @param col Column being placed
     */
    public void add(int row, int col, cType type){
        // If the row and column are out of bounds.
        if(row >= maxRow || col >= maxCol){
            System.out.println("Incorrect Coordinates...");
        }else{
            //Check to see what the type is.
            switch(type){
                case LASER:
                    // If we are trying to place it on a pillar.
                    if(this.SafeLayout[row][col].getCardType() == cType.PILLAR) {
                        System.out.println("Pillar at coordinates...");
                    }else if(this.SafeLayout[row][col].getCardType() == cType.LASER) {
                        System.out.println("Laser at coordinates...");
                    }else{
                        //create and add a laser.
                        Card laser = new Laser(row, col);
                        laser.setCardType(type);
                        this.SafeLayout[row][col] = laser;

                        //Set the beams for that laser.
                        setBeams(row, col);

                        //Add this laser as being powered by the pillar.
                        testForPillars(row, col);
                        System.out.println("Laser added @: (" + row +", " + col + ")");
                    }
                    break;
                case BEAM:
                    //creates a new beam object
                    Card beam = new Card(row, col, BEAM);
                    // if we are trying to place a beam on a laser, add it to the overlap.
                    if(this.SafeLayout[row][col].isType(LASER)){
                        this.SafeLayout[row][col].addOverlap(beam);
                        //if there is nothing on the empty floor space, place the beam.
                    }else if (this.SafeLayout[row][col].getCardType() == NONE){
                        this.SafeLayout[row][col] = beam;
                    }
            }
        }
    }

    /**
     * Add for Pillar
     * This has an extra param for the number of lasers the pillar can power
     * @param row Row being added
     * @param col Column being added
     * @param type Type needs to be pillar
     * @param numLasers Number of lasers for
     */
    public void add(int row, int col, cType type, int numLasers){
        // if we are out of bounds.
        if(row >= maxRow || col >= maxCol){
            System.out.println("Incorrect Coordinates...");
        }else if((type == PILLAR) && (this.SafeLayout[row][col].getCardType() == cType.NONE)) {
            // creates a new piller and places it at the cords.
            Card pillar = new Pillar(row, col, numLasers);
            pillar.setCardType(type);
            this.SafeLayout[row][col] = pillar;
        }
    }

    /***
     * tests for pillars from the inputted (row, col) in cardinal directions.
     * Scans out in the cardinal directions looking for pillars.
     * If it finds one it adds itself to that pillars Overlap array.
     * @param row the row for the test to originate at, (non-inclusive)
     * @param col the column for the test to originate at, (non-inclusive)
     */
    private void testForPillars(int row, int col){
        // Order: Bottom, Top, Right, Left.
        if((row + 1 < maxRow) && (this.SafeLayout[row + 1][col].isType(PILLAR))){
            this.SafeLayout[row + 1][col].addOverlap(this.SafeLayout[row][col]);
        }if((row - 1 >= 0) && (this.SafeLayout[row - 1][col].isType(PILLAR))){
            this.SafeLayout[row - 1][col].addOverlap(this.SafeLayout[row][col]);
        }if((col + 1 < maxCol) && (this.SafeLayout[row][col + 1].isType(PILLAR))){
            this.SafeLayout[row][col + 1].addOverlap(this.SafeLayout[row][col]);
        }if((col - 1 >= 0) && (this.SafeLayout[row][col - 1].isType(PILLAR))){
            this.SafeLayout[row][col - 1].addOverlap(this.SafeLayout[row][col]);
        }
    }

    /**
     * Takes in coordinates (row, col) and sets all tiles in the cardinal directions to beams from those coordinates
     * until met with a pillar or wall.
     * meant to be used when a new laser is placed, using the laser's coordinates
     * Precondition: the coordinates are valid.
     * @param row the row for the set of beams to originate from.
     * @param col the column for the laser beams to follow
     */
    private void setBeams(int row, int col){
        // Scanning order: Up, Down, Left, Right.
        for(int r = (row - 1); r >= 0; r--){
            // Stops when a pillar is hit.
            if(this.SafeLayout[r][col].isType(PILLAR)){
                break;
            }
            //adds the beams.
            add(r, col, BEAM);
        }for(int r = (row + 1); r < this.maxRow; r++){
            if(this.SafeLayout[r][col].isType(PILLAR)){
                break;
            }add(r, col, BEAM);
        }for(int c = (col - 1); c >= 0; c--){
            if(this.SafeLayout[row][c].isType(PILLAR)){
                break;
            }add(row, c, BEAM);
        }for(int c = (col + 1); c < this.maxCol; c++){
            if(this.SafeLayout[row][c].isType(PILLAR)){
                break;
            }add(row, c, BEAM);
        }
    }

    /**
     * Remove a laser at a position (row, col)
     * Removes all traces of that laser ever existing.
     * @param row Row being removed
     * @param col Column being removed
     */
    public void remove(int row, int col){
        //Checks that the cords are inbounds.
        if(row > maxRow || col > maxCol){
            System.out.println("Incorrect Coordinates...");
        }
        // If we are trying to remove something that is not a Laser.
        else if(this.SafeLayout[row][col].getCardType() != cType.LASER){
            System.out.println("No Laser at coordinates...");
        }else{
            //Creates a card for the blank space
            Card space = new Card(row, col, NONE);
            this.SafeLayout[row][col] = space;
            //removes the Laser from being "powered" by the pillar.
            removeFromPillars(row, col);
            //Removes the beams that laser created.
            removeFromBeams(row, col);
            System.out.println("Laser removed @ (" + row + ", "+ col+ ")");

        }
    }

    /**
     * Helper function to remove.
     * Removes the Laser from being powered by the pillars next to it.
     * @param row Row being removed
     * @param col Column being removed
     */
    private void removeFromPillars(int row, int col){
        // Order: Down, Up, Right, Left.
        // If the pillar is in bounds and is powering the Laser.
        if((row + 1 < maxRow) && ((this.SafeLayout[row + 1][col].isType(PILLAR)) && (this.SafeLayout[row + 1][col].overlapNotEmpty()))){
            // remove one laser from the amount of lasers the pillar is powering.
            this.SafeLayout[row + 1][col].overlap.remove(0);
        }if((row - 1 >= 0) && ((this.SafeLayout[row - 1][col].isType(PILLAR)) && (this.SafeLayout[row - 1][col].overlapNotEmpty()))){
            this.SafeLayout[row - 1][col].overlap.remove(0);
        }if((col + 1 < maxCol) && (this.SafeLayout[row][col + 1].isType(PILLAR)) && (this.SafeLayout[row][col + 1].overlapNotEmpty())){
            this.SafeLayout[row][col + 1].overlap.remove(0);
        }if((col - 1 >= 0) && ((this.SafeLayout[row][col - 1].isType(PILLAR)) && (this.SafeLayout[row][col - 1].overlapNotEmpty()))){
            this.SafeLayout[row][col - 1].overlap.remove(0);
        }
    }

    /**
     * Helper function of remove.
     * Removes the lasers beams and removes the beams from any lasers they collide with.
     * @param row The row of the laser being removed
     * @param col the column of the laser being removed.
     */
    private void removeFromBeams(int row, int col){
        //Scanning order: Up, Down, Left Right.
        //Creates a new Card that will replace the beam.
        Card none = new Card(row, col, NONE);
        //Checks all cards above the laser being removed.
        for(int r = (row - 1); r >= 0; r--){
            Card card = this.SafeLayout[r][col];
            // If we hit a pillar Stop.
            if(card.isType(PILLAR)){
                break;
            }else if((card.isType(LASER)) && (card.overlapNotEmpty())){
                // if we hit a laser that has one laser added to its overlap, remove one beam from it.
                this.SafeLayout[r][col].overlap.remove(0);
            }else if(card.isType(BEAM)){
                //if the card we are checking is a beam, change it to a blank card.
                this.SafeLayout[r][col] = none;
            }
        }
        for(int r = (row + 1); r < this.maxRow; r++){
            Card card = this.SafeLayout[r][col];
            if(card.isType(PILLAR)){
                break;
            }else if((card.isType(LASER)) && (card.overlapNotEmpty())){
                this.SafeLayout[r][col].overlap.remove(0);
            }else if(card.isType(BEAM)){
                this.SafeLayout[r][col] = none;
            }
        }
        for(int c = (col - 1); c >= 0; c--){
            Card card = this.SafeLayout[row][c];
            if(card.isType(PILLAR)){
                break;
            }else if((card.isType(LASER)) && (card.overlapNotEmpty())){
                this.SafeLayout[row][c].overlap.remove(0);
            }else if(card.isType(BEAM)){
                this.SafeLayout[row][c] = none;
            }
        }
        for(int c = (col + 1); c < this.maxCol; c++){
            Card card = this.SafeLayout[row][c];
            if(card.isType(PILLAR)){
                break;
            }else if((card.isType(LASER)) && (card.overlapNotEmpty())){
                this.SafeLayout[row][c].overlap.remove(0);
            }else if(card.isType(BEAM)){
                this.SafeLayout[row][c] = none;
            }
        }
        // Checks for lasers on the board, reset the lasers they create to fill spots that should not be empty.
        for(int r = 0; r < this.maxRow; r++){
            for(int c = 0; c < this.maxCol; c++) {
                if(this.SafeLayout[r][c].isType(LASER)){
                    setBeamsOnRemove(r, c);
                }
            }
        }
    }

    /**
     * Helper function to Remove from beams
     * Sets beams of all lasers to what they should be after removing an intersecting beam
     * This was needed because if setBeams was called normally, If there where two lasers places on the board
     * and they both intersected another laser source, it would count as two intersections at the laser source
     * that was placed first.
     *
     * This was a odd edge case and a pain in the butt to trouble shoot, because the error didnt appear until
     * we tried completely filling the safe and removing the ones that didnt belong, It took a good hour or so to
     * work out the cause of the bug.
     *
     * To fix it we do the same thing as the normal process for setting the beams, but only add the beams on blank spaces.
     * this prevents that duplication error. when adding multiple lasers in incorrect positions.
     *
     * @param row Row of laser being removed.
     * @param col Column of laser being removed.
     */
    private void setBeamsOnRemove(int row, int col){
        for(int r = (row - 1); r >= 0; r--){
            // Stops when a pillar is hit.
            if(this.SafeLayout[r][col].isType(PILLAR)){
                break;
            }else if(this.SafeLayout[r][col].isType(NONE)) {
                //adds the beams.
                add(r, col, BEAM);
            }
        }for(int r = (row + 1); r < this.maxRow; r++){
            if(this.SafeLayout[r][col].isType(PILLAR)){
                break;
            }else if(this.SafeLayout[r][col].isType(NONE)) {
                add(r, col, BEAM);
            }
        }for(int c = (col - 1); c >= 0; c--){
            if(this.SafeLayout[row][c].isType(PILLAR)){
                break;
            }else if(this.SafeLayout[row][c].isType(NONE)) {
                add(row, c, BEAM);
            }
        }for(int c = (col + 1); c < this.maxCol; c++){
            if(this.SafeLayout[row][c].isType(PILLAR)){
                break;
            }else if(this.SafeLayout[row][c].isType(NONE)) {
                add(row, c, BEAM);
            }
        }
    }




    /**
     * verifies if the current safe layout is valid or not
     * A safe is valid if: each tile that is not a pillar has a laser or beam covering it
     * Each pillar has exactly the specified amount of lasers adjacent to it, except X (any amount)
     * No two lasers are in direct sight of each other in a cardinal direction.
     * @return true if the safe is valid, false if the safe is not valid.
     */
    public boolean verify(){
        // Scan the whole board
        for(int row = 0; row < this.maxRow; row++){
            for(int col = 0; col < this.maxCol; col++){
                //If we hit an empty space.
                if(this.SafeLayout[row][col].isType(NONE)){
                    System.out.println("Error Verifying at " + row + " " + col);
                    return false;
                }
                // If we hit a pillar.
                if(this.SafeLayout[row][col].isType(PILLAR)){
                    //Check to see if the pillar has the right amount of Lasers being powered.
                    Pillar card =  (Pillar)this.SafeLayout[row][col];
                    ArrayList<Card> overlap = card.getOverlap();
                    // Any lasers >4 means it can have any amount of lasers.
                    if(card.getnLasers() > 4){
                        continue;
                    }else if(overlap.size() != card.getnLasers()){
                        // If it has the wrong amount of lasers being powered return false.
                        System.out.println("Error Verifying at " + row + " " + col);
                        return false;
                    }
                }
                // If we are dealing with a Laser
                if(this.SafeLayout[row][col].isType(LASER)){
                    //Check to see if any lasers beams overlap with the laser.
                    Laser card = (Laser)this.SafeLayout[row][col];
                    ArrayList<Card> overlap = card.getOverlap();
                    if(overlap.size() != 0){
                        //If so return false.
                        System.out.println("Error Verifying at " + row + " " + col);
                        return false;
                    }
                }
            }
        }
        System.out.println("The safe is fully verified");
        return true;
    }
}

package lasers.ptui;
/**
 * @author: George Link
 * @author: Marshall Teichman
 * @file: LasersPTUI.java
 * @language: Java 14
 *
 * the user interface that takes in the safe file and creates a Safe object for the user to manipulate
 */

import lasers.model.Card;
import lasers.model.Safe;

import java.io.*;
import java.util.Scanner;

public class LasersPTUI {

    /**
     * a simple print method that displays the commands available to the user, and shows their definition and usage.
     */
    public static void printCMDS(){
        System.out.println("a|add r c: Add laser to (r,c)\n" +
                "d|display: Display safe\n" +
                "h|help: Print this help message\n" +
                "q|quit: Exit program\n" +
                "r|remove r c: Remove laser from (r,c)\n" +
                "v|verify: Verify safe correctness");
    }

    /**
     * a method to take the user's input, analyze it, and perform the specified command on the inputted safe object,
     * or reject the command if it is invalid.
     * @param cmd a String of the user's command split by the whitespaces into an array
     * @param safe the safe object that the user's input will affect
     */
    public static void output(String[] cmd, Safe safe){
        String command = cmd[0];
        switch (command){
            case "a":
                //check if the coordinates are numbers.
                if(cmd.length != 3){
                    System.out.println("Invalid parameters!");
                }
                else if ((cmd[1].matches("[0-9]+")) && (cmd[2].matches("[0-9]+")) ){
                    safe.add(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), Card.cType.LASER);
                    safe.printSafe();
                }
                else{
                    System.out.println("Error. Invalid coordinate inputs!");
                }
                break;
            case "d":
                safe.printSafe();
                break;
            case "h":
                printCMDS();
                break;
            case "q":
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            case "r":
                //check if the coordinates are numbers.
                if(cmd.length != 3){
                    System.out.println("Invalid parameters!");
                }
                else if ((cmd[1].matches("[0-9]+")) && (cmd[2].matches("[0-9]+")) ){
                    safe.remove(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
                    safe.printSafe();
                }
                else{
                    System.out.println("Error. Invalid coordinate inputs!");
                }
                break;
            case "v":
                System.out.println("Verifying...");
                safe.verify();
                break;
            default:
                System.out.println("Error! Unknown Command!");
        }
    }

    public static BufferedReader openFile(String fileName) throws FileNotFoundException {
        File file = new File("data//"+fileName);
        BufferedReader input = new BufferedReader(new FileReader(file));
        return input;
    }

    /**
     * The main method; rejects the user is there are les than 1 or greater than 2 input commands.
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        // check (questionable) sanity of input
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java LasersPTUI safe-file [input]");
        } else {
            //reads the first line of the safe file and creates a board of those dimensions
            String[] DIMMs = openFile(args[0]).readLine().split(" ");
            printCMDS();
            Safe safe = new Safe(Integer.parseInt(DIMMs[0]), Integer.parseInt(DIMMs[1]));

            //makes the Safe's layout with the pillars based on the safe file input.
            safe.makeSafe("data//"+args[0]);

            //if there is an input file, it will be read and the commands will be executed in top-down order until
            //reaching the bottom line
            if(args.length == 2){
                BufferedReader inputCMDS = openFile(args[1]);
                String cmds = inputCMDS.readLine();
                while (cmds != null){
                    output(cmds.split(" "), safe);
                    cmds = inputCMDS.readLine();
                }
            }
            safe.printSafe();
            Scanner kboard = new Scanner(System.in);
            //until the user enters 'q' the while loop takes in the user's input and executes it accordingly.
            while(true){
                System.out.print(">");
                String[] cmd = kboard.nextLine().split(" ");
                if(cmd[0].equals("q")){
                    System.out.println("Goodbye!");
                    break;
                }
                output(cmd, safe);
            }
        }
    }
}

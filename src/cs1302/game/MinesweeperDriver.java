package cs1302.game;

import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * Driver class that creates a new {@code MinesweeperGame} object
 * based on the seed file given by the player.
 */
public class MinesweeperDriver {

    /**
     * Creates a new {@code MinesweeperGame} object and calls its
     * {@code play} method.
     *
     * @param args the path name (argument) given by the player which
     * points to a seed file.
     * @throws ArrayIndexOutOfBoundsException if no path name (argument) is given,
     * which causes the program to exit.
     */
    public static void main(String[] args) {
        Scanner stdIn = new Scanner(System.in);
        String seedPath = null;
        try {
            seedPath = args[0];
        } catch (ArrayIndexOutOfBoundsException aie) {
            System.err.println("\nUsage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        }
        try {
            MinesweeperGame g1 = new MinesweeperGame(stdIn, seedPath);
            g1.play();
        } catch (FileNotFoundException fnfe) {
            System.err.print("\nSeed File Not Found Error: ");
            System.err.println(args[0] + " (No such file or directory)");
            System.exit(2);
        }
    } // main
} // MinesweeperDriver

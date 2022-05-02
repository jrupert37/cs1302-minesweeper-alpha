package cs1302.game;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Object class that constructs a new Minesweeper Game setup with 5-10
 * {@code rows} and 5-10 {@code col} (columns) and a certain {@code numberOfMines},
 *  all given by a seed file.
 */
public class MinesweeperGame {

    private final Scanner stdIn;
    private int rows = 0;
    private int col = 0;
    private int numberOfMines = 0;
    private int rounds = 0;
    private double score = 0;
    private boolean[][] isMine;
    private String[][] markers;
    private File seedFile;
    private Scanner seedScan;
    private ArrayList<Integer> seedNumbers = new ArrayList<Integer>();
    private String invalid = "\nInvalid Command: ";
    private String sfme = "\nSeed File Malformed Error: ";

    /**
     * Constructor method that creates a new {@code MinesweeperGame}.
     * Calls {@code parseSeedFile} to assign values to {@code rows}, {@code col},
     * and {@code numberOfMines}.
     *
     * @param stdIn    Scanner with standard input, given by the driver class.
     * @param seedPath String that provides a path to a seed file.
     * @throws FileNotFoundException if
     */
    public MinesweeperGame(Scanner stdIn, String seedPath) throws FileNotFoundException {
        this.stdIn = stdIn;
        seedFile = new File(seedPath);
        seedScan = new Scanner(seedFile);
        parseSeedFile(seedScan);
        isMine = new boolean[rows][col];
        markers = new String[rows][col];
        assignMines();
    } // constructor

    /**
     * Reads the {@code seedFile} and parses values from the file to assign them to the
     * {@code seedNumbers} ArrayList, assuming the file contains values. Values from
     * {@code seedNumbers} are then assigned to {@code rows}, {@code col},
     * and {@code numberOfMines}, assuming {@code seedNumbers} contains the
     * appropriate number of values.
     * The program exits if the number of rows and/or columns is not
     * between 5 and 10, or if the number of mines is greater than or equal to
     * the number of squares.
     *
     * @param seedScan the scanner object containing the {@code seedFile}
     * @throws IndexOutOfBoundsException if {@code seedNumbers} does not contain the appropriate
     * number of values
     */
    public void parseSeedFile(Scanner seedScan) {
        while (seedScan.hasNextInt()) {
            seedNumbers.add(seedScan.nextInt());
        } // while
        if (seedNumbers.size() % 2 == 0) {
            System.err.println(sfme + "seed file missing some information");
            System.exit(3);
        } else if (seedNumbers.size() == 3) {
            System.err.println(sfme + "mine locations expected but not given");
            System.exit(3);
        }
        try {
            rows = seedNumbers.get(0);
            if (rows < 5) {
                System.err.print(sfme);
                System.err.println("number of rows must be greater than 5");
                System.exit(3);
            } else if (rows > 10) {
                System.err.print(sfme);
                System.err.println("number of rows cannot be greater than 10");
                System.exit(3);
            }
            col = seedNumbers.get(1);
            if (col < 5) {
                System.err.print(sfme);
                System.err.println("number of columns must be greater than 5");
                System.exit(3);
            } else if (col > 10) {
                System.err.print(sfme);
                System.err.println("number of columns cannot be greater than 10");
                System.exit(3);
            }
            numberOfMines = seedNumbers.get(2);
            if (numberOfMines < 1) {
                System.err.print(sfme);
                System.err.println("number of mines must be greater than 1");
                System.exit(3);
            } else if (numberOfMines >= (rows * col)) {
                System.err.print(sfme);
                System.err.print("number of mines cannot be greater than or equal to ");
                System.err.println("the number of squares");
                System.exit(3);
            }
        } catch (IndexOutOfBoundsException seedMalformed) {
            System.out.println(sfme + "number of columns and/or mines expected but not given");
            System.exit(3);
        }
    } // readSeedFile

    /**
     * Reads values from {@code seedNumbers} (after the values assigned to {@code rows},
     * {@code col}, and {@code numberOfMines}) and sets the index (given by the values)
     * of {@code isMine} to {@code true}. Initializes {@code markers} with spaces (" ").
     * The program exits if not enough mine locations are given
     * by {@code seedNumbers}.
     *
     * @throws ArrayIndexOutOfBoundsException if the mine location given by {@code seedNumbers}
     * is not in range of the mine field.
     */
    public void assignMines() {
        int counter = 0;
        for (int i = 3; i < seedNumbers.size() - 1; i += 2) {
            int mineRow = seedNumbers.get(i);
            int mineColumn = seedNumbers.get(i + 1);
            try {
                isMine[mineRow][mineColumn] = true;
                counter++;
            } catch (ArrayIndexOutOfBoundsException aie) {
                System.err.print(sfme);
                System.err.println(aie.getMessage());
                System.exit(3);
            }
        } // for
        if (counter < numberOfMines) {
            System.err.println(sfme + "not enough mine locations given");
            System.exit(3);
        } else if (counter > numberOfMines) {
            System.err.println(sfme + "too many mine locations given");
            System.exit(3);
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                markers[i][j] = " ";
            } // for-inner
        } // for-outer
    } // assignMines

    /**
     * Called by the {@code MinesweeperDriver} class, which in turn calls
     * {@code printWelcome} to print out the welcome message. Calls {@code promptUser}
     * repeatedly until program exits from an excpetion, the game is won, or the game is lost.
     *
     * @throws FileNotFoundException if the welcome.txt file is not found
     */
    public void play() throws FileNotFoundException {
        try {
            printWelcome();
        } catch (FileNotFoundException fnfe) {
            System.err.println("resources/welcome.txt: file not found\n");
            System.exit(1);
        }
        boolean running = true;
        while (running == true) {
            promptUser();
        } // while
    } // play

    /**
     * Creates a {@code File} object from the welcome.txt file, which contains
     * the welcome message. A {@code Scanner} object prints the welcome messgae from
     * the {@code File}. Calls {@code printMineField} to first print the mine field.
     *
     * @throws FileNotFoundException which is propogated to {@code play} if the
     * welcome.txt file is not found
     */
    public void printWelcome() throws FileNotFoundException {
        File welcome = new File("resources/welcome.txt");
        Scanner welcomePrint = new Scanner(welcome);
        while (welcomePrint.hasNext()) {
            System.out.println(welcomePrint.nextLine());
        } // while
        printMineField("print");
    } // printWelcome

    /**
     * Called by the {@code play} method, prompts the user to make a play.
     * Invalid command error given if the user plays anything (including an empty play) other than
     * "r/reveal", "m/mark", "g/guess", "h/help", "q/quit", or "nofog", and the
     * mine field is then printed with a new call to {@code promptUser}.
     * If "r/reveal", "m/mark", or "g/guess" is played, the appropriate method is called
     * with a Scanner containing the row and col number of the play passed into it.
     * If "h/help" or "q/quit" is played, the appropriate method is called with no parameters
     * passed into it.

     * @throws FileNotFoundException if the gamewon.txt or gameover.txt file is not found
     */
    public void promptUser() throws FileNotFoundException {
        System.out.print("minesweeper-alpha: ");
        String fullCommand = stdIn.nextLine();
        Scanner commandScan = new Scanner(fullCommand);
        String command = "";
        if (commandScan.hasNext()) {
            command = commandScan.next();
        }
        if (command.equals("r") || command.equals("reveal")) {
            reveal(commandScan);
        } else if (command.equals("m") || command.equals("mark")) {
            mark(commandScan);
        } else if (command.equals("g") || command.equals("guess")) {
            guess(commandScan);
        } else if (command.equals("h") || command.equals("help")) {
            help();
        } else if (command.equals("q") || command.equals("quit")) {
            quit();
        } else if (command.equals("nofog")) {
            printMineField(command);
        } else {
            System.err.println(invalid + "command not recognized!");
            printMineField("print");
        } // if
    } // promptUser

    /**
     * Called by {@code promptUser} if "r/reveal" is played. Checks if the
     * index played (given by a row and column number) contains a mine. If the
     * index of {@code isMine} is {@code true}, a mine has been revealed, the player loses,
     * and the game exits.
     * If the index is {@code false}, then {@code getNumAdjMines} is called to
     * determine how many mines surround that index. The number returned by {@code getNumAdjMines}
     * is printed in the mine field and the number of rounds increases by one.
     * Invalid command error given if the player does not give a row and/or column number,
     * or if the row and/or column is out of bounds, and the number of rounds does not increase.
     * Checks to see if the player has won (if {@code isWon} returns {@code true}) and if so,
     * player's score is calculated, the win message is displayed, and the game exits. If not,
     * the mine field is printed and the game moves on.
     *
     * @param commandScan the scanner containing the row and column number given by the player.
     * @throws ArrayIndexOutOfBoundsException if the row and/or column number given
     * is not in bounds
     * @throws FileNotFoundException if the gamewon.txt or gameover.txt file is not found
     */
    public void reveal(Scanner commandScan) throws FileNotFoundException {
        int row = 0;
        int column = 0;
        int adjacentMines = 0;
        String adjMines = null;
        if (commandScan.hasNextInt()) {
            row = commandScan.nextInt();
            if (commandScan.hasNextInt()) {
                column = commandScan.nextInt();
                try {
                    if (isMine[row][column] == true) {
                        printLoss();
                    } else if (isMine[row][column] == false) {
                        if ("012345678F".contains(markers[row][column])) {
                            System.out.println("\nYou have already played this square!");
                        }
                        adjacentMines = getNumAdjMines(row, column);
                        adjMines = Integer.toString(adjacentMines);
                        markers[row][column] = adjMines;
                        rounds++;
                    } // if
                } catch (ArrayIndexOutOfBoundsException aie) {
                    System.err.println(invalid + aie.getMessage());
                } // try-catch
            } else {
                System.out.println(invalid + "column number expected but not given");
            } // if
        } else {
            System.out.println(invalid + "row and column number expected but not given");
        } // if
        if (isWon() == true) {
            score = 100.0 * rows * col / rounds;
            printWin();
            System.exit(0);
        } else {
            printMineField("print");
        } // if
    } // Reveal

    /**
     * Called by {@code reveal} if the index given by the player does not contain a mine.
     * Nested loops used to determine if the squares around a given index contain any mines.
     * The number of mines around a square is returned.
     *
     * @param row the row number given by the player
     * @param column the column number given by the player
     * @return the number of mines around a given index
     * @throws ArrayIndexOutOfBondsException if the index (row and column) given by the player
     * is on a corner or edge of the field, in which case the loop simply moves past looking in
     * squares around an index that don't exist.
     */
    private int getNumAdjMines(int row, int column) {
        int adjMines = 0;
        int notInBounds = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                try {
                    if (isMine[i][j] == true) {
                        adjMines++;
                    }
                } catch (ArrayIndexOutOfBoundsException aie) {
                  // if index is not in bounds, loop will move on withouth causing program to crash;
                    notInBounds++;
                }
            }
        }
        return adjMines;
    } // getNumAdjMines

    /**
     * Called by {@code promptUser} if "m/mark" is played. Adds a flag ("F") to the index of
     * {@code markers} given by the player and is printed to the mine field. The number of rounds
     * increases by one.
     * Inavlid command error given if the index given is
     * out of bounds, or if a row and/or column number is not given, and the number of rounds
     * does not change.
     * Checks to see if the player has won (if {@code isWon} returns {@code true}) and if so,
     * player's score is calculated, the win message is displayed, and the game exits. If not,
     * the mine field is printed and the game moves on.
     *
     * @param commandScan the scanner containing the row and column number given by the player.
     * @throws ArrayIndexOutOfBoundsException if the row and/or column
     * number given is out of bounds
     * @throws FileNotFoundException if the gamewon.txt or gameover.txt file is not found.
     */
    public void mark(Scanner commandScan) throws FileNotFoundException {
        int row = 0;
        int column = 0;
        if (commandScan.hasNextInt()) {
            row = commandScan.nextInt();
            if (commandScan.hasNextInt()) {
                column = commandScan.nextInt();
                try {
                    if ("012345678F".contains(markers[row][column])) {
                        System.out.println("\nYou have alread played this square!");
                    }
                    markers[row][column] = "F"; // try-catch
                    rounds++;
                } catch (ArrayIndexOutOfBoundsException aie) {
                    System.err.println(invalid + aie.getMessage());
                }
            } else {
                System.err.println(invalid + "column number expected but not given");
            }
        } else {
            System.err.println(invalid + "row and column number expected but not given");
        } // if
        if (isWon() == true) {
            score = 100.0 * rows * col / rounds;
            printWin();
            System.exit(0);
        } else {
            printMineField("print");
        } // if
    } // Mark

    /**
     * Called by {@code promptUser} if "g/guess" is played. Adds a "?" to the index of
     * {@code markers} given by the player and is printed to the mine field, and the number
     * of rounds played increases by one.
     * Invalid command error given if the index given is out of bounds, or if a row and/or column
     * number is not given, and the number of rounds does not increase.
     *
     * @param commandScan the scanner containing the row and column
     * number given by the player
     * @throws ArrayIndexOutOfBoundsException if the row and/or column number given
     * is out of bounds
     */
    public void guess(Scanner commandScan) {
        int row = 0;
        int column = 0;
        if (commandScan.hasNextInt()) {
            row = commandScan.nextInt();
            if (commandScan.hasNextInt()) {
                column = commandScan.nextInt();
                try {
                    markers[row][column] = "?"; // try-catch
                    rounds++;
                } catch (ArrayIndexOutOfBoundsException aie) {
                    System.err.println(invalid + aie.getMessage());
                }
            } else {
                System.err.println(invalid + "column number expected but not given");
            }
        } else {
            System.err.println(invalid + "row and column numbers expected but not given");
        }
        printMineField("print");
    } // Guess

    /**
     * Called by {@code promptUser} if "h/help" is played. Displays the list of valid commands
     * the player can give (not including nofog). The number of rounds increases by one
     * and the mine field is printed.
     */
    public void help() {
        System.out.println("\nCommands Available...");
        System.out.println(" - Reveal: r/reveal row col");
        System.out.println(" - Mark: m/mark     row col");
        System.out.println(" - Guess: g/guess   row col");
        System.out.println(" - Help: h/help");
        System.out.println(" - Quit: q/quit");
        rounds++;
        printMineField("print");
    } // printHelp

    /**
     * Called by {@code promptUser} if "q/quit" is played. Displays a quit message and
     * the game exits.
     */
    public void quit() {
        System.out.println("\nQuitting the game...");
        System.out.println("Bye!");
        System.exit(0);
    }

    /**
     * Called by {@code promptUser} if "nofog" is played. Prints a "cheat" version of
     * the mine field that indicates where the mines are located.
     */
    public void printMineField(String arg) {
        rounds++;
        System.out.println("\n Rounds Completed: " + rounds + "\n");
        for (int i = 0; i < rows; i++) {
            System.out.print(" " + i + " |");
            for (int j = 0; j < col; j++) {
                if (arg.equals("nofog") && isMine[i][j] == true) {
                    System.out.print("<" + markers[i][j] + ">|");
                } else {
                    System.out.print(" " + markers[i][j] + " |");
                } // if
            } // for-inner
            System.out.println();
        } // for-outer
        System.out.print("     ");
        for (int k = 0; k < col - 1; k++) {
            System.out.print(k + "   ");
        }
        System.out.println(col -  1 + "\n");
    } // printNoFog

    /**
     * Called each time {@code reveal} or {@code mark} is called to determine
     * if the player has won. If all mine indexes have been marked with a flag
     * ("F") AND all non-mine locations have been revealed, the player wins,
     * and the method returns true, otherwise it returns false.
     *
     * @return true if mines have been marked with a flag and non-mine
     * locations have been successfully revealed, otherwise returns false
     */
    public boolean isWon() {
        int numReveals = 0;
        int numFlags = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if ("012345678".contains(markers[i][j])) {
                    numReveals++;
                }
                if (markers[i][j] == "F") {
                    numFlags++;
                }
            }
        }
        int nonMines = (rows * col) - numberOfMines;
        if (numReveals == nonMines && numFlags == numberOfMines) {
            return true;
        } else {
            return false;
        }
    } // isWon

    /**
     * Called by {@code reveal} or {@code mark} if {@code isWon} returns
     * {@code true}. Creates a {@code File} from the gamewon.txt file, which
     * passes to a {@code Scanner} to be printed out. Prints out the player's score
     * on the last line, then the game exits.
     *
     * @throws FileNotFoundException if the gamewon.txt file is not found
     */
    public void printWin() throws FileNotFoundException {
        System.out.println();
        File win = new File("resources/gamewon.txt");
        Scanner winPrint = new Scanner(win);
        int counter = 0;
        while (winPrint.hasNext()) {
            counter++;
            if (counter != 19) {
                System.out.println(winPrint.nextLine());
            } else {
                System.out.print(winPrint.nextLine());
                System.out.printf(" %.2f\n", score);
            }
        }
        System.out.println();
        System.exit(0);
    } // printWin

    /**
     * Called by {@code reveal} if the index given by the player
     * contains a mine. Creates a {@code File} from the gameover.txt file,
     * which passes to a {@code Scanner} to be printed out. Prints out
     * the "game over" message then the game exits.
     *
     * @throws FileNotFoundException if the gameover.txt file is not found
     */
    public void printLoss() throws FileNotFoundException {
        System.out.println();
        File loss = new File("resources/gameover.txt");
        Scanner lossPrint = new Scanner(loss);
        while (lossPrint.hasNext()) {
            System.out.println(lossPrint.nextLine());
        } // while
        System.out.println();
        System.exit(0);
    } // printWelcome

} // MinesweeperGame

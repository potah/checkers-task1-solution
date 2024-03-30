import java.util.Scanner;

public class Checkers {

    private final static String[][] board = new String[8][8];
    public static boolean player_one = true;
    public static boolean player_two = false;
    public static Scanner scan = new Scanner(System.in);

    public final static String exitx = "exit";
    public final static String tox = "to";
    public final static String view = "View";

    public final static String b = "b";
    public final static String w = "w";
    public final static String bk = "B";
    public final static String wk = "W";
    public final static String delim = "|";
    public final static String emptyString = "";
    public final static String space = " ";

    private static void initialiseBoard() {
        final int rows = board.length;
        final int columns = board[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                board[row][col] = space;
                if ((row < 3 || row >= 5) && (row + col) % 2 != 0) {
                    if (row < 3) {
                        board[row][col] = w;
                    } else {
                        board[row][col] = b;
                    }
                }
            }
        }
    }

    /**
     * Displays the current state of the board to the console.
     */
    private static void displayBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                System.out.print(delim + board[row][col]);
            }
            System.out.println(delim + emptyString);
        }
        System.out.println(emptyString);
    }

    private static void startGame() {
        boolean flagError = false;

        int x1 = -1, x2 = -1, y1 = -1, y2 = -1;

        while (true) {
            String input = scan.nextLine();

            if (input.equals(exitx)) {
                System.out.println(emptyString);
                System.exit(0);
            } else if (input.equals(view)) {
                System.out.println(emptyString);
                displayBoard();
            } else {
                String[] instruction = input.split(space);
                if (instruction.length != 3) {
                    flagError = true;
                } else {
                    String start = instruction[0];
                    String end = instruction[2];
                    if (start.length() != 2 || end.length() != 2 || !instruction[1].equals(tox)) {
                        flagError = true;
                    } else {
                        x1 = charToIndex(start.charAt(0));
                        y1 = charToIndex(end.charAt(0));
                        try {
                            x2 = Integer.parseInt(String.valueOf(start.charAt(1))) - 1;
                            y2 = Integer.parseInt(String.valueOf(end.charAt(1))) - 1;
                        } catch (NumberFormatException e) {
                            flagError = true;
                        }

                        if (!flagError && (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7)) {
                            flagError = true;
                        }

                        if (!flagError) {
                            isGameOver();

                            if (isValidMove(x2, x1, y2, y1)) {
                                processMove(x2, x1, y2, y1);
                                player_one = !player_one;
                                player_two = !player_two;
                                displayBoard();
                                isGameOver();
                            } else {
                                flagError = true;
                            }
                        }
                    }
                }

                if (flagError) {
                    System.out.println("Error!");
                    System.out.println();
                    flagError = false;
                    displayBoard();
                }
            }
        }
    }

    private static int charToIndex(char c) {
        if (c >= 'A' && c <= 'H') {
            return c - 'A';
        } else if (c >= 'a' && c <= 'h') {
            return c - 'a';
        }
        return -1;
    }

    /**
     * Processes a player's move.
     *
     * @param move A string representing the player's move (e.g., "C3 to D4").
     * @return true if the move is valid and executed, false otherwise.
     */
    private static void processMove(int fromRow, int fromCol, int toRow, int toCol) {
        String start = board[fromRow][fromCol];

        board[fromRow][fromCol] = space;
        board[toRow][toCol] = start;
        for (int i = 0; i < 8; i++) {
            if (board[0][i].equals(b)) {
                board[0][i] = bk;
            }
        }
        for (int i = 0; i < 8; i++) {
            if (board[7][i].equals(w)) {
                board[7][i] = wk;
            }
        }
    }

    /**
     * Checks if a move is valid.
     *
     * @param fromRow the starting row of the move.
     * @param fromCol the starting column of the move.
     * @param toRow   the ending row of the move.
     * @param toCol   the ending column of the move.
     * @return true if the move is legal, false otherwise.
     */
    private static boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        int deltaX = toRow - fromRow;
        int deltaY = toCol - fromCol;
        String startPiece = board[fromRow][fromCol];
        String end = board[toRow][toCol];

        if (!end.equals(space)) {
            return false;
        }

        // Movement checks (either move one square diagonally or jump over two)
        if (!((Math.abs(deltaX) == 1 && Math.abs(deltaY) == 1) || (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 2))) {
            return false;
        }

        // Player one piece (b or bk)
        if (player_one && (startPiece.equals(b) || startPiece.equals(bk))) {
            if (startPiece.equals(b)) {
                // b can only move forward (deltaX -1 for normal, -2 for capture)
                if (!(deltaX == -1 || deltaX == -2))
                    return false;
            }
            if (Math.abs(deltaX) == 2) {
                // Check for a piece to jump over
                int midRow = fromRow + deltaX / 2;
                int midCol = fromCol + deltaY / 2;
                String midPiece = board[midRow][midCol];
                if (!midPiece.equals(w) && !midPiece.equals(wk) && !midPiece.equals(b) && !midPiece.equals(bk)) {
                    return false; // No piece to jump over
                }
                // Capture: Clear the jumped piece for actual game move; for validation, this
                // might be skipped or handled elsewhere
                if (!(board[midRow][midCol]).equals(startPiece))
                    board[midRow][midCol] = space;
            }
            return true; // Move is valid
        }

        // Player two piece (w or wk)
        if (player_two && (startPiece.equals(w) || startPiece.equals(wk))) {
            if (startPiece.equals(w)) {
                // w can only move forward (deltaX 1 for normal, 2 for capture)
                if (!(deltaX == 1 || deltaX == 2))
                    return false;
            }
            if (Math.abs(deltaX) == 2) {
                // Check for a piece to jump over
                int midRow = fromRow + deltaX / 2;
                int midCol = fromCol + deltaY / 2;
                String midPiece = board[midRow][midCol];
                if (!midPiece.equals(b) && !midPiece.equals(bk) && !midPiece.equals(w) && !midPiece.equals(wk)) {
                    return false; // No piece to jump over
                }
                // Capture: Clear the jumped piece for actual game move; for validation, this
                // might be skipped or handled elsewhere
                if (!(board[midRow][midCol]).equals(startPiece))
                    board[midRow][midCol] = space;
            }
            return true; // Move is valid
        }

        return false; // If none of the conditions are met, the move is invalid
    }

    /**
     * Checks if the game has ended.
     * The program should terminate if the game has finished.
     */
    private static void isGameOver() {
        int countBlack = 0, countWhite = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].equals(b) || board[row][col].equals(bk)) {
                    countBlack += 1;
                }
                if (board[row][col].equals(w) || board[row][col].equals(wk)) {
                    countWhite += 1;
                }
            }
        }
        if (countBlack == 0 || countWhite == 0) {
            System.exit(0);
        }
    }

    /**
     * Main method to run the game.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        initialiseBoard();
        displayBoard();
        startGame();
    }
}

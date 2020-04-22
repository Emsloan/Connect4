package core;

/**
 * Class Connect4 holds the logic for the Connect4 game.
 * 
 * @author Elliott Sloan
 * @version 1
 *
 */
public class Connect4 {

    public static class SuccessObject extends Connect4 {
        private boolean x;
        private char player;
        private int row;
        private int col;

        public SuccessObject() {
            setPlayer(' ');
            setX(false);
            setRow(-1);
            setCol(-1);
            
        }

        /**
         * @return the x
         */
        public boolean isX() {
            return x;
        }

        /**
         * @param x the x to set
         */
        public void setX(boolean x) {
            this.x = x;
        }

        /**
         * @return the row
         */
        public int getRow() {
            return row;
        }

        /**
         * @param row the row to set
         */
        public void setRow(int row) {
            this.row = row;
        }

        /**
         * @return the col
         */
        public int getCol() {
            return col;
        }

        /**
         * @param col the col to set
         */
        public void setCol(int col) {
            this.col = col;
        }

        /**
         * @return the player
         */
        public char getPlayer() {
            return player;
        }

        /**
         * @param player the player to set
         */
        public void setPlayer(char player) {
            this.player = player;
        }
    }

    /**
     * board is a 2D array that holds all of the potential pieces.
     */
    char[][] board = new char[6][7];
    /**
     * PLAYER_X AND PLAYER_O are constants that represent the characters used for
     * player pieces.
     */
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';
    /*
     * EMPTY is a constant that represents the character used for empty places on
     * the board.
     */
    public static final char EMPTY = ' ';

    /**
     * ROW and COL and constants used to reference the dimensions of the board.
     */
    public static final int ROW = 6;
    public static final int COL = 7;

    /**
     * STREAK_X and STREAK_O are constants used to check if the a row, column, or
     * diagonal has 4-in-a-row.
     */
    private static final String STREAK_X = "XXXX";
    private static final String STREAK_O = "OOOO";

    /**
     * Connect4 is the default constructor for Connect4. It fills the board with
     * empty spaces.
     */
    public Connect4() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    /**
     * Connect4(test) is an optional constructor that can be set to fill the board
     * with certain configurations for testing.
     * 
     * @param test
     */
    public Connect4(boolean test) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    /**
     * getPiece() is a method that returns the piece, or character, at the input
     * coordinates.
     * 
     * @param row
     * @param col
     * @return [the requested piece]
     */

    public char getPiece(int row, int col) {
        return board[row][col];

    }

    /**
     * addPiece() is a method that "drops" a piece in a column.
     * 
     * @param piece [the piece to be added, X or O]
     * @param col
     * @return [a value stating if a piece was successfully added]
     */
    public SuccessObject addPiece(char piece, int col) {

        SuccessObject obj = new SuccessObject();

        if (isFull(col)) {
            return obj;
        }

        char hold;
        for (int i = 5; i >= 0; i--) {
            hold = getPiece(i, col);
            if (hold == EMPTY) {
                board[i][col] = piece;
                obj.setRow(i);
                obj.setCol(col);
                break;
            }
        }
        obj.setX(true);
        return obj;

    }

    /**
     * isFull() verifies if a column is full ensuring a piece isn't added to an
     * invalid space.
     * 
     * @param col2
     * @return [a value stating if a column is full]
     */
    boolean isFull(int col2) {
        if (getPiece(0, col2) == EMPTY) {
            return false;
        } else
            return true;
    }
    boolean boardFull() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if(board[i][j] == EMPTY) {
                    return false;
                }
            }
        }    
        return true;
    }

    /**
     * checkWin() checks all rows, columns, and diagonals to verify if either player
     * has won.
     * 
     * @return [a value stating if a player has won]
     */
    public SuccessObject checkWin() {
        SuccessObject obj = new SuccessObject();

        // horizontal
        String[] row = new String[6];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                row[i] += getPiece(i, j);
            }
            if (row[i].contains(STREAK_X) ) {
                obj.setX(true);
                obj.setPlayer(PLAYER_X);
                return obj; 
            }
            if(row[i].contains(STREAK_O)) {
                obj.setX(true);
                obj.setPlayer(PLAYER_O);
                return obj;                 
            }

        }

        // vertical
        String[] col = new String[7];
        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {
                col[i] += getPiece(j, i);
            }
            if (col[i].contains(STREAK_X) ) {
                obj.setX(true);
                obj.setPlayer(PLAYER_X);
                return obj; 
            }
            if(col[i].contains(STREAK_O)) {
                obj.setX(true);
                obj.setPlayer(PLAYER_O);
                return obj;                 
            }
        }

        // diagonal up
        String[] diagonal = diagonalUp();
        for (int i = 0; i < diagonal.length; i++) {
            if (diagonal[i].contains(STREAK_X) ) {
                obj.setX(true);
                obj.setPlayer(PLAYER_X);
                return obj; 
            }
            if(diagonal[i].contains(STREAK_O)) {
                obj.setX(true);
                obj.setPlayer(PLAYER_O);
                return obj;                 
            }
        }
        // diagonal down

        diagonal = diagonalDown();
        for (int i = 0; i < diagonal.length; i++) {
            if (diagonal[i].contains(STREAK_X) ) {
                obj.setX(true);
                obj.setPlayer(PLAYER_X);
                return obj; 
            }
            if(diagonal[i].contains(STREAK_O)) {
                obj.setX(true);
                obj.setPlayer(PLAYER_O);
                return obj;                 
            }
        }
        return obj;

    }

    /**
     * diagonalUp() is a helper method for checkWin() and returns an array
     * containing half of the diagonals on the board.
     * 
     * @return [a string containing all of the ascending diagonals longer than 3]
     */
    private String[] diagonalUp() {
        String[] diagonal = new String[6];
        for (int i = 0; i < diagonal.length; i++) {
            diagonal[i] = "";
        }

        diagonal[0] += getPiece(3, 0);
        diagonal[0] += getPiece(2, 1);
        diagonal[0] += getPiece(1, 2);
        diagonal[0] += getPiece(0, 3);

        diagonal[1] += getPiece(4, 0);
        diagonal[1] += getPiece(3, 1);
        diagonal[1] += getPiece(2, 2);
        diagonal[1] += getPiece(1, 3);
        diagonal[1] += getPiece(0, 4);

        diagonal[2] += getPiece(5, 0);
        diagonal[2] += getPiece(4, 1);
        diagonal[2] += getPiece(3, 2);
        diagonal[2] += getPiece(2, 3);
        diagonal[2] += getPiece(1, 4);
        diagonal[2] += getPiece(0, 5);

        diagonal[3] += getPiece(5, 1);
        diagonal[3] += getPiece(4, 2);
        diagonal[3] += getPiece(3, 3);
        diagonal[3] += getPiece(2, 4);
        diagonal[3] += getPiece(1, 5);
        diagonal[3] += getPiece(0, 6);

        diagonal[4] += getPiece(5, 2);
        diagonal[4] += getPiece(4, 3);
        diagonal[4] += getPiece(3, 4);
        diagonal[4] += getPiece(2, 5);
        diagonal[4] += getPiece(1, 6);

        diagonal[5] += getPiece(5, 3);
        diagonal[5] += getPiece(4, 4);
        diagonal[5] += getPiece(3, 5);
        diagonal[5] += getPiece(2, 6);

        return diagonal;

    }

    /**
     * diagonalDown() is a helper method for checkWin() and returns an array
     * containing half of the diagonals on the board.
     * 
     * @return [a string containing all of the descending diagonals longer than 3]
     * 
     */

    private String[] diagonalDown() {
        String[] diagonal = new String[6];
        for (int i = 0; i < diagonal.length; i++) {
            diagonal[i] = "";
        }

        diagonal[0] += getPiece(2, 0);
        diagonal[0] += getPiece(3, 1);
        diagonal[0] += getPiece(4, 2);
        diagonal[0] += getPiece(5, 3);

        diagonal[1] += getPiece(1, 0);
        diagonal[1] += getPiece(2, 1);
        diagonal[1] += getPiece(3, 2);
        diagonal[1] += getPiece(4, 3);
        diagonal[1] += getPiece(5, 4);

        diagonal[2] += getPiece(0, 0);
        diagonal[2] += getPiece(1, 1);
        diagonal[2] += getPiece(2, 2);
        diagonal[2] += getPiece(3, 3);
        diagonal[2] += getPiece(4, 4);
        diagonal[2] += getPiece(5, 5);

        diagonal[3] += getPiece(0, 1);
        diagonal[3] += getPiece(1, 2);
        diagonal[3] += getPiece(2, 3);
        diagonal[3] += getPiece(3, 4);
        diagonal[3] += getPiece(4, 5);
        diagonal[3] += getPiece(5, 6);

        diagonal[4] += getPiece(0, 2);
        diagonal[4] += getPiece(1, 3);
        diagonal[4] += getPiece(2, 4);
        diagonal[4] += getPiece(3, 5);
        diagonal[4] += getPiece(4, 6);

        diagonal[5] += getPiece(0, 3);
        diagonal[5] += getPiece(1, 4);
        diagonal[5] += getPiece(2, 5);
        diagonal[5] += getPiece(3, 6);

        return diagonal;

    }

    /**
     * toString() returns a String representation of the board for printing
     * purposes.
     */
    public String toString() {
        String board = "";
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                board += ("|" + getPiece(i, j));
            }
            board += ("|\n");
        }
        return board;
    }

}

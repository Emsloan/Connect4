package core;

import java.util.Random;

/**
 * This class is called whenever Player 2 takes a turn in a game against a
 * computer.
 * 
 * @author exman
 *
 */
public class Connect4ComputerPlayer extends Connect4 {

    /**
     * addRandPiece generates a random column to drop Player 2's piece.
     * 
     * @param game [the current Connect4 game object being used]
     */
    public static SuccessObject addRandPiece(Connect4 game) {

        SuccessObject object = null;
        Random obj = new Random();
        int col = obj.nextInt(7);
        object = game.addPiece(PLAYER_O, col);

        while (!object.isX()) {
            col = obj.nextInt(7);
            object = game.addPiece(PLAYER_O, col);
        }
        return object;

    }

}

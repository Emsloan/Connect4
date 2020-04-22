package ui;

import core.*;
import ui.Connect4GUI;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The class Connect4TextConsole acts as the driver class for the Connect4 game.
 * It displays messages to the players in the console and handles turns as well
 * as ensuring correct user input.
 * 
 * @author Elliott Sloan
 * @version 1
 */
public class Connect4TextConsole extends Connect4 {

    /**
     * computer decides if a game is against a player or computer
     */
    static int computer = 1;

    /**
     * scan reads the user input.
     */
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Welcome to Connect4! Please enter 1 to play with  GUI or 2 to play in the text-console.");

        try {
            int choice = scan.nextInt();
            if (choice == 1) {
                Connect4GUI.main(args);

            } else {
                textRun();
            }

        } catch (InputMismatchException e) {
            System.out.println("That isn't a valid input. Please restart the program.");
            System.exit(1);
        }

    }

    /**
     * textRun is the driver for running Connect4 on the console. This is not
     * accessed if the player chooses to use a GUI.
     */

    private static void textRun() {
        /**
         * Connect4 object 'test' is the instance of the Connect4 class created when the
         * game begins.
         */
        Connect4TextConsole test = new Connect4TextConsole();

        /**
         * won is defaulted to false and is set to true when the game is won by a
         * player.
         */
        boolean won = false;

        System.out.println("Please enter 1 for a 2-player game or 2 to play against a Computer opponent.");
        computer = scan.nextInt();

        System.out.println("Player 1 goes first!" + "\n" + test.toString());

        char winner = ' ';
        while (!won) {

            playerXturn(test);
            System.out.println(test.toString());

            if (test.checkWin().isX()) {
                won = true;
                winner = test.checkWin().getPlayer();
                break;
            }
            playerOturn(test);
            System.out.println(test.toString());

            if (test.checkWin().isX()) {
                won = true;
                winner = test.checkWin().getPlayer();
                break;
            }
        }

        System.out.println(winner + " wins!");

    }

    /**
     * playerOturn is the twin method of playerXturn. It tells player 2 that it is
     * their turn, reads their input, ensures it is a valid choice, and calls the
     * addPiece method to add their piece to the board.
     * 
     * @param game [game is the Connect4 object created in main()]
     */
    private static void playerOturn(Connect4 game) {

        int choice = 0;
        if (computer == 1) {
            while (choice > 7 || choice < 1) {
                System.out.println("Player 2, please enter a column (1-7) to place your piece.");
                choice = scan.nextInt();
                if (choice > 7 || choice < 1) {
                    System.out.println(choice + " is not a valid column.");
                }
            }
            if (!game.addPiece(PLAYER_O, choice - 1).isX()) {
                System.out.println("Column full. Please make a different selection.");
                playerOturn(game);
            }
        }
        if (computer == 2) {
            Connect4ComputerPlayer.addRandPiece(game);

        }

    }

    /**
     * playerXturn is the twin method of playerOturn. It tells player 1 that it is
     * their turn, reads their input, ensures it is a valid choice, and calls the
     * addPiece method to add their piece to the board.
     * 
     * @param game [game is the Connect4 object created in main()]
     */
    private static void playerXturn(Connect4 game) {
        int choice = 0;
        while (choice > 7 || choice < 1) {
            System.out.println("Player 1, please enter a column (1-7) to place your piece.");
            choice = scan.nextInt();
            if (choice > 7 || choice < 1) {
                System.out.println(choice + " is not a valid column.");
            }
        }

        if (!game.addPiece(PLAYER_X, choice - 1).isX()) {
            System.out.println("Column full. Please make a different selection.");
            choice = 0;
            playerXturn(game);
        }

    }

}

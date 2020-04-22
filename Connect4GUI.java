package ui;

import core.*;
import core.Connect4.SuccessObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * The class Connect4GUI creates and displays a GUI for players to play
 * Connect4, using the core package for it's logic.
 * 
 * @author exman
 * @version 1
 */
public class Connect4GUI extends Application {
    /**
     * game is the Connect4.java instance where the game is played
     */
    Connect4 game = new Connect4();

    /**
     * gameType is checked when turns are taken to see if a computer move must be
     * taken or if player input should be waited for
     */
    int gameType = 0;
    /**
     * turnCount tracks the turn and helps decide who wins
     */
    int turnCount = 0;
    Label status = new Label("Player 1's Turn.");
    GridPane board = new GridPane();

    /**
     * start creates the start window and the game screen
     * 
     * @param start [the first Stage that players see]
     */
    @Override
    public void start(Stage choice) throws Exception {

        /**
         * the gameWindow Stage that holds the bulk of the GUI
         */
        Stage gameWindow = new Stage();

        ColumnHandler cHandle = new ColumnHandler();

        for (int k = 0; k < 7; k++) {
            String title = "";
            title += k + 1;

            Button button = new Button(title);
            button.setOnAction(cHandle);
            board.add(button, k, 0);
        }
        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Circle circle = new Circle();
                circle.setRadius(20);
                circle.setStroke(Color.BLACK);
                circle.setFill(null);
                board.add(circle, j, i);
                board.setHgap(45);
                board.setVgap(45);
            }
        }

        board.add(status, 7, 3);

        Scene boardScene = new Scene(board, 750, 600);

        gameWindow.setTitle("Connect4");
        gameWindow.setScene(boardScene);
        gameWindow.show();

        GridPane pane = new GridPane();

        Button computer = new Button("Computer");
        Button player = new Button("Player");
        Label prompt = new Label("Play against a real player or computer?");

        /**
         * Inner class that handles the button pushes of Button computer Changes the
         * gametype and closes choice
         * 
         * @author exman
         *
         */
        class ComputerHandler implements EventHandler<ActionEvent> {

            @Override
            public void handle(ActionEvent event) {
                gameType = 2;
                choice.close();

            }

        }
        /**
         * Inner class that handles the button pushes of Button player Changes the
         * gametype and closes choice
         * 
         * @author exman
         *
         */
        class PlayerHandler implements EventHandler<ActionEvent> {

            @Override
            public void handle(ActionEvent event) {
                gameType = 1;
                choice.close();

            }

        }

        ComputerHandler compHandler = new ComputerHandler();
        computer.setOnAction(compHandler);

        PlayerHandler playHandler = new PlayerHandler();
        player.setOnAction(playHandler);

        pane.add(prompt, 0, 0);
        pane.add(player, 0, 1);
        pane.add(computer, 1, 1);

        Scene scene = new Scene(pane, 300, 100);
        choice.setTitle("Connect4");
        choice.setScene(scene);
        choice.alwaysOnTopProperty();
        choice.show();

    }

    /**
     * method that updates the GUI to display the current board layout
     */
    private void updateBoard() {
        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                char hold = game.getPiece(i - 1, j);

                if (hold == Connect4.PLAYER_X) {
                    Circle circle = new Circle();
                    circle.setRadius(20);
                    circle.setStroke(Color.BLACK);
                    Paint color = Paint.valueOf("red");
                    circle.setFill(color);
                    board.add(circle, j, i);
                } else if (hold == Connect4.PLAYER_O) {
                    Circle circle = new Circle();
                    circle.setRadius(20);
                    circle.setStroke(Color.BLACK);
                    Paint color = Paint.valueOf("yellow");
                    circle.setFill(color);
                    board.add(circle, j, i);
                }
            }
        }

    }
    
    /**
     * method called once a player or computer has won. 
     * Displays a new window stating the winner
     * @param victor
     */

    private void gameOver(String victor) {
        Stage gameOver = new Stage();
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 200, 200);
        Label label = new Label(victor);
        pane.getChildren().add(label);
        gameOver.alwaysOnTopProperty();
        gameOver.setScene(scene);
        gameOver.show();

    }
    
    /**
     * Event handler for all of the column buttons. 
     * Calls the addPiece() method for the game object
     * Calls the updateBoard() method if addPiece() is successful
     * calls the checkWin() method to see if the game has been won
     * prints a readout if a column is full
     * @author exman
     *
     */

    class ColumnHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            Button button = (Button) e.getSource();
            int col = Integer.parseInt(button.getText()) - 1;

            SuccessObject success;
            if (turnCount % 2 == 0) {
                success = game.addPiece(Connect4.PLAYER_X, col);
                if (success.isX()) {
                    updateBoard();
                    if (game.checkWin().isX()) {
                        String victor = "";
                        victor = "Player 1 won!";
                        gameOver(victor);
                        return;
                    }
                    turnCount++;
                    if (gameType == 2) {
                        Connect4ComputerPlayer.addRandPiece(game);
                        updateBoard();
                        if (game.checkWin().isX()) {
                            String victor = "";
                            victor = "Computer won!";
                            gameOver(victor);
                            return;
                        }
                        turnCount++;
                        status.setText("Player 1's Turn.");
                        return;
                    }
                    status.setText("Player 2's Turn.");
                    return;
                } else {
                    status.setText("Column full. Make another choice.");
                    return;

                }
            }

            if (turnCount % 2 != 0 && gameType == 1) {

                success = game.addPiece(Connect4.PLAYER_O, col);
                if (success.isX()) {
                    updateBoard();
                    if (game.checkWin().isX()) {
                        String victor = "";
                        victor = "Player 2 won!";
                        gameOver(victor);
                        return;
                    }
                    turnCount++;
                    status.setText("Player 1's Turn.");
                    return;
                } else {
                    status.setText("Column full. Make another choice.");
                    return;
                }

            }

        }

    }

    /**
     * is called if the GUI option is selected
     * @param args
     */
    
    public static void main(String[] args) {
        launch(args);
    }

}

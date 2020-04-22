package core;

import core.Connect4;
import core.Connect4Constants;
import java.io.*;
import java.net.*;
import java.util.Date;

import core.Connect4.SuccessObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Starts a server and handles all logic for a connect4game between players, or
 * a player and a computer, independent of ui choice. Verz similar to the
 * TicTacToeServer example.
 * 
 * @author exman
 * @version 1
 */
public class Connect4Server extends Application {

    public static int PLAYER1 = 1; // Indicate player 1
    public static int PLAYER2 = 2; // Indicate player 2
    public static int PLAYER1_WON = 1; // Indicate player 1 won
    public static int PLAYER2_WON = 2; // Indicate player 2 won
    public static int DRAW = 3; // Indicate a draw
    public static int CONTINUE = 4; // Indicate to continue
    public static int INVALID = 5; // Indicate pick new column
    public static int VALID = 6; // Indicate add successful

    private int sessionNo = 1;

    private boolean choiceMade = false;

    private int gameType = -1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea taLog = new TextArea();

        Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
        primaryStage.setTitle("Connect4Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage

        Stage choice = new Stage();
        GridPane gameChoicePane = new GridPane();
        Scene gameChoice = new Scene(gameChoicePane, 300, 100);

        Button computer = new Button("Computer");
        Button player = new Button("Player");
        Label prompt = new Label("Play against a real player or computer?");

        gameChoicePane.add(prompt, 0, 0);
        gameChoicePane.add(player, 0, 1);
        gameChoicePane.add(computer, 1, 1);

        choice.setTitle("Connect4 Game Type");
        choice.setScene(gameChoice);

        class ComputerHandler implements EventHandler<ActionEvent> {

            @Override
            public void handle(ActionEvent event) {
                gameType = 2;
                choice.close();
                primaryStage.show(); // Display the stage
                choiceMade = true;

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
                primaryStage.show(); // Display the stage
                choiceMade = true;
            }

        }

        // Set the computer and player button actions

        ComputerHandler compHandler = new ComputerHandler();
        computer.setOnAction(compHandler);

        PlayerHandler playHandler = new PlayerHandler();
        player.setOnAction(playHandler);

        choice.show();

        new Thread(() -> {

            // Wait until server owner selects gameType
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } while (choiceMade == false);

            try {

                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8016);

                primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                int port = serverSocket.getLocalPort();
                Platform.runLater(() -> taLog.appendText(new Date() + ": Server started at socket " + port + "\n"));

                if (gameType == 2) {
                    // Ready to create a session for one player and a computer
                    while (true) {
                        Platform.runLater(() -> taLog
                                .appendText(new Date() + ": Wait for player to join session " + sessionNo + '\n'));

                        // Connect to player 1
                        Socket player1 = serverSocket.accept();

                        Platform.runLater(() -> {
                            taLog.appendText(new Date() + ": Player 1 joined session " + sessionNo + '\n');
                            taLog.appendText(
                                    "Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');
                            try {
                                // Notify that the player is Player 1
                                DataOutputStream outStream = new DataOutputStream(player1.getOutputStream());
                                outStream.writeInt(PLAYER1);

                                DataInputStream inStream = new DataInputStream(player1.getInputStream());

                                // Display this session and increment session number
                                Platform.runLater(() -> taLog
                                        .appendText(new Date() + ": Start a thread for session " + sessionNo++ + '\n'));

                                // Launch a new thread for this session of one player and a computer
                                new Thread(new HandleASession(player1, inStream, outStream)).start();

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        });

                    }

                }
                if (gameType == 1) {
                    // Ready to create a session for every two players
                    // Ready to create a session for every two players
                    while (true) {
                        Platform.runLater(() -> taLog
                                .appendText(new Date() + ": Wait for players to join session " + sessionNo + '\n'));

                        // Connect to player 1
                        Socket player1 = serverSocket.accept();

                        Platform.runLater(() -> {
                            taLog.appendText(new Date() + ": Player 1 joined session " + sessionNo + '\n');
                            taLog.appendText(
                                    "Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');
                        });

                        // Notify that the player is Player 1
                        new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                        // Connect to player 2
                        Socket player2 = serverSocket.accept();

                        Platform.runLater(() -> {
                            taLog.appendText(new Date() + ": Player 2 joined session " + sessionNo + '\n');
                            taLog.appendText(
                                    "Player 2's IP address" + player2.getInetAddress().getHostAddress() + '\n');
                        });

                        // Notify that the player is Player 2
                        new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

                        // Display this session and increment session number
                        Platform.runLater(() -> taLog
                                .appendText(new Date() + ": Start a thread for session " + sessionNo++ + '\n'));

                        // Launch a new thread for this session of two players
                        new Thread(new HandleASession(player1, player2)).start();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

    }

    class HandleASession implements Runnable, Connect4Constants {
        private Socket player1;
        private Socket player2;

        Connect4 game;
        @SuppressWarnings("unused")
        private DataInputStream fromPlayer1;
        @SuppressWarnings("unused")
        private DataOutputStream toPlayer1;
        @SuppressWarnings("unused")
        private DataInputStream fromPlayer2;
        @SuppressWarnings("unused")

        private DataOutputStream toPlayer2;

        @SuppressWarnings("unused")
        private boolean continueToPlay = true;

        public HandleASession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;
            game = new Connect4();
        }

        // single player session handler
        public HandleASession(Socket player1, DataInputStream inStream, DataOutputStream outStream) {
            this.player1 = player1;
            game = new Connect4();

            this.fromPlayer1 = inStream;
            this.toPlayer1 = outStream;
        }

        public void run() {

            if (gameType == 1) {
                try {
                    // Create data input and output streams
                    DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
                    DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
                    DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
                    DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());

                    toPlayer1.writeInt(1);

                    while (true) {
                        // receive column choice
                        int column = fromPlayer1.readInt();
                        // receive dummy row
                        int row = fromPlayer1.readInt();
                        SuccessObject obj = game.addPiece(Connect4.PLAYER_X, column);
                        row = obj.getRow();
                        boolean valid = obj.isX();
                        if (!valid) {
                            toPlayer1.writeInt(INVALID);
                            break;
                        } else if (game.checkWin().isX() && game.checkWin().getPlayer() == Connect4.PLAYER_X) {
                            toPlayer1.writeInt(PLAYER1_WON);
                            sendMove(toPlayer1, row, column);
                            toPlayer2.writeInt(PLAYER1_WON);
                            sendMove(toPlayer2, row, column);
                            break;
                        } else if (isFull()) {
                            toPlayer1.writeInt(DRAW);
                            sendMove(toPlayer1, row, column);
                            toPlayer2.writeInt(DRAW);
                            sendMove(toPlayer2, row, column);
                            break;
                        } else {
                            toPlayer1.writeInt(VALID);
                            sendMove(toPlayer1, row, column);
                            toPlayer2.writeInt(CONTINUE);
                            sendMove(toPlayer2, row, column);
                        }

                        column = fromPlayer2.readInt();
                        row = fromPlayer2.readInt();
                        obj = game.addPiece(Connect4.PLAYER_O, column);
                        valid = obj.isX();
                        row = obj.getRow();
                        if (!valid) {
                            toPlayer2.writeInt(INVALID);
                            break;
                        } else if (game.checkWin().isX() && game.checkWin().getPlayer() == Connect4.PLAYER_O) {
                            toPlayer1.writeInt(PLAYER2_WON);
                            sendMove(toPlayer1, row, column);
                            toPlayer2.writeInt(PLAYER2_WON);
                            sendMove(toPlayer2, row, column);
                            break;
                        } else {
                            toPlayer2.writeInt(VALID);
                            sendMove(toPlayer2, row, column);
                            toPlayer1.writeInt(CONTINUE);
                            sendMove(toPlayer1, row, column);
                        }

                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else if (gameType == 2) {
                try {
                    toPlayer1.writeInt(1);
                    SuccessObject play = new SuccessObject();

                    while (true) {
                        // receive column choice
                        int column = fromPlayer1.readInt();
                        // receive dummy row
                        int row = fromPlayer1.readInt();
                        SuccessObject obj = game.addPiece(Connect4.PLAYER_X, column);
                        row = obj.getRow();
                        boolean valid = obj.isX();
                        if (!valid) {
                            toPlayer1.writeInt(INVALID);
                            break;
                        } else if (game.checkWin().isX() && game.checkWin().getPlayer() == Connect4.PLAYER_X) {
                            toPlayer1.writeInt(PLAYER1_WON);
                            sendMove(toPlayer1, row, column);
                            break;
                        } else if (isFull()) {
                            toPlayer1.writeInt(DRAW);
                            sendMove(toPlayer1, row, column);
                            break;
                        } else {
                            toPlayer1.writeInt(VALID);
                            sendMove(toPlayer1, row, column);
                        }

                        play = Connect4ComputerPlayer.addRandPiece(game);
                        column = play.getCol();
                        row = play.getRow();

                        if (game.checkWin().isX() && game.checkWin().getPlayer() == Connect4.PLAYER_O) {
                            toPlayer1.writeInt(PLAYER2_WON);
                            sendMove(toPlayer1, row, column);
                            break;
                        } else {
                            toPlayer1.writeInt(CONTINUE);
                            sendMove(toPlayer1, row, column);
                        }

                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

        private boolean isFull() {
            return game.boardFull();

        }

        private void sendMove(DataOutputStream out, int row, int column) throws IOException {
            out.writeInt(row);
            out.writeInt(column);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}

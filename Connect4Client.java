package core;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Connect4Client is the companion to Connect4Server It builds and updates UI's
 * and GUI's and communicates player/computer inputs to the Server
 * 
 * @author exman
 *
 */
public class Connect4Client extends Application {

    public static int PLAYER1 = 1; // Indicate player 1
    public static int PLAYER2 = 2; // Indicate player 2
    public static int PLAYER1_WON = 1; // Indicate player 1 won
    public static int PLAYER2_WON = 2; // Indicate player 2 won
    public static int DRAW = 3; // Indicate a draw
    public static int CONTINUE = 4; // Indicate to continue
    public static int INVALID = 5; // Indicate pick new column
    public static int VALID = 6; // Indicate add successful

    // scanner for text-based ui
    Scanner scan = new Scanner(System.in);

    // Indicate whether the player has the turn
    private boolean myTurn = false;

    // Indicate the token for the player
    private char myToken = ' ';

    // Indicate the token for the other player
    private char otherToken = ' ';

    // graphical array for text-based ui
    char[][] textBoard = new char[6][7];

    private int columnSelected;
    @SuppressWarnings("unused")
    private int rowSelected;

    private DataInputStream fromServer;
    private DataOutputStream toServer;

    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Host name or ip
    private String host = "localhost";

    /**
     * gameType is checked when turns are taken to see if a computer move must be
     * taken or if player input should be waited for
     */
    int gameType = 0;

    /**
     * uiType helps decide which UI is used
     */

    int uiType = 0;
    /**
     * turnCount tracks the turn and helps decide who wins
     */
    int turnCount = 0;

    private Label status = new Label();

    private Label title = new Label();

    GridPane board = new GridPane();

    /**
     * start creates the start window and the game screen
     * 
     * @param start [the first Stage that players see]
     */
    @Override
    public void start(Stage gameWindow) throws Exception {

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                textBoard[i][j] = ' ';
            }
        }

        /**
         * the gameWindow Stage that holds the bulk of the GUI
         */

        ColumnHandler cHandle = new ColumnHandler();

        for (int k = 0; k < 7; k++) {
            String buttonTitle = "";
            buttonTitle += k + 1;

            Button button = new Button(buttonTitle);
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
        board.add(title, 7, 2);

        Scene boardScene = new Scene(board, 750, 600);

        gameWindow.setTitle("Connect4");
        gameWindow.setScene(boardScene);

        // Create UI choice window
        Stage uiChoice = new Stage();
        GridPane uiPane = new GridPane();
        Scene uiScene = new Scene(uiPane, 300, 100);

        Button textBased = new Button("Text Based");
        Button GUI = new Button("GUI");
        Label prompt2 = new Label("Play with a text-based UI or a GUI?");

        uiPane.add(prompt2, 0, 0);
        uiPane.add(textBased, 0, 1);
        uiPane.add(GUI, 1, 1);

        uiChoice.setTitle("Interface Choice");
        uiChoice.setScene(uiScene);
        uiChoice.alwaysOnTopProperty();

        // handler class for UI choice buttons

        class ButtonHandler implements EventHandler<ActionEvent> {

            @Override
            public void handle(ActionEvent e) {
                Button button = (Button) e.getSource();
                if (button.getText().equals("Text Based")) {
                    uiType = 1;
                } else {
                    uiType = 2;
                    gameWindow.show();
                }
                try {
                    connectToServer(gameWindow);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                uiChoice.close();
            }

        }

        // set action for ui button choices
        ButtonHandler uiHandler = new ButtonHandler();
        textBased.setOnAction(uiHandler);
        GUI.setOnAction(uiHandler);

        uiChoice.show();
    }

    public void connectToServer(Stage gameWindow) throws IOException {
        try {

            Socket socket = new Socket(host, 8016);

            gameWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });

            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        new Thread(() -> {
            try {
                int player = fromServer.readInt();

                if (player == PLAYER1) {
                    myToken = 'X';
                    otherToken = 'O';

                    if (uiType == 1) {
                        Platform.runLater(() -> {
                            System.out.println("Player 1 with token 'X'");
                            System.out.println("Waiting for player 2 to join");
                        });
                    }
                    if (uiType == 2) {
                        Platform.runLater(() -> {
                            title.setText("Player 1 with token 'X'");
                            status.setText("Waiting for player 2 to join");
                        });
                    }

                    fromServer.readInt();

                    if (uiType == 1) {
                        Platform.runLater(
                                () -> System.out.println("Player 2 has joined. I start first"));

                    }
                    if (uiType == 2) {
                        Platform.runLater(() -> status.setText("Player 2 has joined. I start first"));
                    }

                    myTurn = true;
                } else if (player == PLAYER2) {
                    myToken = 'O';
                    otherToken = 'X';
                    if (uiType == 1) {
                        Platform.runLater(() -> {
                            System.out.println("Player 2 with token 'O'");
                            System.out.println("Waiting for player 1 to move");
                        });
                    }
                    if (uiType == 2) {
                        Platform.runLater(() -> {
                            title.setText("Player 2 with token 'O'");
                            status.setText("Waiting for player 1 to move");
                        });
                    }

                }

                while (continueToPlay) {
                    if (uiType == 1) {
                        if (player == PLAYER1) {
                            waitForPlayerInput();
                            sendMove();
                            receiveInfoFromServerText();
                            receiveInfoFromServerText();
                        } else if (player == PLAYER2) {
                            receiveInfoFromServerText();
                            waitForPlayerInput();
                            sendMove();
                            receiveInfoFromServerText();
                        }
                    }
                    if (uiType == 2) {
                        if (player == PLAYER1) {
                            waitForPlayerAction();
                            sendMove();
                            receiveInfoFromServer();
                            receiveInfoFromServer();
                        } else if (player == PLAYER2) {
                            receiveInfoFromServer();
                            waitForPlayerAction();
                            sendMove();
                            receiveInfoFromServer();
                        }
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }).start();

    }

    /**
     * text version of receiveInfoFromServer method
     * 
     * @throws IOException
     */
    private void receiveInfoFromServerText() throws IOException {

        int status = fromServer.readInt();
        if (status == VALID) {
            receiveMove(myToken);
            myTurn = false;
        }

        else if (status == PLAYER1_WON) {
            continueToPlay = false;
            if (myToken == 'X') {
                System.out.println("I won! (X)");
                receiveMove(myToken);
            } else if (myToken == 'O') {
                System.out.println("Player 1 (X) has won!");
                receiveMove(otherToken);
            }
        } else if (status == PLAYER2_WON) {
            continueToPlay = false;
            if (myToken == 'O') {
                System.out.println("I won! (O)");
                receiveMove(myToken);
            } else if (myToken == 'X') {
                System.out.println("Player 2 (O) has won!");
                receiveMove(otherToken);
            }
        } else if (status == DRAW) {
            continueToPlay = false;
            System.out.println("Game is over, no winner!");
            if (myToken == 'O') {
                receiveMove(otherToken);
            }
        } else if (status == INVALID) {
            System.out.println("Column full. My turn. Input a column 1-7:");
            myTurn = true; // It is my turn
        } else {
            receiveMove(otherToken);
            System.out.println("My turn. Input a column 1-7:");
            myTurn = true; // It is my turn
        }

    }

    private void waitForPlayerInput() {
        System.out.println("Input a column 1-7:");
        columnSelected = scan.nextInt() - 1;
    }

    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }
        waiting = true;
    }

    private void sendMove() throws IOException {
        toServer.writeInt(columnSelected); // Send the selected column
        toServer.writeInt(-1);
    }

    private void receiveInfoFromServer() throws IOException {

        int status = fromServer.readInt();
        if (status == VALID) {
            receiveMove(myToken);
            myTurn = false;
        }

        else if (status == PLAYER1_WON) {

            continueToPlay = false;
            if (myToken == 'X') {
                Platform.runLater(() -> this.status.setText("I won! (X)"));
                receiveMove(myToken);
            } else if (myToken == 'O') {
                Platform.runLater(() -> this.status.setText("Player 1 (X) has won!"));
                receiveMove(otherToken);
            }
        } else if (status == PLAYER2_WON) {
            continueToPlay = false;
            if (myToken == 'O') {
                Platform.runLater(() -> this.status.setText("I won! (O)"));
                receiveMove(myToken);
            } else if (myToken == 'X') {
                Platform.runLater(() -> this.status.setText("Player 2 (O) has won!"));
                receiveMove(otherToken);
            }
        } else if (status == DRAW) {
            continueToPlay = false;
            Platform.runLater(() -> this.status.setText("Game is over, no winner!"));
            if (myToken == 'O') {
                receiveMove(otherToken);
            }
        } else if (status == INVALID) {
            Platform.runLater(() -> this.status.setText("Column full. My turn"));
            myTurn = true; // It is my turn
        } else {
            receiveMove(otherToken);
            Platform.runLater(() -> this.status.setText("My turn"));
            myTurn = true; // It is my turn
        }

    }

    private void receiveMove(char token) throws IOException {
        int row = fromServer.readInt();
        int column = fromServer.readInt();

        if (uiType == 2) {
            updateBoard(token, row, column);
        }
        if (uiType == 1) {
            updateTextBoard(token, row, column);
        }

    }

    private void updateTextBoard(char token, int row, int column) {
        
        for(int i = 0; i<30; i++) {
            System.out.println();
        }
        textBoard[row][column] = token;
        String board = "";
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board += ("|" + textBoard[i][j]);
            }
            board += ("|\n");
        }
        System.out.println(board);
    }

    /**
     * method that updates the GUI to display the current board layout
     * 
     * @param column
     */
    private void updateBoard(char hold, int row, int column) {
        Circle circle = new Circle();
        circle.setRadius(20);
        circle.setStroke(Color.BLACK);
        Paint color = null;
        if (hold == 'O') {
            color = Paint.valueOf("yellow");
        } else if (hold == 'X') {
            color = Paint.valueOf("red");
        }
        circle.setFill(color);
        Platform.runLater(() -> {
            board.add(circle, column, row + 1);
        });
        
    }

    /**
     * Event handler for all of the column buttons. Calls the addPiece() method for
     * the game object Calls the updateBoard() method if addPiece() is successful
     * calls the checkWin() method to see if the game has been won prints a readout
     * if a column is full
     * 
     * @author exman
     *
     */

    class ColumnHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            Button button = (Button) e.getSource();
            int col = Integer.parseInt(button.getText()) - 1;
            if (myTurn) {
                columnSelected = col;
                myTurn = false;
                status.setText("Waiting for the other player to move");
                waiting = false;
            }

        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}

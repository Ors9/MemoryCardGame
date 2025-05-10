import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javafx.scene.control.Label;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JOptionPane;

/**
 * CardGameController handles the JavaFX client-side logic and UI for the memory
 * matching game.
 *
 * This class is connected to an FXML file containing the grid and turn label.
 * It manages rendering the board, sending user input to the server, and
 * reacting to game state updates from the server.
 *
 */
public class CardGameController implements Runnable {

	/** The GridPane that holds the buttons representing cards */
	@FXML
	private GridPane grid;

	/** Label that displays whose turn it is */
	@FXML
	private Label labelTurn;

	private final int SIZE = 4; // The board is 4x4
	private int[] imageId; // The shuffled image ID array (each value appears twice)
	private boolean[] revealed; // Indicates which cards are currently face-up
	private ObjectOutputStream out; // Stream to send messages to server
	private ObjectInputStream in; // Stream to receive messages from server
	private boolean myTurn = false; // Whether it is this player's turn
	private boolean gameOver = false; // Whether the game has ended
	private Button[] buttons; // The array of buttons displayed on the grid

	/**
	 * Initializes the game board with the starting state from the server. Called
	 * once when the client connects to the server.
	 *
	 * @param initialState Initial game state from the server
	 * @param out          Output stream to server
	 * @param in           Input stream from server
	 */
	public void setNetworkData(BoardState initialState, ObjectOutputStream out, ObjectInputStream in) {
		this.imageId = initialState.imageId;
		this.revealed = initialState.revealed;
		this.out = out;
		this.in = in;
		this.myTurn = initialState.yourTurn;

		buttons = new Button[SIZE * SIZE];

		// Setup the GUI on the JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setupBoard();
			}
		});

		// Start listening for updates from the server
		new Thread(this).start();
	}

	/**
	 * Creates and configures the game board buttons and places them on the grid.
	 */
	private void setupBoard() {
		grid.getChildren().clear();
		for (int i = 0; i < SIZE * SIZE; i++) {
			int index = i;
			Button btn = new Button();
			btn.setMinSize(10, 10);
			btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					handleClick(index);
				}
			});
			buttons[i] = btn;
			grid.add(btn, index % SIZE, index / SIZE);
			GridPane.setHgrow(btn, Priority.ALWAYS);
			GridPane.setVgrow(btn, Priority.ALWAYS);

		}
		updateBoard();
	}

	/**
	 * Handles a card click event. Sends the index to the server if it's the
	 * player's turn.
	 *
	 * @param index The index of the clicked button
	 */
	private void handleClick(int index) {

		if (myTurn && !revealed[index]) {
			try {
				out.writeObject(index);
				out.flush();
				myTurn = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates the board display by showing images for revealed cards. Hides
	 * unrevealed cards.
	 */
	private void updateBoard() {
		for (int i = 0; i < buttons.length; i++) {
			Button btn = buttons[i];
			if (revealed[i]) {
				int imgId = imageId[i];
				Image img = new Image(getClass().getResource("/dog" + (imgId + 1) + ".png").toExternalForm());

				// Skip updating if the current image is already shown
				if (btn.getGraphic() instanceof ImageView existing
						&& existing.getImage().getUrl().equals(img.getUrl())) {
					continue;
				}

				ImageView iv = new ImageView(img);
				iv.setPreserveRatio(false);
				iv.setSmooth(true);

				iv.fitWidthProperty().bind(btn.widthProperty());
				iv.fitHeightProperty().bind(btn.heightProperty());

				btn.setGraphic(iv);
			} else {
				btn.setGraphic(null);
			}
		}

	}

	/**
	 * Displays a dialog box announcing the end of the game.
	 *
	 * @param msg Message from the server (e.g., "Player 1 Wins")
	 */
	private void showGameOver(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Listens for updates from the server and updates the board accordingly. This
	 * runs on a separate thread.
	 */
	@Override
	public void run() {
		try {
			while (!gameOver) {
				Object obj = in.readObject();
				if (obj instanceof BoardState) {
					BoardState state = (BoardState) obj;
					this.imageId = state.imageId;
					this.revealed = state.revealed;
					this.myTurn = state.yourTurn;

					Platform.runLater(new Runnable() {
						public void run() {
							updateBoard();
							if (myTurn) {
								labelTurn.setText("🔵 תורך!");
							} else {
								labelTurn.setText("🕒 המתן ליריב...");
							}
						}
					});

					if (state.gameOver) {
						gameOver = true;
						showGameOver(state.message);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

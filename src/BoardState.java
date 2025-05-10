import java.io.Serializable;
import java.util.Arrays;

import javax.swing.JOptionPane;

/**
 * BoardState represents the complete state of the memory game board at a given
 * moment. This class is serializable and is sent between server and clients to
 * synchronize the game.
 *
 * Fields include card data, which player's turn it is, whether the game is
 * over, a message string for display, and the score of both players.
 *
 */
public class BoardState implements Serializable {
	private static final long serialVersionUID = 1L;

	// An array of image identifiers (0-7, with each appearing twice) representing
	// the game board
	public int[] imageId;

	// Boolean array indicating which cards are currently revealed
	public boolean[] revealed;

	// True if it's the current client's turn
	public boolean yourTurn;

	// True if the game has ended
	public boolean gameOver;

	// Message from the server to the client (e.g., "Your turn", "You won", etc.)
	public String message;

	// Score of player 1 , player 2
	public int score1;
	public int score2;

	/**
	 * Default constructor. Used when the server builds a new BoardState to send to
	 * clients.
	 */
	public BoardState() {
		// Empty constructor, fields will be set manually.
	}

	/**
	 * Full constructor to initialize the board state.
	 *
	 * @param imageId  Array of image IDs
	 * @param revealed Array indicating which cards are shown
	 * @param yourTurn Whether it is this player's turn
	 * @param gameOver Whether the game has ended
	 * @param message  Server message to display
	 */
	public BoardState(int[] imageId, boolean[] revealed, boolean yourTurn, boolean gameOver, String message) {
		this.imageId = Arrays.copyOf(imageId, imageId.length);
		this.revealed = Arrays.copyOf(revealed, revealed.length);
		this.yourTurn = yourTurn;
		this.gameOver = gameOver;
		this.message = message;
	}

	/**
	 * Static helper method that prompts the user for a server IP. If the user
	 * cancels or enters nothing, returns the provided default.
	 *
	 * @param defaultIp The IP to return if user input is empty
	 * @return IP string
	 */
	public static String getIpFromUser(String defaultIp) {
		String input = JOptionPane.showInputDialog("Enter server IP (cancel for default: \"" + defaultIp + "\")");
		if (input == null || input.isEmpty()) {
			return defaultIp;
		}
		return input;
	}

	/**
	 * Static helper method that prompts the user for a port number. If invalid or
	 * empty input is provided, returns the default port.
	 *
	 * @param defaultPort The port to return if input is invalid
	 * @return Valid port number
	 */
	public static int getPortFromUser(int defaultPort) {
		String input = JOptionPane.showInputDialog("Enter port (cancel for default: " + defaultPort + ")");
		if (input != null && !input.isEmpty()) {
			try {
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid port. Using default " + defaultPort + ".");
			}
		}
		return defaultPort;
	}
}

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

/**
 * CardGameWithTwoClients handles the game logic for a two-player memory card
 * game. Each instance manages the full game session between two connected
 * clients using sockets and object streams.
 *
 * Key Responsibilities: - Create and shuffle a 4x4 board with 8 pairs (16 cards
 * total) - Manage turns and card reveals - Track and update player scores -
 * Send updated game state to both players using serialized BoardState objects -
 * Determine the game winner and notify both clients
 */
public class CardGameWithTwoClients extends Thread {
	public static final int NUM_PAIRS = 8; // Total number of unique pairs

	// Client connections
	private Socket p1, p2;
	private ObjectOutputStream out1, out2;
	private ObjectInputStream in1, in2;

	private int playerTurn;// 1 or 2: which player's turn it is

	private ArrayList<Integer> imageId; // Shuffled image identifiers (each pair appears twice)
	private boolean[] revealed; // Tracks which cards are currently revealed

	private int sumPlayer1, sumPlayer2; // Player scores

	private int tempFirstIndex = -1; // Temporary storage for selected card indices
	private int tempSecondIndex = -1;

	/**
	 * Constructor initializes sockets, player turn, scores, and shuffled board.
	 */
	public CardGameWithTwoClients(Socket p1, Socket p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.playerTurn = 1;
		this.sumPlayer1 = 0;
		this.sumPlayer2 = 0;
		this.revealed = new boolean[NUM_PAIRS * 2];
		this.imageId = buildShuffledBoard();
	}

	/**
	 * Main game loop. Exchanges data with both players and controls turn logic.
	 */
	@Override
	public void run() {
		try {
			// Set up input/output streams
			out1 = new ObjectOutputStream(p1.getOutputStream());
			in1 = new ObjectInputStream(p1.getInputStream());
			out2 = new ObjectOutputStream(p2.getOutputStream());
			in2 = new ObjectInputStream(p2.getInputStream());

			// Send initial state to both players
			sendUpdatedBoards("Game Start");

			while (!isGameFinished()) {
				// Select which streams to use based on turn
				ObjectOutputStream currentOut = (playerTurn == 1) ? out1 : out2;
				ObjectInputStream currentIn = (playerTurn == 1) ? in1 : in2;

				// Notify current player it's their turn
				currentOut.writeObject(buildBoardState(true, "Your turn"));
				currentOut.flush();

				// Handle first card click
				tempFirstIndex = getValidIndex(currentIn);
				revealed[tempFirstIndex] = true;
				sendUpdatedBoards("First card chosen");

				// Handle second card click
				tempSecondIndex = getValidIndex(currentIn);
				revealed[tempSecondIndex] = true;
				sendUpdatedBoards("Second card chosen");

				// Check for match
				boolean match = imageId.get(tempFirstIndex).equals(imageId.get(tempSecondIndex));
				if (match) {
					if (playerTurn == 1)
						sumPlayer1++;
					else
						sumPlayer2++;
				} else {
					// Wait before hiding if not a match
					Thread.sleep(3000);
					revealed[tempFirstIndex] = false;
					revealed[tempSecondIndex] = false;
					playerTurn = (playerTurn == 1) ? 2 : 1; // Switch turns
				}

				sendUpdatedBoards("Turn resolved");
			}

			// Game ended — decide and send winner message
			String winner = (sumPlayer1 > sumPlayer2) ? "Player 1 Wins"
					: (sumPlayer2 > sumPlayer1) ? "Player 2 Wins" : "Draw";

			sendGameOver(winner);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Close all connections
			try {
				p1.close();
				in1.close();
				out1.close();
			} catch (Exception ignored) {
			}
			try {
				p2.close();
				in2.close();
				out2.close();
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Creates a shuffled board containing 2 of each card from 0 to 7.
	 */
	private ArrayList<Integer> buildShuffledBoard() {
		ArrayList<Integer> ids = new ArrayList<>();
		for (int i = 0; i < NUM_PAIRS; i++) {
			ids.add(i);
			ids.add(i);
		}
		Collections.shuffle(ids);
		return ids;
	}

	/**
	 * Checks if all cards have been matched.
	 */
	private boolean isGameFinished() {
		for (boolean b : revealed) {
			if (!b)
				return false;
		}
		return true;
	}

	/**
	 * Reads and validates a card index sent from a client. Ignores invalid or
	 * already revealed indices.
	 */
	private int getValidIndex(ObjectInputStream in) throws IOException {
		while (true) {
			try {
				Object obj = in.readObject();
				if (obj instanceof Integer index && !revealed[index]) {
					return index;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends current game state to both clients, marking whose turn it is.
	 */
	private void sendUpdatedBoards(String message) throws IOException {
		BoardState state1 = buildBoardState(playerTurn == 1, message);
		BoardState state2 = buildBoardState(playerTurn == 2, message);
		out1.writeObject(state1);
		out2.writeObject(state2);
		out1.flush();
		out2.flush();
	}

	/**
	 * Sends final state with a winning message and sets gameOver flag.
	 */
	private void sendGameOver(String winnerMessage) throws IOException {
		BoardState end1 = buildBoardState(false, winnerMessage);
		BoardState end2 = buildBoardState(false, winnerMessage);
		end1.gameOver = true;
		end2.gameOver = true;
		out1.writeObject(end1);
		out2.writeObject(end2);
		out1.flush();
		out2.flush();
	}

	/**
	 * Helper to construct a BoardState object with current game data.
	 */
	private BoardState buildBoardState(boolean isYourTurn, String message) {
		BoardState state = new BoardState();
		int[] imageArray = new int[imageId.size()];
		for (int i = 0; i < imageId.size(); i++) {
			imageArray[i] = imageId.get(i);
		}
		state.imageId = imageArray;
		state.revealed = revealed.clone();
		state.yourTurn = isYourTurn;
		state.gameOver = false;
		state.message = message;
		state.score1 = sumPlayer1;
		state.score2 = sumPlayer2;
		return state;
	}
}

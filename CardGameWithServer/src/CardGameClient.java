import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * CardGameClient is the entry point for the client-side JavaFX application of
 * the Memory Game. It connects to the server via TCP sockets, receives the
 * initial board state, and launches the GUI defined in FXML (CardGame.fxml).
 *
 * Responsibilities: - Ask user for IP and port and connect to the server -
 * Receive the initial BoardState object from the server - Load the game UI and
 * pass the connection data to the controller - Set the window title based on
 * player turn
 *
 */
public class CardGameClient extends Application {
	// Socket connection to the server
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	/**
	 * JavaFX entry point. Starts a background thread to connect to the server.
	 */
	@Override
	public void start(Stage primaryStage) {
		// Run network logic in a background thread to avoid blocking JavaFX
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				connectToServer();
			}
		});
		thread.setDaemon(true); // Allows app to exit when main window closes
		thread.start();
	}

	/**
	 * Establishes a connection to the game server and launches the game UI. Reads
	 * an initial BoardState object from the server to determine player turn.
	 */
	private void connectToServer() {
		// Ask the user for IP and port (defaults provided)
		String ip = BoardState.getIpFromUser("localhost");
		int port = BoardState.getPortFromUser(7777);

		try {
			// Establish socket connection and create input/output streams
			socket = new Socket(ip, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			System.out.println("✅ Connected to server");

			// Wait for initial BoardState from the server
			Object msg = in.readObject();
			if (msg instanceof BoardState) {
				final BoardState initialState = (BoardState) msg;

				// Launch the JavaFX GUI on the JavaFX thread
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							// Load UI from FXML file
							FXMLLoader loader = new FXMLLoader(getClass().getResource("CardGame.fxml"));
							Parent root = loader.load();

							// Pass server state and streams to the controller
							CardGameController controller = loader.getController();
							controller.setNetworkData(initialState, out, in);

							// Open a new stage (window) and set its title
							Stage stage = new Stage();
							String title = "🃏 משחק זיכרון";
							if (initialState.yourTurn) {
								title += " - 🔵 שחקן 1 (תורך)";
							} else {
								title += " - 🟢 שחקן 2";
							}
							stage.setTitle(title);
							stage.setScene(new Scene(root));
							stage.show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "❌ Failed to connect: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}

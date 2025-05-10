import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * CardGamesServer
 * 
 * This server waits for two players to connect via sockets and then starts a
 * new thread to run a memory card game session between them.
 */
public class CardGamesServer {
	public static void main(String[] args) {

		// Prompt the user to choose a port number (default is 7777)
		int port = BoardState.getPortFromUser(7777);

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("🎮 Server started on port " + port);

			// Keep accepting players and starting new games
			while (true) {
				System.out.println("⌛ Waiting for players...");

				// Accept first player
				Socket p1 = serverSocket.accept();
				System.out.println(
						"🟢 Player 1 connected from " + p1.getInetAddress().getHostAddress() + ":" + p1.getPort());

				// Accept second player
				Socket p2 = serverSocket.accept();
				System.out.println(
						"🟢 Player 2 connected from " + p2.getInetAddress().getHostAddress() + ":" + p2.getPort());

				// Start a new game session in a separate thread
				new CardGameWithTwoClients(p1, p2).start();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "❌ Server error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}

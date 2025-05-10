# 🧠 Multiplayer Memory Game

A memory card game for two players connected over a network. Each player gets an identical board and tries to reveal matching pairs. 
The game uses JavaFX for the graphical interface and Java Sockets for communication between the server and clients.

---

## 🚀 Key Features

- Two-player game (Client-Server architecture).
- Beautiful and intuitive JavaFX GUI.
- Image matching on board buttons (`dog1.png`, `dog2.png`, etc.).
- Turn management, scoring, and end-of-game handling.
- Prevents more than two cards from being flipped at once.

---

## 🛠️ Requirements

- Java 17+ (or any version supporting JavaFX 23.0.2)
- JavaFX SDK installed
- Eclipse or any JavaFX-compatible IDE
- Image files (`dog1.png` ... `dog8.png`) placed inside the `src` folder

---

## 📁 Project Structure
- MemoryGame/
  - src/
    - CardGameServer.java  
    - CardGameClient.java  
    - CardGameController.java  
    - CardGameWithTwoClients.java  
    - BoardState.java  
    - CardGame.fxml  
    - dog1.png ... dog8.png

---

## ▶️ How to Run

### 1. Run the Server
- Launch `CardGameServer.java`
- Listens on port `7777` by default

### 2. Run Two Clients
- Launch `CardGameClient.java` **twice**
- Each client enters IP and port (e.g., `localhost`, `7777`)

### 3. Start Playing
- Both players receive the same board.
- Players take turns trying to find matching pairs.

---

## 📝 Notes

- The image files **must** be placed in the `src` folder, not `resources`.
- This game is designed for exactly two players.
- No installation required – just run it from your IDE.




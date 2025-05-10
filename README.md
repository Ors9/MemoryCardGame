# 🧠 Multiplayer Memory Game

A memory card game for **two players over a network**. Each player gets an identical board and takes turns trying to find matching pairs. The game is built with **JavaFX** for the GUI and uses **Java Sockets** for server-client communication.

---

## 🚀 Features

- 🔗 Two-player multiplayer (Client-Server architecture)
- 🎨 Responsive and intuitive UI with JavaFX
- 🖼️ Image-based cards (dog1.png to dog8.png)
- 🔄 Turn-based gameplay and score tracking
- 🛡️ Prevents flipping more than two cards per turn
- 🏁 Game ends when all pairs are matched

---

## 🛠️ Requirements

- Java 17+  
- JavaFX SDK (tested with JavaFX 23.0.2)  
- IDE like Eclipse or IntelliJ with JavaFX support  
- Place `dog1.png` to `dog8.png` directly in the `src` folder (not in resources)

---

## 📁 Project Structure

MemoryCardGame/
├── src/
│   ├── CardGameClient.java  
│   ├── CardGameController.java  
│   ├── CardGameServer.java  
│   ├── CardGameWithTwoClients.java  
│   ├── BoardState.java  
│   ├── CardGame.fxml  
│   ├── dog1.png  
│   ├── dog2.png  
│   ├── dog3.png  
│   ├── dog4.png  
│   ├── dog5.png  
│   ├── dog6.png  
│   ├── dog7.png  
│   └── dog8.png  
├── README.md  
└── משחק הזיכרון.pdf (optional)


---

## ▶️ How to Run

### 🖥️ 1. Start the Server
- Run `CardGameServer.java`
- Default port is `7777`

### 👥 2. Run the Clients
- Run `CardGameClient.java` **twice** (on the same machine or different ones)
- Enter the **IP** and **port** when prompted (`localhost`, `7777` by default)

### 🕹️ 3. Play the Game
- Each player clicks to reveal 2 cards.
- If the cards match, the player gets a point and another turn.
- If not, cards are hidden again and turn switches.

---

## 📝 Notes

- The `.png` files must be in the same directory as the `.java` files (`src/`)
- Works in local network or remote if firewall allows access to the port.
- Designed as a simple academic multiplayer Java project.

---

## 👤 Author

Developed by **Or Saban**


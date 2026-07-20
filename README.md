# 👑 Royal Checkers


**Royal Checkers** is a premium, multi-mode draughts (checkers) game built entirely in Java. It offers a rich single-player experience against a smart AI, a local two-player mode, and an extensible architecture ready for future online multiplayer. With a polished Swing-based UI, immersive audio, and complete rule enforcement, Royal Checkers brings the classic board game to life with elegance and depth.

---

## ✨ Key Features

- ** Three Game Modes** – VS Computer (AI), Local 2-Player, and an upcoming Online mode.
- ** Intelligent AI** – Minimax search with Alpha-Beta pruning across 3 difficulty levels (Easy/Medium/Hard).
- ** Dual Visual Themes** – Switch between *Classic* (warm wood tones) and *Modern* (sleek silver/blue) styles.
- ** Immersive Audio** – Background music for menu and gameplay with independent mute controls.
- ** Save & Load** – Serialize your session to a `.dat` file and resume anytime.
- ** Player Profiles** – Track names, scores, games played, and wins.
- ** Move History** – Full replayable history of every move made.

---

## 📂 Project Structure & Class Breakdown

The project is split into two primary packages, with additional packages reserved for future expansion.

### `model` Package – Core Logic & Data

This package contains the entire backend engine of the game, independent of any user interface.

| Class | Description |
| :--- | :--- |
| **`Piece`** | Represents a single game piece. Stores its `Color` (BLACK/RED), `Type` (NORMAL/KING), and current position. Provides logic for forward movement direction based on color. |
| **`Move`** | Encapsulates a move with origin (`fromRow`, `fromCol`), destination (`toRow`, `toCol`), and `MoveType` (NORMAL or CAPTURE). Calculates the exact coordinates of captured pieces when applicable. |
| **`Board`** | Manages the 8×8 grid of pieces. Handles piece placement, removal, copying (for AI simulation), and standard game initialization. Validates dark square logic and position bounds. |
| **`GameLogic`** | The **rule engine**. Generates all valid moves (enforcing mandatory captures), detects multi-jump (chain capture) opportunities, applies moves to create new game states, checks for promotions, and evaluates game-over conditions (win by capture or no legal moves). |
| **`GameState`** | A complete immutable snapshot of the game. Encapsulates the current `Board`, whose turn it is, the `GameStatus` (PLAYING, RED_WINS, BLACK_WINS, DRAW), move history, scores, game mode, and player references. Used extensively for save/load and AI search. |
| **`Player`** | Stores user profile data: unique UUID, display name, current score, total games played, and games won. |
| **`CheckersAI`** | Implements the AI opponent. Uses the **Minimax algorithm with Alpha-Beta pruning** and a heuristic evaluation function (material value + positional bonuses). Supports three `Difficulty` levels: `EASY` (depth 2, random), `MEDIUM` (depth 5), and `HARD` (depth 8). |

### `Graphic` Package – User Interface & Presentation

This package contains the Swing-based graphical components that bring the game to life.

| Class | Description |
| :--- | :--- |
| **`Theme`** | A simple enumeration defining the two available visual color palettes: `CLASSIC` (cream and dark brown) and `MODERN` (silver and dark blue). |
| **`MusicManager`** | Handles background audio playback. Loads `.wav` or `.mp3` files (menu and in-game tracks). Supports seamless looping, start/stop, and independent mute toggles for both audio contexts. |
| **`GameBoard`** | A custom `JPanel` that renders the checkerboard and pieces. Handles mouse-click events for piece selection and movement. Orchestrates the game flow (player vs. AI, local 2-player), manages chain-capture sequences, and displays visual feedback for selected pieces and active turns. |
| **`MainMenu`** | The main launcher and navigation hub (`JFrame`). Manages transitions between the splash screen, main menu, rules page, settings panel (theme, difficulty, sound), and the active game session. Implements the save/load feature by serializing `GameState` to `savegame.dat`. |

---

## 🌐 Online Multiplayer (Status: *Suspended / In Development*)

The architecture of Royal Checkers was designed with online multiplayer in mind. The project includes dedicated packages—**`server`**, **`client`**, and **`common`**—intended to facilitate a client-server model for remote play. The `common` package would have contained shared network protocols and data transfer objects, while `server` would manage matchmaking and game state synchronization, and `client` would handle the remote connection and UI updates.

**However**, due to ongoing technical complexities and optimization requirements, the development of the online multiplayer feature has been **temporarily suspended**. We are committed to delivering a seamless and stable online experience, and work on this feature will resume in a future phase of the project. The current release focuses on the fully polished single-player (VS AI) and local 2-player modes.

---

## 🎵 Audio & Save Files

To ensure full functionality, please place the following resource files in the project's root directory or classpath:

| File Name | Purpose |
| :--- | :--- |
| **`menu_music.wav`** / **`menu_music.mp3`** | Looping background track played on the main menu. |
| **`game_music.wav`** / **`game_music.mp3`** | Looping background track played during an active game session. |
| **`savegame.dat`** | The default serialized file generated when saving a session. Loads automatically when using the "Load Game" feature. |

*Note: If audio files are missing, the game will run without sound and log an error to the console.*

---

## 🚀 Installation & Execution

### Prerequisites
- **Java 17** or higher ([Download here](https://adoptium.net/))

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/royal-checkers.git
   cd royal-checkers
   ```

2. **Compile the source code:**
   ```bash
   javac -d out $(find . -name "*.java")
   ```

3. **Run the application:**
   ```bash
   java -cp out Graphic.MainMenu
   ```

*(Alternatively, import the project into your preferred IDE—such as IntelliJ IDEA, Eclipse, or VS Code—and run `MainMenu.java` directly.)*

---

## 🎯 How to Play

- **Objective:** Capture all of your opponent's pieces or block them from making any legal move.
- **Setup:** BLACK (bottom rows 5–7) moves first. RED (top rows 0–2) follows.
- **Movement:** Pieces move diagonally forward to an empty dark square.
- **Captures:** Jump over an opponent's piece diagonally into an empty square behind it. **Captures are mandatory.**
- **Chain Captures:** If a piece lands and can capture again, it must continue (multi-jump).
- **King Promotion:** When a piece reaches the opposite end row, it becomes a King and gains the ability to move and capture both forward and backward.
- **Controls:** Click your piece to select it, then click the highlighted destination square to execute the move.

---

## 🤖 AI Complexity

The computer opponent uses a **Minimax algorithm with Alpha-Beta pruning** to search the game tree effectively.

- **Evaluation Heuristic:** Balances material advantage (men = 100, kings = 175), central control, and advancement toward promotion (kings row bonus).
- **Terminal Scoring:** Rewards faster wins by adding remaining depth to the win score (`WIN_SCORE + depth`).

| Difficulty | Search Depth | Strategy |
| :--- | :--- | :--- |
| **Easy** | 2 | Prefers captures but introduces randomness for a beginner challenge. |
| **Medium** | 5 | Standard minimax search – balanced for casual players. |
| **Hard** | 8 | Deep tactical search – suited for experienced players. |

---

## 🛠️ Technologies Used

- **Java 17** – Core language and runtime.
- **Swing (javax.swing)** – GUI framework for the user interface.
- **Java Sound API (javax.sound)** – Audio playback and management.
- **Serialization (java.io)** – Save/Load functionality.

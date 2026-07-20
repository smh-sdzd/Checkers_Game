package Graphic;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class GameBoard extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 65;

    private GameState gameState;
    private Board board;
    private String mode;                  // Game mode: "computer", "online", "local"
    private Theme currentTheme;           // Visual theme
    private CheckersAI ai;                // AI opponent for computer mode
    private CheckersAI.Difficulty difficulty;

    private int selectedRow = -1, selectedCol = -1;  // Currently selected piece
    private boolean chainMove = false;                 // Chain capture flag

    public GameBoard(String mode, CheckersAI.Difficulty difficulty) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.currentTheme = MainMenu.currentTheme;
        this.gameState = new GameState();
        this.gameState.setGameMode(mode);
        this.board = gameState.getBoard();

        // Initialize AI for computer mode (AI always plays RED)
        if (mode.equals("computer")) {
            this.ai = new CheckersAI(Piece.Color.RED, difficulty);
        }

        setPreferredSize(new Dimension(BOARD_SIZE * SQUARE_SIZE, BOARD_SIZE * SQUARE_SIZE));
        setBackground(new Color(0x2B0F0F));
        setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 4));

        // Mouse listener for board interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;
                if (row < BOARD_SIZE && col < BOARD_SIZE) {
                    handleClick(row, col);
                }
            }
        });
    }

    // Public setters for dynamic configuration
    public void setDifficulty(CheckersAI.Difficulty difficulty) {
        this.difficulty = difficulty;
        if (mode.equals("computer")) {
            this.ai = new CheckersAI(Piece.Color.RED, difficulty);
        }
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        repaint();
    }

    public GameState getGameState() { return gameState; }

    public void setGameState(GameState state) {
        this.gameState = state;
        this.board = state.getBoard();
        this.mode = state.getGameMode();
        this.selectedRow = -1;
        this.selectedCol = -1;
        this.chainMove = false;

        if (mode.equals("computer")) {
            this.ai = new CheckersAI(Piece.Color.RED, difficulty);
        }

        repaint();
        revalidate();
    }

    public void stopGame() {}

    /**
     * Handles mouse click on the board.
     * Validates and executes moves based on game rules.
     */
    private void handleClick(int row, int col) {
        if (gameState.isGameOver()) {
            JOptionPane.showMessageDialog(this, "Game Over!");
            return;
        }

        Piece.Color currentTurn = gameState.getCurrentTurn();

        // In computer mode, only BLACK (player) can click
        if (mode.equals("computer") && currentTurn != Piece.Color.BLACK) {
            return;
        }

        // In online mode, only local player can click
        if (mode.equals("online") && !gameState.isLocalPlayerTurn()) {
            return;
        }

        Piece clicked = board.getPieceAt(row, col);

        // No piece selected yet - try to select one
        if (selectedRow == -1 && selectedCol == -1) {
            // Only select pieces of the current player's color
            if (clicked != null && clicked.getColor() == currentTurn) {
                List<Move> moves = GameLogic.getValidMovesForPiece(board, row, col);
                if (!moves.isEmpty()) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
            }
            return;
        }

        // Piece already selected - attempt to make a move
        int fromRow = selectedRow, fromCol = selectedCol;
        selectedRow = -1;
        selectedCol = -1;

        List<Move> validMoves = GameLogic.getValidMovesForPiece(board, fromRow, fromCol);
        List<Move> allPlayerMoves = GameLogic.getAllValidMovesForPlayer(board, currentTurn);

        // Find if clicked position is a valid move
        Move targetMove = null;
        for (Move m : validMoves) {
            if (m.getToRow() == row && m.getToCol() == col) {
                for (Move pm : allPlayerMoves) {
                    if (pm.getFromRow() == m.getFromRow() && pm.getFromCol() == m.getFromCol() &&
                            pm.getToRow() == m.getToRow() && pm.getToCol() == m.getToCol()) {
                        targetMove = m;
                        break;
                    }
                }
                if (targetMove != null) break;
            }
        }

        if (targetMove != null) {
            // Execute the move
            gameState = GameLogic.applyMove(gameState, targetMove);
            board = gameState.getBoard();
            repaint();

            // Check for chain capture
            boolean canCaptureAgain = GameLogic.hasAnyCaptureMoveForPiece(board, targetMove.getToRow(), targetMove.getToCol());
            if (canCaptureAgain && targetMove.isCapture()) {
                chainMove = true;
                selectedRow = targetMove.getToRow();
                selectedCol = targetMove.getToCol();
            } else {
                chainMove = false;
                selectedRow = -1;
                selectedCol = -1;
            }

            // Check for game over
            if (gameState.isGameOver()) {
                String winner = gameState.getWinner() == Piece.Color.BLACK ? "Black" : "Red";
                JOptionPane.showMessageDialog(this, winner + " wins!");
                return;
            }

            // Trigger AI move in computer mode
            if (mode.equals("computer") && gameState.getCurrentTurn() == Piece.Color.RED) {
                Timer timer = new Timer(300, e -> makeComputerMove());
                timer.setRepeats(false);
                timer.start();
            }
        } else {
            // Invalid move - try to select a new piece
            if (clicked != null && clicked.getColor() == currentTurn) {
                List<Move> moves = GameLogic.getValidMovesForPiece(board, row, col);
                if (!moves.isEmpty()) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
            }
        }
    }

    /**
     * Executes a move for the AI in computer mode.
     * Uses CheckersAI to select the best move.
     */
    private void makeComputerMove() {
        if (gameState.isGameOver() || gameState.getCurrentTurn() != Piece.Color.RED) return;

        Move move = ai.chooseMove(gameState);
        if (move == null) {
            if (gameState.isGameOver()) {
                String winner = gameState.getWinner() == Piece.Color.BLACK ? "Black" : "Red";
                JOptionPane.showMessageDialog(this, winner + " wins!");
            }
            return;
        }

        gameState = GameLogic.applyMove(gameState, move);
        board = gameState.getBoard();

        // Chain capture for AI
        boolean canCaptureAgain = GameLogic.hasAnyCaptureMoveForPiece(board, move.getToRow(), move.getToCol());
        if (canCaptureAgain && move.isCapture()) {
            Timer timer = new Timer(300, e -> makeComputerMove());
            timer.setRepeats(false);
            timer.start();
        }

        repaint();

        if (gameState.isGameOver()) {
            String winner = gameState.getWinner() == Piece.Color.BLACK ? "Black" : "Red";
            JOptionPane.showMessageDialog(this, winner + " wins!");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Color selection based on theme
        Color light, dark;
        if (currentTheme == Theme.CLASSIC) {
            light = new Color(0xF5E6D3);  // Cream
            dark = new Color(0x3B2B1A);   // Dark brown
        } else {
            light = new Color(0xA0A0A0);  // Silver
            dark = new Color(0x1A2A4A);   // Dark blue
        }

        // Draw the board
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int x = c * SQUARE_SIZE, y = r * SQUARE_SIZE;
                g2d.setColor((r + c) % 2 == 0 ? light : dark);
                g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                g2d.setColor(new Color(0xD4AF37)); // Gold border
                g2d.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // Draw pieces
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p == null) continue;

                int cx = c * SQUARE_SIZE + SQUARE_SIZE / 2;
                int cy = r * SQUARE_SIZE + SQUARE_SIZE / 2;
                int rad = SQUARE_SIZE / 2 - 8;

                // Piece color based on theme
                Color color;
                if (p.getColor() == Piece.Color.RED) {
                    color = currentTheme == Theme.CLASSIC ? new Color(0x8B0000) : new Color(0xD4AF37);
                } else {
                    color = currentTheme == Theme.CLASSIC ? Color.BLACK : new Color(0xC0C0C0);
                }

                // Shadow for depth
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillOval(cx - rad + 3, cy - rad + 3, rad * 2, rad * 2);

                // Main piece body
                g2d.setColor(color);
                g2d.fillOval(cx - rad, cy - rad, rad * 2, rad * 2);

                // Edge highlight
                g2d.setColor(color.brighter());
                g2d.drawOval(cx - rad, cy - rad, rad * 2, rad * 2);

                // Inner shine
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(cx - rad / 2, cy - rad / 2, rad, rad);

                // King piece
                if (p.isKing()) {
                    int crownSize = rad;
                    int crownX = cx - crownSize/2;
                    int crownY = cy - crownSize/2 - 2;

                    // Outer glow
                    g2d.setColor(new Color(0xD4AF37));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawOval(cx - rad - 4, cy - rad - 4, rad*2 + 8, rad*2 + 8);
                    g2d.setStroke(new BasicStroke(1));

                    // Crown base
                    g2d.setColor(new Color(0xD4AF37));
                    g2d.fillOval(crownX, crownY + crownSize/2 - 4, crownSize, 6);

                    // Crown peaks (triangle)
                    int[] xPoints = {
                            crownX, crownX + crownSize/2, crownX + crownSize
                    };
                    int[] yPoints = {
                            crownY + crownSize/2, crownY - 4, crownY + crownSize/2
                    };
                    g2d.fillPolygon(xPoints, yPoints, 3);

                    // Gold ring around center
                    g2d.setColor(new Color(0xFFD700));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(cx - rad/3, cy - rad/3, rad*2/3, rad*2/3);
                    g2d.setStroke(new BasicStroke(1));

                    // Crown jewels
                    g2d.fillOval(cx - 2, crownY - 1, 4, 4);
                    g2d.fillOval(crownX + 4, crownY + crownSize/4, 3, 3);
                    g2d.fillOval(crownX + crownSize - 7, crownY + crownSize/4, 3, 3);

                    // Crown outline
                    g2d.setColor(new Color(0xB8860B));
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawPolygon(xPoints, yPoints, 3);
                    g2d.drawOval(crownX, crownY + crownSize/2 - 4, crownSize, 6);
                    g2d.setStroke(new BasicStroke(1));
                }

                // Selected piece highlight
                if (selectedRow == r && selectedCol == c) {
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawOval(cx - rad - 2, cy - rad - 2, rad * 2 + 4, rad * 2 + 4);
                    g2d.setStroke(new BasicStroke(1));
                }

                // Chain capture highlight
                if (chainMove && selectedRow == r && selectedCol == c) {
                    g2d.setColor(new Color(255, 215, 0, 100));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawOval(cx - rad - 4, cy - rad - 4, rad * 2 + 8, rad * 2 + 8);
                    g2d.setStroke(new BasicStroke(1));
                }
            }
        }
    }
}
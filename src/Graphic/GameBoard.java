package Graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoard extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 65;
    private String playerName;
    private String opponentName;
    private String gameMode;
    private Piece[][] pieces;
    private int selectedRow = -1, selectedCol = -1;
    private Theme currentThemeLocal;

    public GameBoard(String playerName, String opponentName, String mode) {
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.gameMode = mode;
        this.currentThemeLocal = MainMenu.currentTheme;
        setPreferredSize(new Dimension(BOARD_SIZE * SQUARE_SIZE, BOARD_SIZE * SQUARE_SIZE));
        setBackground(new Color(0x2B0F0F));
        setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 4));

        initPieces();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;
                if (row < BOARD_SIZE && col < BOARD_SIZE) {
                    handleSquareClick(row, col);
                }
            }
        });
    }

    private void initPieces() {
        pieces = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    if (row < 3) {
                        pieces[row][col] = new Piece(PieceColor.BLACK);
                    } else if (row >= BOARD_SIZE - 3) {
                        pieces[row][col] = new Piece(PieceColor.RED);
                    } else {
                        pieces[row][col] = null;
                    }
                }
            }
        }
    }

    public void setTheme(Theme theme) {
        this.currentThemeLocal = theme;
        repaint();
    }

    private void handleSquareClick(int row, int col) {
        if (selectedRow == -1 && selectedCol == -1) {
            if (pieces[row][col] != null) {
                selectedRow = row;
                selectedCol = col;
                repaint();
            }
        } else {
            int fromRow = selectedRow, fromCol = selectedCol;
            selectedRow = -1;
            selectedCol = -1;
            if (pieces[row][col] == null && (row + col) % 2 == 1) {
                pieces[row][col] = pieces[fromRow][fromCol];
                pieces[fromRow][fromCol] = null;
                repaint();
                JOptionPane.showMessageDialog(this, "Move made (demo)");
            } else {
                if (pieces[row][col] != null) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                    repaint();
                }
            }
        }
    }

    public void setPiece(int row, int col, PieceColor color) {
        if (color == null) {
            pieces[row][col] = null;
        } else {
            pieces[row][col] = new Piece(color);
        }
        repaint();
    }

    public PieceColor getPieceColor(int row, int col) {
        if (pieces[row][col] != null) {
            return pieces[row][col].color;
        }
        return null;
    }

    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        repaint();
    }

    public void stopGame() { /* برای توقف تایمرها */ }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color lightColor, darkColor;
        if (currentThemeLocal == Theme.CLASSIC) {
            lightColor = new Color(0xF5E6D3);
            darkColor = new Color(0x3B2B1A);
        } else {
            lightColor = new Color(0xA0A0A0);
            darkColor = new Color(0x1A2A4A);
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int x = col * SQUARE_SIZE;
                int y = row * SQUARE_SIZE;
                if ((row + col) % 2 == 0) {
                    g2d.setColor(lightColor);
                } else {
                    g2d.setColor(darkColor);
                }
                g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                g2d.setColor(new Color(0xD4AF37));
                g2d.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece p = pieces[row][col];
                if (p != null) {
                    int centerX = col * SQUARE_SIZE + SQUARE_SIZE / 2;
                    int centerY = row * SQUARE_SIZE + SQUARE_SIZE / 2;
                    int radius = SQUARE_SIZE / 2 - 8;

                    Color pieceColor;
                    if (p.color == PieceColor.BLACK) {
                        pieceColor = Color.BLACK;
                    } else {
                        pieceColor = new Color(0x8B0000);
                    }

                    if (currentThemeLocal == Theme.MODERN) {
                        if (p.color == PieceColor.BLACK) {
                            pieceColor = new Color(0xC0C0C0);
                        } else {
                            pieceColor = new Color(0xD4AF37);
                        }
                    }

                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.fillOval(centerX - radius + 3, centerY - radius + 3, radius * 2, radius * 2);

                    g2d.setColor(pieceColor);
                    g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

                    g2d.setColor(pieceColor.brighter());
                    g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.fillOval(centerX - radius/2, centerY - radius/2, radius, radius);

                    if (selectedRow == row && selectedCol == col) {
                        g2d.setColor(Color.WHITE);
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawOval(centerX - radius - 2, centerY - radius - 2, radius * 2 + 4, radius * 2 + 4);
                        g2d.setStroke(new BasicStroke(1));
                    }
                }
            }
        }
    }
}
package Graphic;

import model.CheckersAI;
import model.GameState;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main menu and game launcher for Royal Checkers.
 * Handles:
 * - Main menu display with animation
 * - Navigation to settings, rules, and game modes
 * - Game session initialization
 * - Save/Load functionality
 */

public class MainMenu extends JFrame {

    private static final String[] BUTTON_LABELS = {"New Game", "Load Game", "Rules", "Settings", "Exit"};
    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_SPACING = 15;

    private JPanel mainPanel;
    private float titleAlpha = 0.0f;
    private float titleSize = 56f;
    private int titleY;
    private boolean transitionDone = false;
    private Timer splashTimer, transitionTimer, menuAppearTimer;
    private List<JButton> menuButtons = new ArrayList<>();
    private int currentButtonIndex = 0;
    private int titleTargetY, buttonsStartY;

    private GameBoard gameBoard;
    private Player player1;
    private Player player2;
    public static Theme currentTheme = Theme.CLASSIC;
    public static CheckersAI.Difficulty currentDifficulty = CheckersAI.Difficulty.MEDIUM;

    public MainMenu() {
        setTitle("Royal Checkers");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        recalculatePositions();

        // Custom panel with gradient background and animated title
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x2B0F0F),
                        getWidth(), getHeight(), new Color(0x1A1A1A));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                Font tf = new Font("Cinzel", Font.BOLD, Math.round(titleSize));
                g2d.setFont(tf);
                FontMetrics fm = g2d.getFontMetrics();
                String text = "Royal Checkers";
                int tw = fm.stringWidth(text);
                int x = (getWidth() - tw) / 2;
                int y = titleY + fm.getAscent();

                // Shadow
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha * 0.3f));
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x + 3, y + 3);

                // Main title
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
                g2d.setColor(new Color(0xD4AF37));
                g2d.drawString(text, x, y);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        };
        mainPanel.setLayout(null);
        add(mainPanel);
        setVisible(true);

        MusicManager.playMenuMusic();
        startSplashFadeIn();
    }

    private void recalculatePositions() {
        int w = getWidth(), h = getHeight();
        titleTargetY = (int) (h * 0.12);
        titleY = (h - 50) / 2 - 40;
        int totalBH = (BUTTON_HEIGHT + BUTTON_SPACING) * BUTTON_LABELS.length - BUTTON_SPACING;
        int gap = 40;
        int titleH = 60;
        int totalCH = titleH + gap + totalBH;
        int startY = (h - totalCH) / 2;
        buttonsStartY = startY + titleH + gap;
    }

    private void startSplashFadeIn() {
        titleAlpha = 0;
        splashTimer = new Timer(50, e -> {
            titleAlpha += 0.015f;
            if (titleAlpha >= 1) {
                titleAlpha = 1;
                splashTimer.stop();
                new Timer(1000, ev -> startTransitionToMenu()).start();
            }
            mainPanel.repaint();
        });
        splashTimer.start();
    }

    private void startTransitionToMenu() {
        if (transitionDone) return;
        transitionDone = true;
        int startY = titleY, targetY = titleTargetY;
        int steps = 40, delay = 20;
        transitionTimer = new Timer(delay, new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                float progress = Math.min((float) step / steps, 1f);
                titleY = (int) (startY - (startY - targetY) * progress);
                if (progress >= 1) {
                    transitionTimer.stop();
                    titleY = targetY;
                    showMainMenu();
                }
                mainPanel.repaint();
            }
        });
        transitionTimer.start();
    }



    // Displays the main menu with all buttons.
    private void showMainMenu() {
        MusicManager.stopGameMusic();
        MusicManager.playMenuMusic();

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        menuButtons.clear();


        for (int i = 0; i < BUTTON_LABELS.length; i++) {
            JButton btn = createStyledButton(BUTTON_LABELS[i]);
            int x = (getWidth() - BUTTON_WIDTH) / 2;
            int y = buttonsStartY + i * (BUTTON_HEIGHT + BUTTON_SPACING);
            btn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            btn.setVisible(false);
            btn.addActionListener(e -> handleButtonAction(btn.getText()));
            mainPanel.add(btn);
            menuButtons.add(btn);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
        startMenuAppearAnimation();
    }


    private void startMenuAppearAnimation() {
        currentButtonIndex = 0;
        if (menuAppearTimer != null && menuAppearTimer.isRunning()) menuAppearTimer.stop();
        menuAppearTimer = new Timer(150, e -> {
            if (currentButtonIndex < menuButtons.size()) {
                menuButtons.get(currentButtonIndex).setVisible(true);
                currentButtonIndex++;
                mainPanel.repaint();
            } else menuAppearTimer.stop();
        });
        menuAppearTimer.start();
    }

    /**
     * Shows game mode selection screen with three options:
     * - VS Computer (active)
     * - Online (shows "Coming Soon" message)
     * - Local 2-Player (active)
     */

    private void showModeSelection() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);

        int bw = 220, bh = 50, sp = 20;
        int totalH = (bh + sp) * 4 - sp;
        int startY = (getHeight() - totalH) / 2;

        JButton vsComp = createStyledButton("VS Computer");
        vsComp.setBounds((getWidth() - bw) / 2, startY, bw, bh);
        vsComp.addActionListener(e -> startGame("computer"));
        mainPanel.add(vsComp);

        JButton online = createStyledButton("Online");
        online.setBounds((getWidth() - bw) / 2, startY + bh + sp, bw, bh);
        online.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Online mode is coming soon!");
        });
        mainPanel.add(online);

        JButton local = createStyledButton("Local 2-Player");
        local.setBounds((getWidth() - bw) / 2, startY + 2 * (bh + sp), bw, bh);
        local.addActionListener(e -> startGame("local"));
        mainPanel.add(local);

        JButton back = new JButton("Back");
        back.setFont(new Font("Pristina", Font.PLAIN, 24));
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(0x555555));
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        back.addActionListener(e -> showMainMenu());
        back.setBounds((getWidth() - bw) / 2, startY + 3 * (bh + sp), bw, bh);
        mainPanel.add(back);

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    // Starts a new game , Collects player names and initializes the game board.
    private void startGame(String mode) {
        MusicManager.stopMenuMusic();
        MusicManager.playGameMusic();

        String playerName1 = JOptionPane.showInputDialog(this, "Enter Player 1 name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
        if (playerName1 == null || playerName1.trim().isEmpty()) playerName1 = "Player 1";

        player1 = new Player(playerName1);

        if (mode.equals("local")) {
            String playerName2 = JOptionPane.showInputDialog(this, "Enter Player 2 name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
            if (playerName2 == null || playerName2.trim().isEmpty()) playerName2 = "Player 2";
            player2 = new Player(playerName2);
        } else {
            player2 = new Player(mode.equals("computer") ? "Computer" : "Player 2");
        }

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        // Top panel: opponent info
        String opp = mode.equals("computer") ? "Computer" : (mode.equals("local") ? player2.getName() : "Player 2");
        JLabel oppLabel = new JLabel("Opponent: " + opp);
        oppLabel.setFont(new Font("Pristina", Font.PLAIN, 28));
        oppLabel.setForeground(new Color(0xD4AF37));
        oppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.setOpaque(false);
        top.add(oppLabel);
        top.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(top, BorderLayout.NORTH);

        // Game board
        gameBoard = new GameBoard(mode, currentDifficulty);
        gameBoard.setPreferredSize(new Dimension(520, 520));
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(gameBoard);
        mainPanel.add(wrap, BorderLayout.CENTER);

        // Bottom panel: player info and controls
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String playerLabelText = mode.equals("local") ?
                "Player 1: " + player1.getName() + " (" + getColorName(true) + ")  |  Player 2: " + player2.getName() + " (" + getColorName(false) + ")" :
                "Player: " + player1.getName() + " (" + getColorName(true) + ")";

        JLabel nameLabel = new JLabel(playerLabelText);
        nameLabel.setFont(new Font("Pristina", Font.PLAIN, 20));
        nameLabel.setForeground(new Color(0xD4AF37));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setOpaque(false);

        JButton back = new JButton("Back to Menu");
        back.setFont(new Font("Pristina", Font.PLAIN, 20));
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(0x444444));
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        back.addActionListener(e -> {
            if (gameBoard != null) gameBoard.stopGame();
            showMainMenu();
        });
        btnPanel.add(back);

        if (mode.equals("computer") || mode.equals("local")) {
            JButton save = new JButton("Save Game");
            save.setFont(new Font("Pristina", Font.PLAIN, 20));
            save.setForeground(Color.WHITE);
            save.setBackground(new Color(0x006400));
            save.setFocusPainted(false);
            save.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
            save.addActionListener(e -> saveGame());
            btnPanel.add(save);
        }

        bottom.add(nameLabel);
        bottom.add(Box.createRigidArea(new Dimension(0, 5)));
        bottom.add(btnPanel);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    // Saves the current game state to a file.
    private void saveGame() {
        if (gameBoard == null) {
            JOptionPane.showMessageDialog(this, "No game to save!");
            return;
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("savegame.dat"));
            out.writeObject(gameBoard.getGameState());
            out.close();
            JOptionPane.showMessageDialog(this, "Game saved!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
        }
    }



    // Loads a saved game from file.
    private void loadGame() {
        try {
            File f = new File("savegame.dat");
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "No saved game found!");
                return;
            }

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            GameState loadedState = (GameState) in.readObject();
            in.close();

            String loadedMode = loadedState.getGameMode();

            // If a game is already running, just update it
            if (gameBoard != null) {
                gameBoard.setGameState(loadedState);
                JOptionPane.showMessageDialog(this, "Game loaded!");
                return;
            }

            // If no game is running, create a new session with loaded state
            String name = JOptionPane.showInputDialog(this, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.trim().isEmpty()) name = "Player";

            player1 = new Player(name);
            player2 = new Player(loadedMode.equals("computer") ? "Computer" : "Player 2");

            mainPanel.removeAll();
            mainPanel.setLayout(new BorderLayout());

            String opp = loadedMode.equals("computer") ? "Computer" :
                    (loadedMode.equals("local") ? player2.getName() : "Player 2");
            JLabel oppLabel = new JLabel("Opponent: " + opp);
            oppLabel.setFont(new Font("Pristina", Font.PLAIN, 28));
            oppLabel.setForeground(new Color(0xD4AF37));
            oppLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
            top.setOpaque(false);
            top.add(oppLabel);
            top.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            mainPanel.add(top, BorderLayout.NORTH);

            gameBoard = new GameBoard(loadedMode, currentDifficulty);
            gameBoard.setGameState(loadedState);
            gameBoard.setPreferredSize(new Dimension(520, 520));
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setOpaque(false);
            wrap.add(gameBoard);
            mainPanel.add(wrap, BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            bottom.setOpaque(false);
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
            bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            String playerLabelText = loadedMode.equals("local") ?
                    "Player 1: " + player1.getName() + " (" + getColorName(true) + ")  |  Player 2: " + player2.getName() + " (" + getColorName(false) + ")" :
                    "Player: " + player1.getName() + " (" + getColorName(true) + ")";

            JLabel nameLabel = new JLabel(playerLabelText);
            nameLabel.setFont(new Font("Pristina", Font.PLAIN, 20));
            nameLabel.setForeground(new Color(0xD4AF37));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
            btnPanel.setOpaque(false);

            JButton back = new JButton("Back to Menu");
            back.setFont(new Font("Pristina", Font.PLAIN, 20));
            back.setForeground(Color.WHITE);
            back.setBackground(new Color(0x444444));
            back.setFocusPainted(false);
            back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            back.addActionListener(e -> {
                if (gameBoard != null) gameBoard.stopGame();
                showMainMenu();
            });
            btnPanel.add(back);

            if (loadedMode.equals("computer") || loadedMode.equals("local")) {
                JButton save = new JButton("Save Game");
                save.setFont(new Font("Pristina", Font.PLAIN, 20));
                save.setForeground(Color.WHITE);
                save.setBackground(new Color(0x006400));
                save.setFocusPainted(false);
                save.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
                save.addActionListener(e -> saveGame());
                btnPanel.add(save);
            }

            bottom.add(nameLabel);
            bottom.add(Box.createRigidArea(new Dimension(0, 5)));
            bottom.add(btnPanel);

            mainPanel.add(bottom, BorderLayout.SOUTH);

            mainPanel.revalidate();
            mainPanel.repaint();

            JOptionPane.showMessageDialog(this, "Game loaded!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading: " + e.getMessage());
        }
    }


    // Displays the rules page with all game rules.
    private void showRulesPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        JLabel title = new JLabel("Rules");
        title.setFont(new Font("Cinzel", Font.BOLD, 36));
        title.setForeground(new Color(0xD4AF37));
        top.add(title);
        mainPanel.add(top, BorderLayout.NORTH);

        String rules =
                "1. The game is played on an 8x8 checkerboard, and each player starts with 12 pieces placed on the dark squares of their first three rows.\n\n" +
                        "2. Players take turns moving one piece diagonally forward to an empty adjacent dark square.\n\n" +
                        "3. To capture an opponent's piece, you must jump over it diagonally into an empty square behind it; if a capture is available, it is mandatory.\n\n" +
                        "4. When a piece reaches the opponent's farthest back row, it becomes a 'king' and can move and capture both forward and backward.\n\n" +
                        "5. The goal is to capture all of your opponent's pieces or block them so they have no legal moves left.";

        JTextArea area = new JTextArea(rules);
        area.setFont(new Font("Pristina", Font.PLAIN, 24));
        area.setForeground(new Color(0xF5E6D3));
        area.setBackground(new Color(0, 0, 0, 0));
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        area.setOpaque(false);

        JScrollPane sp = new JScrollPane(area);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(50, 30, 25, 30));
        center.add(sp, BorderLayout.CENTER);
        mainPanel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JButton back = new JButton("Back");
        back.setFont(new Font("Pristina", Font.PLAIN, 24));
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(0x555555));
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        back.addActionListener(e -> showMainMenu());
        bottom.add(back);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    /**
     * Displays settings panel with:
     * - Theme selection (Classic/Modern)
     * - Difficulty selection (Easy/Medium/Hard)
     * - Sound controls (Mute/Unmute for menu and game)
     */
    private void showSettings() {
        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(120, 15, 10, 15); // Increased top padding from 50 to 120
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Cinzel", Font.BOLD, 28));
        title.setForeground(new Color(0xD4AF37));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, gbc);

        // Reset insets for remaining components
        gbc.insets = new Insets(10, 15, 10, 15);

        // Theme section
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel themeLbl = new JLabel("Select Theme:");
        themeLbl.setFont(new Font("Pristina", Font.PLAIN, 24));
        themeLbl.setForeground(Color.WHITE);
        themeLbl.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(themeLbl, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton classic = createStyledButton("Classic Theme");
        classic.addActionListener(e -> {
            currentTheme = Theme.CLASSIC;
            applyTheme();
            JOptionPane.showMessageDialog(this, "Theme: Classic");
        });
        mainPanel.add(classic, gbc);

        gbc.gridx = 1;
        JButton modern = createStyledButton("Modern Theme");
        modern.addActionListener(e -> {
            currentTheme = Theme.MODERN;
            applyTheme();
            JOptionPane.showMessageDialog(this, "Theme: Modern");
        });
        mainPanel.add(modern, gbc);

        // Difficulty section
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel diffLbl = new JLabel("Difficulty Level:");
        diffLbl.setFont(new Font("Pristina", Font.PLAIN, 24));
        diffLbl.setForeground(Color.WHITE);
        diffLbl.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(diffLbl, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton easyBtn = createStyledButton("Easy");
        easyBtn.addActionListener(e -> {
            currentDifficulty = CheckersAI.Difficulty.EASY;
            applyDifficulty();
            JOptionPane.showMessageDialog(this, "Difficulty: Easy");
        });
        mainPanel.add(easyBtn, gbc);

        gbc.gridx = 1;
        JButton mediumBtn = createStyledButton("Medium");
        mediumBtn.addActionListener(e -> {
            currentDifficulty = CheckersAI.Difficulty.MEDIUM;
            applyDifficulty();
            JOptionPane.showMessageDialog(this, "Difficulty: Medium");
        });
        mainPanel.add(mediumBtn, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton hardBtn = createStyledButton("Hard");
        hardBtn.addActionListener(e -> {
            currentDifficulty = CheckersAI.Difficulty.HARD;
            applyDifficulty();
            JOptionPane.showMessageDialog(this, "Difficulty: Hard");
        });
        mainPanel.add(hardBtn, gbc);

        // Sound section
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel soundLbl = new JLabel("Sound Settings:");
        soundLbl.setFont(new Font("Pristina", Font.PLAIN, 24));
        soundLbl.setForeground(Color.WHITE);
        soundLbl.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(soundLbl, gbc);

        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton muteMenu = new JButton(MusicManager.isMenuMuted() ? "Unmute Menu" : "Mute Menu");
        muteMenu.setFont(new Font("Pristina", Font.PLAIN, 28));
        muteMenu.setForeground(Color.WHITE);
        muteMenu.setBackground(MusicManager.isMenuMuted() ? new Color(0x8B0000) : new Color(0x006400));
        muteMenu.setFocusPainted(false);
        muteMenu.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
        muteMenu.addActionListener(e -> {
            MusicManager.toggleMenuMute();
            muteMenu.setText(MusicManager.isMenuMuted() ? "Unmute Menu" : "Mute Menu");
            muteMenu.setBackground(MusicManager.isMenuMuted() ? new Color(0x8B0000) : new Color(0x006400));
        });
        mainPanel.add(muteMenu, gbc);

        gbc.gridx = 1;
        JButton muteGame = new JButton(MusicManager.isGameMuted() ? "Unmute Game" : "Mute Game");
        muteGame.setFont(new Font("Pristina", Font.PLAIN, 28));
        muteGame.setForeground(Color.WHITE);
        muteGame.setBackground(MusicManager.isGameMuted() ? new Color(0x8B0000) : new Color(0x006400));
        muteGame.setFocusPainted(false);
        muteGame.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
        muteGame.addActionListener(e -> {
            MusicManager.toggleGameMute();
            muteGame.setText(MusicManager.isGameMuted() ? "Unmute Game" : "Mute Game");
            muteGame.setBackground(MusicManager.isGameMuted() ? new Color(0x8B0000) : new Color(0x006400));
        });
        mainPanel.add(muteGame, gbc);

        // Back button
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton back = new JButton("Back");
        back.setFont(new Font("Pristina", Font.PLAIN, 24));
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(0x555555));
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        back.addActionListener(e -> showMainMenu());
        mainPanel.add(back, gbc);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void applyTheme() {
        if (gameBoard != null) gameBoard.setTheme(currentTheme);
    }

    private void applyDifficulty() {
        if (gameBoard != null) gameBoard.setDifficulty(currentDifficulty);
    }


    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Pristina", Font.PLAIN, 28));
        btn.setForeground(new Color(0xF5F5F5));
        btn.setBackground(new Color(0x8B0000));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0xA52A2A));
                btn.setBorder(BorderFactory.createLineBorder(new Color(0xFFD700), 3));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x8B0000));
                btn.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
            }
        });
        return btn;
    }


    /**
     * Returns the color name based on current theme and piece color.
     * Classic: Black/Red, Modern: Silver/Gold
     */
    private String getColorName(boolean isBlack) {
        if (currentTheme == Theme.CLASSIC) {
            return isBlack ? "Black" : "Red";
        } else {
            return isBlack ? "Silver" : "Gold";
        }
    }


    private void handleButtonAction(String cmd) {
        switch (cmd) {
            case "New Game":
                showModeSelection();
                break;
            case "Load Game":
                loadGame();
                break;
            case "Rules":
                showRulesPage();
                break;
            case "Settings":
                showSettings();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}
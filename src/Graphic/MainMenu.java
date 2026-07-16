package Graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
    private Timer splashTimer;
    private Timer transitionTimer;
    private Timer menuAppearTimer;
    private List<JButton> menuButtons = new ArrayList<>();
    private int currentButtonIndex = 0;
    private int titleTargetY;
    private int buttonsStartY;

    private GameBoard gameBoard;
    private String currentMode;
    public static Theme currentTheme = Theme.CLASSIC;

    public MainMenu() {
        setTitle("Royal Checkers");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        recalculatePositions();

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(0, 0, new Color(0x2B0F0F),
                        getWidth(), getHeight(), new Color(0x1A1A1A));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                Font titleFont = new Font("Cinzel", Font.BOLD, Math.round(titleSize));
                g2d.setFont(titleFont);
                FontMetrics fm = g2d.getFontMetrics();
                String titleText = "Royal Checkers";
                int textWidth = fm.stringWidth(titleText);
                int x = (getWidth() - textWidth) / 2;
                int y = titleY + fm.getAscent();

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha * 0.3f));
                g2d.setColor(Color.BLACK);
                g2d.drawString(titleText, x + 3, y + 3);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
                g2d.setColor(new Color(0xD4AF37));
                g2d.drawString(titleText, x, y);
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
        int width = getWidth();
        int height = getHeight();

        titleTargetY = (int) (height * 0.12);
        titleY = (height - 50) / 2 - 40;

        int totalButtonsHeight = (BUTTON_HEIGHT + BUTTON_SPACING) * BUTTON_LABELS.length - BUTTON_SPACING;
        int gapBetweenTitleAndButtons = 40;
        int titleHeight = 60;
        int totalContentHeight = titleHeight + gapBetweenTitleAndButtons + totalButtonsHeight;
        int contentStartY = (height - totalContentHeight) / 2;
        buttonsStartY = contentStartY + titleHeight + gapBetweenTitleAndButtons;
    }

    private void startSplashFadeIn() {
        titleAlpha = 0.0f;
        splashTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                titleAlpha += 0.015f;
                if (titleAlpha >= 1.0f) {
                    titleAlpha = 1.0f;
                    splashTimer.stop();
                    new Timer(1000, ev -> startTransitionToMenu()).start();
                }
                mainPanel.repaint();
            }
        });
        splashTimer.start();
    }

    private void startTransitionToMenu() {
        if (transitionDone) return;
        transitionDone = true;

        final int startY = titleY;
        final int targetY = titleTargetY;
        final int steps = 40;
        final int delay = 20;

        transitionTimer = new Timer(delay, new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                float progress = Math.min((float) step / steps, 1.0f);
                titleY = (int) (startY - (startY - targetY) * progress);

                if (progress >= 1.0f) {
                    transitionTimer.stop();
                    titleY = targetY;
                    showMainMenu();
                }
                mainPanel.repaint();
            }
        });
        transitionTimer.start();
    }

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
        if (menuAppearTimer != null && menuAppearTimer.isRunning()) {
            menuAppearTimer.stop();
        }
        menuAppearTimer = new Timer(150, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentButtonIndex < menuButtons.size()) {
                    JButton btn = menuButtons.get(currentButtonIndex);
                    btn.setVisible(true);
                    currentButtonIndex++;
                    mainPanel.repaint();
                } else {
                    menuAppearTimer.stop();
                }
            }
        });
        menuAppearTimer.start();
    }

    private void showModeSelection() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);

        int modeButtonWidth = 220;
        int modeButtonHeight = 50;
        int spacing = 20;
        int totalButtonsHeight = (modeButtonHeight + spacing) * 3 - spacing;
        int startY = (getHeight() - totalButtonsHeight) / 2;

        JButton vsComputerBtn = createStyledButton("VS Computer");
        vsComputerBtn.setBounds((getWidth() - modeButtonWidth) / 2, startY, modeButtonWidth, modeButtonHeight);
        vsComputerBtn.addActionListener(e -> startGame("computer"));
        mainPanel.add(vsComputerBtn);

        JButton onlineBtn = createStyledButton("Online");
        onlineBtn.setBounds((getWidth() - modeButtonWidth) / 2, startY + (modeButtonHeight + spacing), modeButtonWidth, modeButtonHeight);
        onlineBtn.addActionListener(e -> startGame("online"));
        mainPanel.add(onlineBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Pristina", Font.PLAIN, 24));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(0x555555));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        backBtn.addActionListener(e -> showMainMenu());
        backBtn.setBounds((getWidth() - modeButtonWidth) / 2, startY + 2 * (modeButtonHeight + spacing), modeButtonWidth, modeButtonHeight);
        mainPanel.add(backBtn);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void startGame(String mode) {
        MusicManager.stopMenuMusic();
        MusicManager.playGameMusic();

        currentMode = mode;
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        String opponentName = mode.equals("computer") ? "Computer" : "Player 2";
        JLabel opponentLabel = new JLabel("Opponent: " + opponentName);
        opponentLabel.setFont(new Font("Pristina", Font.PLAIN, 28));
        opponentLabel.setForeground(new Color(0xD4AF37));
        opponentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        topPanel.add(opponentLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        gameBoard = new GameBoard(playerName, opponentName, mode);
        gameBoard.setPreferredSize(new Dimension(520, 520));
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(gameBoard);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel playerLabel = new JLabel("Player: " + playerName);
        playerLabel.setFont(new Font("Pristina", Font.PLAIN, 28));
        playerLabel.setForeground(new Color(0xD4AF37));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);

        JButton backToMenu = new JButton("Back to Menu");
        backToMenu.setFont(new Font("Pristina", Font.PLAIN, 20));
        backToMenu.setForeground(Color.WHITE);
        backToMenu.setBackground(new Color(0x444444));
        backToMenu.setFocusPainted(false);
        backToMenu.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        backToMenu.addActionListener(e -> {
            if (gameBoard != null) gameBoard.stopGame();
            showMainMenu();
        });
        buttonPanel.add(backToMenu);

        if (mode.equals("computer")) {
            JButton saveButton = new JButton("Save Game");
            saveButton.setFont(new Font("Pristina", Font.PLAIN, 20));
            saveButton.setForeground(Color.WHITE);
            saveButton.setBackground(new Color(0x006400));
            saveButton.setFocusPainted(false);
            saveButton.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
            saveButton.addActionListener(e -> saveGame());
            buttonPanel.add(saveButton);
        }

        bottomPanel.add(playerLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bottomPanel.add(buttonPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void saveGame() {
        JOptionPane.showMessageDialog(this, "Game saved successfully (demo)!");
    }

    private void loadGame() {
        JOptionPane.showMessageDialog(this, "Load game feature will be implemented later.");
    }

    private void showRulesPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        JLabel titleLabel = new JLabel("Rules");
        titleLabel.setFont(new Font("Cinzel", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0xD4AF37));
        topPanel.add(titleLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 25, 30));

        String rulesText =
                "1. The game is played on an 8x8 checkerboard, and each player starts with 12 pieces placed on the dark squares of their first three rows.\n\n" +
                        "2. Players take turns moving one piece diagonally forward to an empty adjacent dark square.\n\n" +
                        "3. To capture an opponent's piece, you must jump over it diagonally into an empty square behind it; if a capture is available, it is mandatory.\n\n" +
                        "4. When a piece reaches the opponent's farthest back row, it becomes a 'king' and can move and capture both forward and backward.\n\n" +
                        "5. The goal is to capture all of your opponent's pieces or block them so they have no legal moves left.";

        JTextArea rulesArea = new JTextArea(rulesText);
        rulesArea.setFont(new Font("Pristina", Font.PLAIN, 24));
        rulesArea.setForeground(new Color(0xF5E6D3));
        rulesArea.setBackground(new Color(0, 0, 0, 0));
        rulesArea.setEditable(false);
        rulesArea.setFocusable(false);
        rulesArea.setLineWrap(true);
        rulesArea.setWrapStyleWord(true);
        rulesArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rulesArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(rulesArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Pristina", Font.PLAIN, 24));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(0x555555));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> showMainMenu());
        bottomPanel.add(backBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showSettings() {
        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Cinzel", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0xD4AF37));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel themeLabel = new JLabel("Select Theme:");
        themeLabel.setFont(new Font("Pristina", Font.PLAIN, 24));
        themeLabel.setForeground(Color.WHITE);
        themeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(themeLabel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton classicBtn = createStyledButton("Classic Theme");
        classicBtn.addActionListener(e -> {
            currentTheme = Theme.CLASSIC;
            applyThemeToGame();
            JOptionPane.showMessageDialog(this, "Theme changed to Classic");
        });
        mainPanel.add(classicBtn, gbc);

        gbc.gridx = 1;
        JButton modernBtn = createStyledButton("Modern Theme");
        modernBtn.addActionListener(e -> {
            currentTheme = Theme.MODERN;
            applyThemeToGame();
            JOptionPane.showMessageDialog(this, "Theme changed to Modern");
        });
        mainPanel.add(modernBtn, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel soundLabel = new JLabel("Sound Settings:");
        soundLabel.setFont(new Font("Pristina", Font.PLAIN, 24));
        soundLabel.setForeground(Color.WHITE);
        soundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(soundLabel, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton muteMenuBtn = new JButton(MusicManager.isMenuMuted() ? "Unmute Menu Music" : "Mute Menu Music");
        muteMenuBtn.setFont(new Font("Pristina", Font.PLAIN, 28));
        muteMenuBtn.setForeground(Color.WHITE);
        muteMenuBtn.setBackground(MusicManager.isMenuMuted() ? new Color(0x8B0000) : new Color(0x006400));
        muteMenuBtn.setFocusPainted(false);
        muteMenuBtn.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
        muteMenuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        muteMenuBtn.addActionListener(e -> {
            MusicManager.toggleMenuMute();
            muteMenuBtn.setText(MusicManager.isMenuMuted() ? "Unmute Menu Music" : "Mute Menu Music");
            muteMenuBtn.setBackground(MusicManager.isMenuMuted() ? new Color(0x8B0000) : new Color(0x006400));
        });
        mainPanel.add(muteMenuBtn, gbc);

        gbc.gridx = 1;
        JButton muteGameBtn = new JButton(MusicManager.isGameMuted() ? "Unmute Game Music" : "Mute Game Music");
        muteGameBtn.setFont(new Font("Pristina", Font.PLAIN, 28));
        muteGameBtn.setForeground(Color.WHITE);
        muteGameBtn.setBackground(MusicManager.isGameMuted() ? new Color(0x8B0000) : new Color(0x006400));
        muteGameBtn.setFocusPainted(false);
        muteGameBtn.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
        muteGameBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        muteGameBtn.addActionListener(e -> {
            MusicManager.toggleGameMute();
            muteGameBtn.setText(MusicManager.isGameMuted() ? "Unmute Game Music" : "Mute Game Music");
            muteGameBtn.setBackground(MusicManager.isGameMuted() ? new Color(0x8B0000) : new Color(0x006400));
        });
        mainPanel.add(muteGameBtn, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Pristina", Font.PLAIN, 24));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(0x555555));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> showMainMenu());
        mainPanel.add(backBtn, gbc);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void applyThemeToGame() {
        if (gameBoard != null) {
            gameBoard.setTheme(currentTheme);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Pristina", Font.PLAIN, 28));
        button.setForeground(new Color(0xF5F5F5));
        button.setBackground(new Color(0x8B0000));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xA52A2A));
                button.setBorder(BorderFactory.createLineBorder(new Color(0xFFD700), 3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x8B0000));
                button.setBorder(BorderFactory.createLineBorder(new Color(0xD4AF37), 2));
            }
        });
        return button;
    }

    private void handleButtonAction(String command) {
        switch (command) {
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
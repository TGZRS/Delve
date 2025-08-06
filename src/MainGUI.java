import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private JLabel statsLabel;
    private JLabel roomLabel;
    private JTextArea gameLog;
    private JButton enterRoomButton;
    private JButton exitGameButton;
    private JButton showStatsButton;

    // Combat components
    private JDialog combatDialog;
    private JLabel enemyStatsLabel;
    private JButton attackButton;
    private JButton runButton;
    private JButton combatStatsButton;
    private short currentEnemyHealth;
    private short maxEnemyHealth;
    private short enemyDamage;
    private String enemyType;
    private boolean isBoss;

    public MainGUI() {
        initializeGUI();
        setupGame();
    }

    private void initializeGUI() {
        setTitle("Dungeon Crawler v" + Config.releaseVersion);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Create main panels
        JPanel topPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new FlowLayout());

        // Stats panel
        statsLabel = new JLabel();
        topPanel.add(statsLabel);

        // Game log area
        gameLog = new JTextArea(30, 120);
        gameLog.setEditable(false);
        gameLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(gameLog);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Room info
        roomLabel = new JLabel("", SwingConstants.CENTER);
        roomLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        centerPanel.add(roomLabel, BorderLayout.NORTH);

        // Control buttons
        enterRoomButton = new JButton("Enter Room");
        exitGameButton = new JButton("Exit Game");
        showStatsButton = new JButton("Show Stats");

        bottomPanel.add(enterRoomButton);
        bottomPanel.add(showStatsButton);
        bottomPanel.add(exitGameButton);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button listeners
        enterRoomButton.addActionListener(e -> enterRoom());
        exitGameButton.addActionListener(e -> exitGame());
        showStatsButton.addActionListener(e -> showStats());

        pack();
        setLocationRelativeTo(null);
    }

    private void setupGame() {
        String playerName = JOptionPane.showInputDialog(this, "Enter Player Name:", "Welcome", JOptionPane.QUESTION_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Hero";
        }
        Config.playerName = playerName.trim();

        updateStats();
        updateRoomLabel();
        appendToLog("Welcome to the Dungeon Crawler, " + Config.playerName + "!");
        appendToLog("Controls: Enter Room to progress, Show Stats to view current stats, Exit Game to quit");
        appendToLog("Every 5th room has a shop, every 10th room has a boss!");
    }

    private void enterRoom() {
        appendToLog("\n--- Entering Room " + Config.roomInstanceCount + " ---");
        GameLogic.randomEvent(this);
        Config.roomInstanceCount++;
        Config.difficultyMultiplier = (byte)Math.round((float)((Config.roomInstanceCount/10 + 1)));
        updateRoomLabel();
    }

    private void exitGame() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?\nYou will leave with " + Config.playerGold + " gold.",
                "Exit Game", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            appendToLog("You leave the catacombs...");
            appendToLog("You exited with " + Config.playerGold + " gold");
            appendToLog("Your final attack was: " + Config.playerAttack);
            System.exit(0);
        }
    }

    private void showStats() {
        String stats = Config.playerName + " Stats:\n\n" +
                "Room: " + Config.roomInstanceCount + "\n" +
                "Gold: " + Config.playerGold + "\n" +
                "Attack: " + Config.playerAttack + "\n" +
                "Health: " + Config.playerHealth + "/" + Config.maxPlayerHealth + "\n" +
                "Difficulty: " + Config.difficultyMultiplier;
        JOptionPane.showMessageDialog(this, stats, "Player Stats", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateStats() {
        statsLabel.setText(String.format("Gold: %d | Attack: %d | Health: %d/%d",
                Config.playerGold, Config.playerAttack, Config.playerHealth, Config.maxPlayerHealth));
    }

    private void updateRoomLabel() {
        String roomText = "Current Room: " + Config.roomInstanceCount;
        if (Config.roomInstanceCount % 10 == 0) {
            roomText += " (BOSS ROOM!)";
        } else if (Config.roomInstanceCount % 5 == 0) {
            roomText += " (Shop)";
        }
        roomLabel.setText(roomText);
    }

    public void appendToLog(String message) {
        gameLog.append(message + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        appendToLog(message.replace("\n", " "));
    }

    public void startCombat(String enemyType, short enemyHealth, short enemyDamage, boolean isBoss) {
        this.enemyType = enemyType;
        this.currentEnemyHealth = enemyHealth;
        this.maxEnemyHealth = enemyHealth;
        this.enemyDamage = enemyDamage;
        this.isBoss = isBoss;

        appendToLog("A " + enemyType + " appears with " + enemyHealth + " health!");

        showCombatDialog();
    }

    private void showCombatDialog() {
        combatDialog = new JDialog(this, "Combat: " + enemyType, true);
        combatDialog.setLayout(new BorderLayout());
        combatDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Enemy stats
        enemyStatsLabel = new JLabel("", SwingConstants.CENTER);
        updateEnemyStats();
        combatDialog.add(enemyStatsLabel, BorderLayout.NORTH);

        // Combat buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        attackButton = new JButton("Attack");
        combatStatsButton = new JButton("Show Stats");

        if (!isBoss) {
            runButton = new JButton("Run Away");
            runButton.addActionListener(e -> runAway());
            buttonPanel.add(runButton);
        }

        attackButton.addActionListener(e -> performAttack());
        combatStatsButton.addActionListener(e -> showStats());

        buttonPanel.add(attackButton);
        buttonPanel.add(combatStatsButton);

        combatDialog.add(buttonPanel, BorderLayout.SOUTH);

        combatDialog.pack();
        combatDialog.setLocationRelativeTo(this);
        combatDialog.setVisible(true);
    }

    private void updateEnemyStats() {
        enemyStatsLabel.setText("<html><center>" + enemyType + "<br>Health: " +
                currentEnemyHealth + "/" + maxEnemyHealth + "<br><br>Your Health: " +
                Config.playerHealth + "/" + Config.maxPlayerHealth + "</center></html>");
    }

    private void performAttack() {
        // Player attacks
        short playerDamage = GameLogic.calculatePlayerDamage();
        currentEnemyHealth -= playerDamage;

        String combatResult = "You deal " + playerDamage + " damage!";

        if (currentEnemyHealth <= 0) {
            // Enemy defeated
            combatResult += "\n" + enemyType + " defeated!";
            JOptionPane.showMessageDialog(combatDialog, combatResult, "Combat", JOptionPane.INFORMATION_MESSAGE);
            combatDialog.dispose();
            GameLogic.handleCombatResult(this, true, enemyType, isBoss);
            return;
        }

        // Enemy attacks back
        GameLogic.takeDamage(enemyDamage);
        combatResult += "\n" + enemyType + " deals " + enemyDamage + " damage to you!";

        updateEnemyStats();

        if (!GameLogic.isPlayerAlive()) {
            // Player died
            combatResult += "\nYou have been defeated!";
            JOptionPane.showMessageDialog(combatDialog, combatResult, "Combat", JOptionPane.ERROR_MESSAGE);
            combatDialog.dispose();
            gameOver();
            return;
        }

        updateStats();
        JOptionPane.showMessageDialog(combatDialog, combatResult, "Combat", JOptionPane.INFORMATION_MESSAGE);
    }

    private void runAway() {
        appendToLog("You ran away from the " + enemyType + "...");
        combatDialog.dispose();
    }

    private void gameOver() {
        String gameOverMessage = "Game Over!\n\nYou died with " + Config.playerGold + " gold\n" +
                "Your final attack was: " + Config.playerAttack + "\n" +
                "You reached room: " + Config.roomInstanceCount;

        JOptionPane.showMessageDialog(this, gameOverMessage, "Game Over", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public void showShop() {
        appendToLog("You found a shop!");

        while (true) {
            String[] options = {"Heal Potion (100 Gold)", "Attack Potion (100 Gold)", "Leave Shop"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Welcome to the shop!\nYour Gold: " + Config.playerGold + "\n\nWhat would you like to buy?",
                    "Shop",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            switch (choice) {
                case 0: // Heal Potion
                    if (GameLogic.canAfford(100)) {
                        GameLogic.buyHealPotion();
                        updateStats();
                        appendToLog("Bought Heal Potion! Health: " + Config.playerHealth + ", Gold: " + Config.playerGold);
                        JOptionPane.showMessageDialog(this, "You bought a Heal Potion!\nHealth restored by 25!\nRemaining Gold: " + Config.playerGold, "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "You don't have enough gold!\nYou need " + (100 - Config.playerGold) + " more gold.", "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case 1: // Attack Potion
                    if (GameLogic.canAfford(100)) {
                        GameLogic.buyAttackPotion();
                        updateStats();
                        appendToLog("Bought Attack Potion! Attack: " + Config.playerAttack + ", Gold: " + Config.playerGold);
                        JOptionPane.showMessageDialog(this, "You bought an Attack Potion!\nAttack increased by 10!\nRemaining Gold: " + Config.playerGold, "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "You don't have enough gold!\nYou need " + (100 - Config.playerGold) + " more gold.", "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                default: // Leave shop
                    appendToLog("You leave the shop.");
                    return;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                         InstantiationException | IllegalAccessException e) {
                    // Use default look and feel if system L&F fails
                    System.out.println("Using default look and feel");
                }
                new MainGUI().setVisible(true);
            }
        });
    }
}
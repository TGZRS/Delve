import javax.swing.*;

public class GameLogic {

    public static void chest(MainGUI gui) {
        String message = "";

        if (Math.random() > 0.8) {
            short playerAttackIncrease = (short)(Math.random() * 15 * Config.difficultyMultiplier);
            byte playerHealthIncrease = (byte)(Math.random() * 25 * Config.difficultyMultiplier);
            Config.playerAttack += playerAttackIncrease;
            Config.maxPlayerHealth += playerHealthIncrease;
            Config.playerHealth += playerHealthIncrease;
            message = "You found a rare reward!\nIt increased your attack by " + playerAttackIncrease
                    + " and your health by " + playerHealthIncrease + "\n\n";
        }

        byte goldReceivedChest = (byte) (Math.random() * 100);
        Config.playerGold += goldReceivedChest;
        message += "You found a Chest!\nYou gained " + goldReceivedChest + " gold!\nYou now have " + Config.playerGold + " gold";

        gui.showMessage("Treasure Chest", message);
        gui.updateStats();
    }

    public static void randomEvent(MainGUI gui) {
        if (Config.roomInstanceCount % 10 == 0) {
            bossEncounter(gui);
        }
        else if (Config.roomInstanceCount % 5 == 0) {
            shopEncounter(gui);
        }
        else {
            byte randomNumber = (byte) Math.ceil((Math.random() * 2));
            switch (randomNumber) {
                case 1:
                    chest(gui);
                    break;
                case 2:
                    monsterEncounter(gui);
                    break;
            }
        }
    }

    public static void monsterEncounter(MainGUI gui) {
        short monsterHealth = (short)(5 * Config.difficultyMultiplier + Math.random() * 10);
        short monsterDamage = (short)(Config.difficultyMultiplier + Math.random() * 10);

        gui.startCombat("Monster", monsterHealth, monsterDamage, false);
    }

    public static void bossEncounter(MainGUI gui) {
        short bossHealth = (short)(Math.round((float)(Config.roomInstanceCount/10)) * 50);
        short bossDamage = (short)(Math.random() * 15 * Math.round((double)(Config.roomInstanceCount/10)));

        gui.startCombat("Boss", bossHealth, bossDamage, true);
    }

    public static void shopEncounter(MainGUI gui) {
        gui.showShop();
    }

    public static void handleCombatResult(MainGUI gui, boolean victory, String enemyType, boolean isBoss) {
        if (victory) {
            short goldReceived;
            if (isBoss) {
                goldReceived = (short)(Math.random() * 400);
                gui.showMessage("Victory!", "You beat the " + enemyType + "!\nYour reward is: " + goldReceived + " gold");
            } else {
                goldReceived = (byte)(Math.random() * 100);
                gui.showMessage("Victory!", "You beat the " + enemyType + "!\nYour reward is: " + goldReceived + " gold");
            }
            Config.playerGold += goldReceived;
            gui.updateStats();
        }
    }

    public static boolean playerAttack() {
        short playerDamageDone = (short) (Math.random() * Config.playerAttack * 2);
        return playerDamageDone > 0;
    }

    public static short calculatePlayerDamage() {
        return (short) (Math.random() * Config.playerAttack * 2);
    }

    public static void takeDamage(short damage) {
        Config.playerHealth -= damage;
        if (Config.playerHealth < 0) {
            Config.playerHealth = 0;
        }
    }

    public static boolean isPlayerAlive() {
        return Config.playerHealth > 0;
    }

    public static void buyHealPotion() {
        if (Config.playerGold >= 100) {
            Config.playerHealth += 25;
            if (Config.playerHealth > Config.maxPlayerHealth) {
                Config.playerHealth = Config.maxPlayerHealth;
            }
            Config.playerGold -= 100;
        }
    }

    public static void buyAttackPotion() {
        if (Config.playerGold >= 100) {
            Config.playerAttack += 10;
            Config.playerGold -= 100;
        }
    }

    public static boolean canAfford(int price) {
        return Config.playerGold >= price;
    }
}
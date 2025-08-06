public class Config {
    public static short roomInstanceCount = 1;
    public static byte difficultyMultiplier = (byte)Math.round((float)((Config.roomInstanceCount/10 + 1)));
    public static short playerGold = 0;
    public static short playerAttack = 10;
    public static short playerHealth = 100;
    public static short maxPlayerHealth = 100;
    public static String playerName;
    public static String releaseVersion = "0.2.0";
}
package org.sam.heartsmod;

public class ClientLivesData {
    private static int playerLives;

    // Set the player's lives on the client
    public static void set(int lives) {
        ClientLivesData.playerLives = lives;
    }

    // Get the player's current lives
    public static int getPlayerLives() {
        return playerLives;
    }
}

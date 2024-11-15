package org.sam.heartsmod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class PlayerEventHandler {

    // Get the player's profile and the server's ban list
    GameProfile playerProfile = player.getGameProfile();
    GameProfileBanList banList = server.getPlayerList().getBans();

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerUUID = player.getUUID();
            Config.PLAYER_LIVES.putIfAbsent(playerUUID, 3);  // Default to 3 lives
            Config.saveConfig();
            sendLivesToClient(player);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSource().getEntity() instanceof ServerPlayer) {
                handlePlayerDeath(player);
            }
        }
    }

    public void handlePlayerDeath(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        int livesRemaining = Config.PLAYER_LIVES.getOrDefault(playerUUID, 3);

        if (livesRemaining > 1) {
            Config.PLAYER_LIVES.put(playerUUID, livesRemaining - 1);
        } else {
            Config.PLAYER_LIVES.put(playerUUID, 0);
            if (isMultiplayerServer()) {
                player.connection.disconnect(Component.literal("You have been banned for losing all lives."));

                // Create a new ban entry and add it to the ban list
                GameProfileBanEntry banEntry = new GameProfileBanEntry(playerProfile, null, "Server", null, "You have been banned for losing all lives.");
                banList.add(banEntry);

            } else {
                player.setGameMode(GameType.SPECTATOR);
            }
        }

        sendLivesToClient(player);  // Update the client with the new lives
        Config.saveConfig();
    }

    private void sendLivesToClient(ServerPlayer player) {
        int livesRemaining = Config.PLAYER_LIVES.getOrDefault(player.getUUID(), 3);
        LivesDataSyncS2CPacket packet = new LivesDataSyncS2CPacket(livesRemaining);
        // Send the packet to the player
        ModMessages.sendToPlayer(packet, player); // Use public method
    }

    private boolean isMultiplayerServer() {
        return net.minecraftforge.fml.loading.FMLEnvironment.dist.isDedicatedServer();
    }
}

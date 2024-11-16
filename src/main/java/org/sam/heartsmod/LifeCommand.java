package org.sam.heartsmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class LifeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("life") // Root command /life
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2)) // Requires permission level 2 (OP)
                        .then(Commands.literal("add") // Subcommand /life add
                                .then(Commands.argument("player", StringArgumentType.word()) // Player name
                                        .executes(context -> handleLifeSubCommand(context, "add"))))
                        .then(Commands.literal("remove") // Subcommand /life remove
                                .then(Commands.argument("player", StringArgumentType.word()) // Player name
                                        .executes(context -> handleLifeSubCommand(context, "remove"))))
        );
    }

    private static int handleLifeSubCommand(CommandContext<CommandSourceStack> context, String action) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        // Get the target player
        ServerPlayer targetPlayer = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (targetPlayer == null) {
            source.sendFailure(Component.literal("Player " + playerName + " not found."));
            return 0;
        }

        UUID playerUUID = targetPlayer.getUUID();
        int currentLives = Config.PLAYER_LIVES.getOrDefault(playerUUID, 3);

        // Modify lives based on the action
        if (action.equals("add")) {
            Config.PLAYER_LIVES.put(playerUUID, currentLives + 1);
            source.sendSuccess(() -> Component.literal("Added a life to " + playerName + ". Current lives: " + (currentLives + 1)), true);
        } else if (action.equals("remove")) {
            if (currentLives > 0) {
                Config.PLAYER_LIVES.put(playerUUID, currentLives - 1);
                source.sendSuccess(() -> Component.literal("Removed a life from " + playerName + ". Current lives: " + (currentLives - 1)), true);
            } else {
                source.sendFailure(Component.literal(playerName + " already has 0 lives."));
                return 0;
            }
        }

        // Save the updated config and sync lives with the target player
        Config.saveConfig();
        ModMessages.sendToPlayer(new LivesDataSyncS2CPacket(Config.PLAYER_LIVES.get(playerUUID)), targetPlayer);

        return 1;
    }
}

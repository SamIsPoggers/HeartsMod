package org.sam.heartsmod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LivesDataSyncC2SPacket {
    private final int lives;

    // Constructor for sending data
    public LivesDataSyncC2SPacket(int lives) {
        this.lives = lives;
    }

    // Constructor for reading data from the buffer (client sends this packet to server)
    public LivesDataSyncC2SPacket(FriendlyByteBuf buf) {
        this.lives = buf.readInt();
    }

    // Write the data to the buffer
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(lives);
    }

    // Handle the packet on the server-side
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Handle the received data here (e.g., update the player's lives)
            // For now, we simply log it or modify some server-side data.
            System.out.println("Received lives data from client: " + lives);

            // You can store it in a map or update the player's state.
            // For example, update the player’s lives data:
            // PlayerLivesData.updateLivesOnServer(lives);
        });
        return true;
    }
}

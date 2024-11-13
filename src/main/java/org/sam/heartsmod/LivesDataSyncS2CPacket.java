package org.sam.heartsmod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LivesDataSyncS2CPacket {
    private final int lives;

    public LivesDataSyncS2CPacket(int lives) {
        this.lives = lives;
    }

    public LivesDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.lives = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(lives);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Handle the received data, for example, setting the number of hearts for the player
            // You could set this value in a client-side class like ClientLivesData
            ClientLivesData.set(lives);
        });
        return true;
    }
}

package org.sam.heartsmod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Heartsmod.MODID)
public class Heartsmod {
    public static final String MODID = "heartsmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Heartsmod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Registering the event handler for player events (login, death, etc.)
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

        // Register ourselves for other server and game events
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Config.loadConfig();  // Load the player lives from config on setup
        ModMessages.register();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting...");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Client setup complete");
        }
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public class ModEventHandlers {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            LifeCommand.register(event.getDispatcher());
        }
    }
}

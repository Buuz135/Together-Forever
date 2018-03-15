package com.buuz135.togetherforever.config;

import com.buuz135.togetherforever.TogetherForever;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = TogetherForever.MOD_ID)
public class TogetherForeverConfig {

    @Config.Comment("If enabled, players in the same team will sync the advancements")
    public static boolean advancementSync = true;

    @Config.Comment("If enabled, players in the same team will sync the gamestages")
    public static boolean gamestagesSync = true;

    @Config.Comment("Amount of seconds the syncing will be delayed on world join")
    @Config.RangeInt(min = 0)
    public static int syncDataSecondsDelay = 20;

    @Mod.EventBusSubscriber(modid = TogetherForever.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(TogetherForever.MOD_ID)) {
                ConfigManager.sync(TogetherForever.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}

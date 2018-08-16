/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

    @Config.Comment("If enabled, players in the same team will sync the levels from Reskillable")
    public static boolean reskillableLevelUpSync = true;

    @Config.Comment("If enabled, players in the same team will sync the unlockables from Reskillable")
    public static boolean reskillableUnlockableSync = true;

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

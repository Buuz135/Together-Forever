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
package com.buuz135.togetherforever;

import com.buuz135.togetherforever.api.*;
import com.buuz135.togetherforever.api.annotation.PlayerInformation;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.buuz135.togetherforever.api.annotation.TogetherTeam;
import com.buuz135.togetherforever.api.command.TogetherForeverCommand;
import com.buuz135.togetherforever.api.data.DataManager;
import com.buuz135.togetherforever.api.data.DefaultPlayerInformation;
import com.buuz135.togetherforever.api.data.TogetherRegistries;
import com.buuz135.togetherforever.command.*;
import com.buuz135.togetherforever.config.TogetherForeverConfig;
import com.buuz135.togetherforever.utils.AnnotationHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Mod(
        modid = TogetherForever.MOD_ID,
        name = TogetherForever.MOD_NAME,
        version = TogetherForever.VERSION,
        dependencies = "required:forge@[14.23.1.2560,);after:gamestages@[2.0.90,);after:reskillable@[1.9.1,);"
)
public class TogetherForever {

    public static final String MOD_ID = "togetherforever";
    public static final String MOD_NAME = "TogetherForever";
    public static final String VERSION = "1.0.2";

    public static Logger LOGGER;

    //Add a command to force sync players
    //Sync other player stuff when player joins
    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static TogetherForever INSTANCE;
    public static HashMap<String, Long> joinedPlayers = new HashMap<>();

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        try {
            registerSyncActions(event.getAsmData());
            registerTogetherTeams(event.getAsmData());
            registerPlayerInformations(event.getAsmData());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        TogetherForeverCommand command = new TogetherForeverCommand(Arrays.asList(new HelpCommand(), new InviteCommand(), new AcceptInviteCommand(), new DeclineInviteCommand(), new TeamKickCommand(), new TeamLeaveCommand(), new TeamInfoCommand(), new ForceSyncCommand()));
        event.registerServerCommand(command);
        event.registerServerCommand(new TogetherForeverDebug());
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP && TogetherForeverAPI.getInstance().getPlayerTeam(event.player.getUniqueID()) != null) {
            joinedPlayers.put(event.player.getName(), System.currentTimeMillis());
            if (TogetherForeverConfig.syncDataSecondsDelay > 0)
                event.player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Syncing team data in " + TogetherForeverConfig.syncDataSecondsDelay + " seconds!"));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        List<String> remove = new ArrayList<>();
        for (String name : joinedPlayers.keySet()) {
            if (System.currentTimeMillis() - joinedPlayers.get(name) >= TogetherForeverConfig.syncDataSecondsDelay * 1000) {
                remove.add(name);
                EntityPlayerMP playerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name);
                if (playerMP != null) {
                    playerMP.sendMessage(new TextComponentString(TextFormatting.GOLD + "Trying to sync team data now!"));
                    DefaultPlayerInformation information = DefaultPlayerInformation.createInformation(playerMP);
                    DataManager manager = TogetherForeverAPI.getInstance().getDataManager(playerMP.world);
                    for (IOfflineSyncRecovery recovery : manager.getRecoveries()) {
                        recovery.recoverMissingPlayer(information);
                    }
                    manager.markDirty();
                }
            }
        }
        for (String s : remove) {
            joinedPlayers.remove(s);
        }
    }

    private void registerSyncActions(ASMDataTable data) throws IllegalAccessException, InstantiationException {
        for (Class<?> aClass : AnnotationHelper.getAnnotatedClasses(data, SyncAction.class)) {
            if (ISyncAction.class.isAssignableFrom(aClass)) {
                SyncAction syncAction = aClass.getAnnotation(SyncAction.class);
                if (syncAction.dependencies().length == 0 || areDependenciesLoaded(syncAction.dependencies())) {
                    Object object = aClass.newInstance();
                    TogetherRegistries.registerSyncAction(syncAction.id(), (ISyncAction<?, ? extends IOfflineSyncRecovery>) object);
                }
            }
        }
    }

    private void registerTogetherTeams(ASMDataTable data) {
        for (Class aClass : AnnotationHelper.getAnnotatedClasses(data, TogetherTeam.class)) {
            if (ITogetherTeam.class.isAssignableFrom(aClass)) {
                TogetherTeam togetherTeam = (TogetherTeam) aClass.getAnnotation(TogetherTeam.class);
                TogetherRegistries.registerTogetherTeam(togetherTeam.id(), aClass);
            }
        }
    }

    private void registerPlayerInformations(ASMDataTable data) {
        for (Class aClass : AnnotationHelper.getAnnotatedClasses(data, PlayerInformation.class)) {
            if (IPlayerInformation.class.isAssignableFrom(aClass)) {
                PlayerInformation playerInformation = (PlayerInformation) aClass.getAnnotation(PlayerInformation.class);
                TogetherRegistries.registerPlayerInformation(playerInformation.id(), aClass);
            }
        }
    }

    private boolean areDependenciesLoaded(String[] deps) {
        for (String dep : deps) {
            if (!Loader.isModLoaded(dep)) return false;
        }
        return true;
    }
}

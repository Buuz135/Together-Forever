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
package com.buuz135.together_forever.api;

import com.buuz135.together_forever.api.data.DataManager;
import com.buuz135.together_forever.api.data.TeamInvite;
import com.buuz135.together_forever.api.event.TeamEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A collection of useful methods of the API
 */
public class TogetherForeverAPI {

    public static final String MOD_ID = "togetherforever";
    public static final String API_VERSION = "2.0";
    public static final String API_ID = MOD_ID + "api";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static TogetherForeverAPI ourInstance = new TogetherForeverAPI();
    private List<TeamInvite> teamInvites;

    private TogetherForeverAPI() {
        teamInvites = new ArrayList<>();
    }

    public static TogetherForeverAPI getInstance() {
        return ourInstance;
    }

    /**
     * Gets the list of teams. Changes done to this list won't be saved
     *
     * @return The list of teams
     */
    public List<ITogetherTeam> getTeams() {
        ServerLevel world = getWorld();
        if (world == null) return new ArrayList<>();
        return getDataManager(world).getTeams();
    }

    /**
     * Adds a team to the API
     *
     * @param togetherTeam The team to add
     */
    public void addTeam(ITogetherTeam togetherTeam) {
        DataManager dataManager = getDataManager(getWorld());
        if (dataManager == null) return;
        TeamEvent.Create create = new TeamEvent.Create(togetherTeam);
        MinecraftForge.EVENT_BUS.post(create);
        if (!create.isCanceled()) {
            dataManager.getTeams().add(create.getTogetherTeam());
            dataManager.setDirty();
        }
    }

    /**
     * Adds a player to a team using the team unique identifiers to search it.
     *
     * @param team              The team to add the player to
     * @param playerInformation The information of the player that needs to be added to the team
     */
    public void addPlayerToTeam(ITogetherTeam team, IPlayerInformation playerInformation) {
        DataManager manager = getDataManager(getWorld());
        if (manager == null) return;
        for (ITogetherTeam togetherTeam : manager.getTeams()) {
            if (togetherTeam.getOwner().equals(team.getOwner()) && togetherTeam.getTeamName().equalsIgnoreCase(team.getTeamName())) {
                TeamEvent.PlayerAdd playerAdd = new TeamEvent.PlayerAdd(togetherTeam, playerInformation);
                MinecraftForge.EVENT_BUS.post(playerAdd);
                if (!playerAdd.isCanceled()) {
                    playerAdd.getTogetherTeam().addPlayer(playerAdd.getPlayerInformation());
                    manager.setDirty();
                }
            }
        }
    }

    /**
     * Removes a player from a team using the team unique identifiers to search it
     *
     * @param team              The team to remove the player from
     * @param playerInformation The information of the player that needs to be removed from the team
     */
    public void removePlayerFromTeam(ITogetherTeam team, IPlayerInformation playerInformation) {
        DataManager manager = getDataManager(getWorld());
        if (manager == null) return;
        for (ITogetherTeam togetherTeam : manager.getTeams()) {
            if (togetherTeam.getOwner().equals(team.getOwner()) && togetherTeam.getTeamName().equalsIgnoreCase(team.getTeamName())) {
                TeamEvent.RemovePlayer removePlayer = new TeamEvent.RemovePlayer(togetherTeam, playerInformation);
                MinecraftForge.EVENT_BUS.post(removePlayer);
                if (!removePlayer.isCanceled()) {
                    removePlayer.getTogetherTeam().removePlayer(removePlayer.getPlayerInformation());
                    manager.setDirty();
                }
            }
        }
    }

    /**
     * Gets the team of a player
     *
     * @param playerUUID The UUID of the player to get the team from
     * @return The team of the player, null if the player isn't in a team
     */
    @Nullable
    public ITogetherTeam getPlayerTeam(UUID playerUUID) {
        for (ITogetherTeam togetherTeam : getTeams()) {
            for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                if (playerInformation.getUUID().equals(playerUUID)) return togetherTeam;
            }
        }
        return null;
    }

    /**
     * Creates an invite to join a team
     *
     * @param sender         The player that sends the invite
     * @param receiver       The player that receives the invite
     * @param announceInvite true if the player that gets the invite needs to get a notification
     * @return The created invite
     */
    public TeamInvite createTeamInvite(IPlayerInformation sender, IPlayerInformation receiver, boolean announceInvite) {
        TeamInvite invite = new TeamInvite(sender, receiver);
        if (announceInvite) {
            MutableComponent accept = Component.literal("[ACCEPT]");
            accept.setStyle(accept.getStyle().applyFormats(ChatFormatting.BOLD, ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tofe accept " + sender.getName())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to accept"))));
            MutableComponent decline = Component.literal("[DECLINE]");
            decline.setStyle(decline.getStyle().applyFormats(ChatFormatting.BOLD, ChatFormatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tofe decline " + sender.getName())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to decline"))));
            receiver.getPlayer().sendSystemMessage(Component.literal("You have been invited to join " + sender.getName() + "'s team. Click ")
                    .append(accept).append(" ").append(decline));
        }
        teamInvites.add(invite);
        return invite;
    }

    /**
     * Adds a player to a OfflineSyncRecovery
     *
     * @param recoveryClass      The class of the OfflineSyncRecovery
     * @param iPlayerInformation The PlayerInformation that need to be stored
     * @param compound           The recovery information that needs to be stored
     */
    public void addPlayerToOfflineRecovery(Class<? extends IOfflineSyncRecovery> recoveryClass, IPlayerInformation iPlayerInformation, CompoundTag compound) {
        DataManager manager = getDataManager(getWorld());
        if (manager == null) return;
        for (IOfflineSyncRecovery recovery : manager.getRecoveries()) {
            if (recovery.getClass().equals(recoveryClass)) {
                recovery.storeMissingPlayer(iPlayerInformation, compound);
                manager.setDirty();
                return;
            }
        }
        try {
            IOfflineSyncRecovery recovery = recoveryClass.newInstance();
            recovery.storeMissingPlayer(iPlayerInformation, compound);
            manager.getRecoveries().add(recovery);
            manager.setDirty();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the generic DataManager
     *
     * @param world A world of to get the DataManager, it can be any world
     * @return the DataManager
     */
    public DataManager getDataManager(ServerLevel world) {
        if (world == null) return null;
        DataManager dataManager = (DataManager) world.getDataStorage().get(compoundTag -> {
            var data = new DataManager();
            data.readFromNBT(compoundTag);
            return data;
        }, DataManager.NAME);
        if (dataManager == null) {
            dataManager = new DataManager();
            world.getDataStorage().set(DataManager.NAME, dataManager);
        }
        return dataManager;
    }

    /**
     * Gets the player entity
     *
     * @param string The name of the player
     * @return The ServerPlayer, null if the player is offline
     */
    @Nullable
    public ServerPlayer getPlayer(String string) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(string);
    }

    /**
     * Gets the player entity
     *
     * @param uuid The UUID of the player
     * @return The ServerPlayer, null if the player is offline
     */
    @Nullable
    public ServerPlayer getPlayer(UUID uuid) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    /**
     * Gets the open team invites
     *
     * @return The list of team invites
     */
    public List<TeamInvite> getTeamInvites() {
        return teamInvites;
    }

    /**
     * Gets the world overworld
     *
     * @return The overworld
     */
    public ServerLevel getWorld() {
        return ServerLifecycleHooks.getCurrentServer().getAllLevels().iterator().next();
    }
}

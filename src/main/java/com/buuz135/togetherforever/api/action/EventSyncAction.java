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
package com.buuz135.togetherforever.api.action;

import com.buuz135.togetherforever.api.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Represent a SyncAction based in a event
 *
 * @param <T> The {@link PlayerEvent} type of the SyncAction
 * @param <S> The {@link IOfflineSyncRecovery} type of the SyncAction
 */
public abstract class EventSyncAction<T extends PlayerEvent, S extends IOfflineSyncRecovery> implements ISyncAction<T, S> {

    private final Class<S> recovery;
    private final Class<T> eventClass;

    /**
     * Default constructor of the class
     *
     * @param eventClass      The Event class of the SyncAction that will get automatically registered
     * @param offlineRecovery The Offline Recovery class for the this SyncAction
     */
    public EventSyncAction(Class<T> eventClass, Class<S> offlineRecovery) {
        this.recovery = offlineRecovery;
        this.eventClass = eventClass;
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Triggers the event, gets the Player of the PlayerEvent, gets the {@link ITogetherTeam} from the Player,
     * triggers the SyncAction for all the team and stores all players offline in the OfflineSyncRecovery for later use
     *
     * @param event The event with same class as T
     */
    @SubscribeEvent(receiveCanceled = true)
    public void onEvent(T event) {
        if (!event.getClass().equals(eventClass)) return;
        TogetherForeverAPI.LOGGER.debug("Triggering event class: " + event.getClass().toString());
        if (TogetherForeverAPI.getInstance().getWorld() == null) return;
        TogetherForeverAPI.LOGGER.debug("World is not null");
        ITogetherTeam team = TogetherForeverAPI.getInstance().getPlayerTeam(event.getEntityPlayer().getUniqueID());
        if (team != null) {
            TogetherForeverAPI.LOGGER.debug("Found team: " + team.getTeamName() + ". Contains this players:");
            team.getPlayers().forEach(iPlayerInformation -> TogetherForeverAPI.LOGGER.debug(iPlayerInformation.getUUID().toString() + ':' + iPlayerInformation.getName()));
            TogetherForeverAPI.LOGGER.debug("Starting to trigger sync");
            List<IPlayerInformation> playerLeft = triggerSync(event, team);
            TogetherForeverAPI.LOGGER.debug("Sync triggered with " + playerLeft.size() + " players not being synced:");
            NBTTagCompound compound = transformEventToNBT(event);
            for (IPlayerInformation information : playerLeft) {
                TogetherForeverAPI.LOGGER.debug(information.getUUID().toString() + ':' + information.getName());
                TogetherForeverAPI.getInstance().addPlayerToOfflineRecovery(recovery, information, compound);
            }
        }
    }

    /**
     * Getter of the IOfflineRecovery
     *
     * @return The class
     */
    @Override
    public Class<S> getOfflineRecovery() {
        return recovery;
    }

    /**
     * Transforms an event into a NBTTagCompound so it can be stored in the world for the IOfflineSyncRecovery
     *
     * @param event The event to transform
     * @return The transformed NBTTagCompound of the Event
     */
    public abstract NBTTagCompound transformEventToNBT(T event);

}

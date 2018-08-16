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
package com.buuz135.togetherforever.action;

import com.buuz135.togetherforever.action.recovery.GameStageOfflineRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.action.EventSyncAction;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.buuz135.togetherforever.config.TogetherForeverConfig;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SyncAction(id = "gamestage_event_sync", dependencies = {"gamestages"})
public class GameStagesEventSyncAction extends EventSyncAction<GameStageEvent.Add, GameStageOfflineRecovery> {

    private ListMultimap<EntityPlayerMP, String> stageUnlocks;

    public GameStagesEventSyncAction() {
        super(GameStageEvent.Add.class, GameStageOfflineRecovery.class);
        this.stageUnlocks = ArrayListMultimap.create();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public NBTTagCompound transformEventToNBT(GameStageEvent.Add event) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Stage", event.getStageName());
        return compound;
    }

    public static void unlockPlayerStage(EntityPlayerMP playerMP, String stage) {
        if (!GameStageHelper.hasStage(playerMP, stage)) {
            MinecraftServer server = playerMP.getServer();
            if (server != null) {
                GameStageHelper.addStage(playerMP, stage);
            }
        }
    }

    @Override
    public List<IPlayerInformation> triggerSync(GameStageEvent.Add object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        if (!TogetherForeverConfig.gamestagesSync) return playerInformations;
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
            if (playerMP == null) playerInformations.add(information);
            else {
                if (!GameStageHelper.hasStage(playerMP, object.getStageName()) && !stageUnlocks.containsEntry(playerMP, object.getStageName()))
                    stageUnlocks.put(playerMP, object.getStageName());
            }
        }
        return playerInformations;
    }

    @Override
    public void syncJoinPlayer(IPlayerInformation toBeSynced, IPlayerInformation teamMember) {
        if (teamMember.getPlayer() != null && toBeSynced.getPlayer() != null) {
            for (String s : GameStageHelper.getPlayerData(teamMember.getPlayer()).getStages()) {
                unlockPlayerStage(toBeSynced.getPlayer(), s);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Map.Entry<EntityPlayerMP, String> entityPlayerMPStringEntry : stageUnlocks.entries()) {
            unlockPlayerStage(entityPlayerMPStringEntry.getKey(), entityPlayerMPStringEntry.getValue());
        }
        stageUnlocks.clear();
    }
}

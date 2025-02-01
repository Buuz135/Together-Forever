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
package com.buuz135.together_forever.action;


import com.buuz135.together_forever.TogetherForever;
import com.buuz135.together_forever.action.recovery.AdvancementOfflineRecovery;
import com.buuz135.together_forever.api.IPlayerInformation;
import com.buuz135.together_forever.api.ITogetherTeam;
import com.buuz135.together_forever.api.action.EventSyncAction;
import com.buuz135.together_forever.api.annotation.SyncAction;
import com.buuz135.together_forever.config.TogetherForeverConfig;
import net.minecraft.advancements.Advancement;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import java.util.ArrayList;
import java.util.List;

@SyncAction(id = "advancement_event_sync")
public class AdvancementEventSyncAction extends EventSyncAction<AdvancementEvent.AdvancementEarnEvent, AdvancementOfflineRecovery> {

    public AdvancementEventSyncAction() {
        super(AdvancementEvent.AdvancementEarnEvent.class, AdvancementOfflineRecovery.class);
    }

    public static void grantAllParentAchievements(ServerPlayer player, Advancement advancement) {
        if (advancement.getParent() != null) grantAllParentAchievements(player, advancement.getParent());
        TogetherForever.LOGGER.debug("Advancement granting: " + advancement.getId().toString());
        for (String string : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) {
            player.getAdvancements().award(advancement, string);
        }
    }

    @Override
    public List<IPlayerInformation> triggerSync(AdvancementEvent.AdvancementEarnEvent object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        TogetherForever.LOGGER.debug("Starting to sync advancement " + object.getAdvancement().getId().toString());
        if (!TogetherForeverConfig.advancementSync.get()) return playerInformations;
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            ServerPlayer playerMP = information.getPlayer();
            if (playerMP == null) {
                TogetherForever.LOGGER.debug(information.getName() + " is not online, adding it to the offline recovery!");
                playerInformations.add(information);
            } else {
                TogetherForever.LOGGER.debug("Trying to grant all the parent advancements to the player " + information.getName());
                grantAllParentAchievements(playerMP, object.getAdvancement());
            }
        }
        return playerInformations;
    }

    @Override
    public void syncJoinPlayer(IPlayerInformation toBeSynced, IPlayerInformation teamMember) {
        if (!TogetherForeverConfig.advancementSync.get()) return;
        if (teamMember.getPlayer() != null && toBeSynced.getPlayer() != null) {
            ServerPlayer member = teamMember.getPlayer();
            ServerPlayer sync = toBeSynced.getPlayer();
            if (member.level() instanceof ServerLevel serverLevel) {
                for (Advancement advancement : serverLevel.getServer().getAdvancements().getAllAdvancements()) {
                    for (String crit : member.getAdvancements().getOrStartProgress(advancement).getCompletedCriteria()) {
                        sync.getAdvancements().award(advancement, crit);
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag transformEventToNBT(AdvancementEvent.AdvancementEarnEvent event) {
        CompoundTag compound = new CompoundTag();
        compound.putString("AdvancementId", event.getAdvancement().getId().toString());
        return compound;
    }
}

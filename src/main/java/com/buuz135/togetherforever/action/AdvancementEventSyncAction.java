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

import com.buuz135.togetherforever.TogetherForever;
import com.buuz135.togetherforever.action.recovery.AdvancementOfflineRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.action.EventSyncAction;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.buuz135.togetherforever.config.TogetherForeverConfig;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import java.util.ArrayList;
import java.util.List;

@SyncAction(id = "advancement_event_sync")
public class AdvancementEventSyncAction extends EventSyncAction<AdvancementEvent, AdvancementOfflineRecovery> {

    public AdvancementEventSyncAction() {
        super(AdvancementEvent.class, AdvancementOfflineRecovery.class);
    }

    public static void grantAllParentAchievements(EntityPlayerMP player, Advancement advancement) {
        if (advancement.getParent() != null) grantAllParentAchievements(player, advancement.getParent());
        TogetherForever.LOGGER.debug("Advancement granting: " + advancement.getId().toString());
        for (String string : player.getAdvancements().getProgress(advancement).getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(advancement, string);
        }
    }

    @Override
    public List<IPlayerInformation> triggerSync(AdvancementEvent object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        TogetherForever.LOGGER.debug("Starting to sync advancement " + object.getAdvancement().getId().toString());
        if (!TogetherForeverConfig.advancementSync) return playerInformations;
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
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
        if (teamMember.getPlayer() != null && toBeSynced.getPlayer() != null) {
            EntityPlayerMP member = teamMember.getPlayer();
            EntityPlayerMP sync = toBeSynced.getPlayer();
            for (Advancement advancement : member.getServerWorld().getAdvancementManager().getAdvancements()) {
                for (String crit : member.getAdvancements().getProgress(advancement).getCompletedCriteria()) {
                    sync.getAdvancements().grantCriterion(advancement, crit);
                }
            }
        }
    }

    @Override
    public NBTTagCompound transformEventToNBT(AdvancementEvent event) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("AdvancementId", event.getAdvancement().getId().toString());
        return compound;
    }
}

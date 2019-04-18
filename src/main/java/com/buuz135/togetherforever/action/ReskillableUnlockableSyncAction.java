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

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.event.UnlockUnlockableEvent;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.requirement.TraitRequirement;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.toast.ToastHelper;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import com.buuz135.togetherforever.action.recovery.ReskillableUnlockableOfflineRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.action.EventSyncAction;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.buuz135.togetherforever.config.TogetherForeverConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

@SyncAction(id = "reskillable_unlockable_event_sync", dependencies = {"reskillable"})
public class ReskillableUnlockableSyncAction extends EventSyncAction<UnlockUnlockableEvent.Post, ReskillableUnlockableOfflineRecovery> {

    public ReskillableUnlockableSyncAction() {
        super(UnlockUnlockableEvent.Post.class, ReskillableUnlockableOfflineRecovery.class);
    }

    @Override
    public NBTTagCompound transformEventToNBT(UnlockUnlockableEvent.Post event) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("Unlock", event.getUnlockable().getRegistryName().toString());
        return tagCompound;
    }

    @Override
    public List<IPlayerInformation> triggerSync(UnlockUnlockableEvent.Post object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        if (!TogetherForeverConfig.reskillableUnlockableSync) return playerInformations;
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
            if (playerMP == null) playerInformations.add(information);
            else {
                if (playerMP.getUniqueID().equals(object.getEntityPlayer().getUniqueID())) continue;
                PlayerData data = PlayerDataHandler.get(playerMP);
                Unlockable unlockable = object.getUnlockable();
                PlayerSkillInfo skillInfo = data.getSkillInfo(unlockable.getParentSkill());
                if (!skillInfo.isUnlocked(unlockable)) {
                    skillInfo.unlock(unlockable, playerMP);
                    data.saveAndSync();
                    RequirementCache.invalidateCache(information.getUUID(), TraitRequirement.class);
                    ToastHelper.sendUnlockableToast(information.getPlayer(), unlockable);
                }
            }
        }
        return playerInformations;
    }

    @Override
    public void syncJoinPlayer(IPlayerInformation toBeSynced, IPlayerInformation teamMember) {
        if (!TogetherForeverConfig.reskillableUnlockableSync) return;
        if (toBeSynced.getPlayer() != null && teamMember.getPlayer() != null) {
            PlayerData origin = PlayerDataHandler.get(teamMember.getPlayer());
            PlayerData sync = PlayerDataHandler.get(toBeSynced.getPlayer());
            boolean changed = false;
            for (Skill skill : ReskillableRegistries.SKILLS.getValuesCollection()) {
                PlayerSkillInfo originSkillInfo = origin.getSkillInfo(skill);
                PlayerSkillInfo syncSkillInfo = sync.getSkillInfo(skill);
                for (Unlockable unlockable : skill.getUnlockables()) {
                    if (originSkillInfo.isUnlocked(unlockable) && !syncSkillInfo.isUnlocked(unlockable)) {
                        syncSkillInfo.unlock(unlockable, toBeSynced.getPlayer());
                        changed = true;
                        ToastHelper.sendUnlockableToast(toBeSynced.getPlayer(), unlockable);
                    }
                }
            }
            if (changed) {
                sync.saveAndSync();
                RequirementCache.invalidateCache(toBeSynced.getUUID(), TraitRequirement.class);
            }
            origin.saveAndSync();
        }
    }
}

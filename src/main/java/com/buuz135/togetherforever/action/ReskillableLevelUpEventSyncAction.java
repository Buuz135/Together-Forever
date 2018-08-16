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
import codersafterdark.reskillable.api.event.LevelUpEvent;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.requirement.SkillRequirement;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.toast.ToastHelper;
import com.buuz135.togetherforever.action.recovery.ReskillableLevelUpOfflineRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.action.EventSyncAction;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.buuz135.togetherforever.config.TogetherForeverConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

@SyncAction(id = "reskillable_level_up_event_sync", dependencies = {"reskillable"})
public class ReskillableLevelUpEventSyncAction extends EventSyncAction<LevelUpEvent.Post, ReskillableLevelUpOfflineRecovery> {

    public ReskillableLevelUpEventSyncAction() {
        super(LevelUpEvent.Post.class, ReskillableLevelUpOfflineRecovery.class);
    }

    @Override
    public NBTTagCompound transformEventToNBT(LevelUpEvent.Post event) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("Skill", event.getSkill().getRegistryName().toString());
        tagCompound.setInteger("NewLevel", event.getLevel());
        return tagCompound;
    }

    @Override
    public List<IPlayerInformation> triggerSync(LevelUpEvent.Post object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        if (!TogetherForeverConfig.reskillableLevelUpSync) return playerInformations;
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
            if (playerMP == null) playerInformations.add(information);
            else {
                if (playerMP.getUniqueID().equals(object.getEntityPlayer().getUniqueID())) continue;
                PlayerData data = PlayerDataHandler.get(playerMP);
                Skill skill = object.getSkill();
                PlayerSkillInfo skillInfo = data.getSkillInfo(skill);
                int level = object.getLevel();
                if (skillInfo.getLevel() < level) {
                    skillInfo.setLevel(level);
                    data.saveAndSync();
                    RequirementCache.invalidateCache(information.getUUID(), SkillRequirement.class);
                    ToastHelper.sendSkillToast(playerMP, skill, level);
                }
            }
        }
        return playerInformations;
    }

    @Override
    public void syncJoinPlayer(IPlayerInformation toBeSynced, IPlayerInformation teamMember) {
        if (toBeSynced.getPlayer() != null && teamMember.getPlayer() != null) {
            PlayerData origin = PlayerDataHandler.get(teamMember.getPlayer());
            PlayerData sync = PlayerDataHandler.get(toBeSynced.getPlayer());
            boolean changed = false;
            for (Skill skill : ReskillableRegistries.SKILLS.getValuesCollection()) {
                int otherLevel = origin.getSkillInfo(skill).getLevel();
                PlayerSkillInfo skillInfo = sync.getSkillInfo(skill);
                if (skillInfo.getLevel() < otherLevel) {
                    skillInfo.setLevel(otherLevel);
                    changed = true;
                    ToastHelper.sendSkillToast(toBeSynced.getPlayer(), skill, otherLevel);
                }
            }
            if (changed) {
                sync.saveAndSync();
                RequirementCache.invalidateCache(toBeSynced.getUUID(), SkillRequirement.class);
            }
            origin.saveAndSync();
        }
    }
}

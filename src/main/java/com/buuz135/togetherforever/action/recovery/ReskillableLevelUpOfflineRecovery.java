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
package com.buuz135.togetherforever.action.recovery;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.requirement.SkillRequirement;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.toast.ToastHelper;
import com.buuz135.togetherforever.api.IPlayerInformation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReskillableLevelUpOfflineRecovery extends AbstractOfflineRecovery {
    public ReskillableLevelUpOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(IPlayerInformation playerInformation) {
        PlayerData data = PlayerDataHandler.get(playerInformation.getPlayer());
        if (data == null) {
            return;
        }
        Map<Skill, Integer> levelUps = new HashMap<>();
        List<Map.Entry<IPlayerInformation, NBTTagCompound>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                NBTTagCompound value = entry.getValue();
                String skillID = value.getString("Skill");
                Skill skill = ReskillableRegistries.SKILLS.getValue(new ResourceLocation(skillID));
                if (skill != null) {
                    PlayerSkillInfo skillInfo = data.getSkillInfo(skill);
                    int newLevel = value.getInteger("NewLevel");
                    if (newLevel == 0) { //Backwards compat with old nbt storage
                        skillInfo.levelUp();
                        levelUps.put(skill, skillInfo.getLevel());
                    } else if (skillInfo.getLevel() < newLevel) {
                        skillInfo.setLevel(newLevel);
                        levelUps.put(skill, newLevel);
                    }
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
        if (!levelUps.isEmpty()) {
            data.saveAndSync();
            RequirementCache.invalidateCache(playerInformation.getUUID(), SkillRequirement.class);
            levelUps.forEach((skill, integer) -> ToastHelper.sendSkillToast(playerInformation.getPlayer(), skill, integer));
        }
    }
}
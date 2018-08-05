package com.buuz135.togetherforever.action.recovery;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.requirement.SkillRequirement;
import codersafterdark.reskillable.api.skill.Skill;
import com.buuz135.togetherforever.api.IPlayerInformation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReskillableLevelUpOfflineRecovery extends AbstractOfflineRecovery {
    public ReskillableLevelUpOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(IPlayerInformation playerInformation) {
        List<Map.Entry<IPlayerInformation, NBTTagCompound>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                NBTTagCompound value = entry.getValue();
                String skillID = value.getString("Skill");
                Skill skill = ReskillableRegistries.SKILLS.getValue(new ResourceLocation(skillID));
                if (skill != null) {
                    PlayerData data = PlayerDataHandler.get(playerInformation.getPlayer());
                    PlayerSkillInfo skillInfo = data.getSkillInfo(skill);
                    int newLevel = value.getInteger("NewLevel");
                    if (newLevel == 0) { //Backwards compat with old nbt storage
                        skillInfo.levelUp();
                        data.saveAndSync();
                        RequirementCache.invalidateCache(playerInformation.getUUID(), SkillRequirement.class);
                    } else if (skillInfo.getLevel() < newLevel) {
                        skillInfo.setLevel(newLevel);
                        data.saveAndSync();
                        RequirementCache.invalidateCache(playerInformation.getUUID(), SkillRequirement.class);
                    }
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
    }
}
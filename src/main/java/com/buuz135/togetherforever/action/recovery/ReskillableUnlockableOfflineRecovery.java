package com.buuz135.togetherforever.action.recovery;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.requirement.TraitRequirement;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import com.buuz135.togetherforever.api.IPlayerInformation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReskillableUnlockableOfflineRecovery extends AbstractOfflineRecovery {
    public ReskillableUnlockableOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(IPlayerInformation playerInformation) {
        PlayerData data = PlayerDataHandler.get(playerInformation.getPlayer());
        if (data == null) {
            return;
        }
        boolean changed = false;
        List<Map.Entry<IPlayerInformation, NBTTagCompound>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                String skillID = entry.getValue().getString("Unlock");
                Unlockable unlockable = ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation(skillID));
                if (unlockable != null) {
                    PlayerSkillInfo skillInfo = data.getSkillInfo(unlockable.getParentSkill());
                    if (!skillInfo.isUnlocked(unlockable)) {
                        skillInfo.unlock(unlockable, playerInformation.getPlayer());
                        changed = true;
                    }
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
        if (changed) {
            data.saveAndSync();
            RequirementCache.invalidateCache(playerInformation.getUUID(), TraitRequirement.class);
        }
    }
}
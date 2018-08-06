package com.buuz135.togetherforever.action;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.event.UnlockUnlockableEvent;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.requirement.TraitRequirement;
import codersafterdark.reskillable.api.skill.Skill;
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
                PlayerSkillInfo skillInfo = data.getSkillInfo(object.getUnlockable().getParentSkill());
                if (!skillInfo.isUnlocked(object.getUnlockable())) {
                    skillInfo.unlock(object.getUnlockable(), playerMP);
                    data.saveAndSync();
                    RequirementCache.invalidateCache(information.getUUID(), TraitRequirement.class);
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
                PlayerSkillInfo originSkillInfo = origin.getSkillInfo(skill);
                PlayerSkillInfo syncSkillInfo = sync.getSkillInfo(skill);
                for (Unlockable unlockable : skill.getUnlockables()) {
                    if (originSkillInfo.isUnlocked(unlockable) && !syncSkillInfo.isUnlocked(unlockable)) {
                        syncSkillInfo.unlock(unlockable, toBeSynced.getPlayer());
                        changed = true;
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

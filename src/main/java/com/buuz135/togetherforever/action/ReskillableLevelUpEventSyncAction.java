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

package com.buuz135.togetherforever.action;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.event.LevelUpEvent;
import codersafterdark.reskillable.api.skill.Skill;
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
                while (data.getSkillInfo(object.getSkill()).getLevel() < object.getLevel()) {
                    data.getSkillInfo(object.getSkill()).levelUp();
                }
                data.saveAndSync();
            }
        }
        return playerInformations;
    }

    @Override
    public void syncJoinPlayer(IPlayerInformation toBeSynced, IPlayerInformation teamMember) {
        if (toBeSynced.getPlayer() != null && teamMember.getPlayer() != null) {
            PlayerData origin = PlayerDataHandler.get(teamMember.getPlayer());
            PlayerData sync = PlayerDataHandler.get(toBeSynced.getPlayer());
            for (Skill skill : ReskillableRegistries.SKILLS.getValuesCollection()) {
                while (sync.getSkillInfo(skill).getLevel() < origin.getSkillInfo(skill).getLevel()) {
                    sync.getSkillInfo(skill).levelUp();
                }
            }
            sync.saveAndSync();
            origin.saveAndSync();
        }
    }
}
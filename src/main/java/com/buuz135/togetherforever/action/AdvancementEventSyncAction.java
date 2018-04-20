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
        TogetherForever.LOGGER.warn("Advancement granting: " + advancement.getId().toString());
        for (String string : player.getAdvancements().getProgress(advancement).getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(advancement, string);
        }
    }

    @Override
    public List<IPlayerInformation> triggerSync(AdvancementEvent object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        TogetherForever.LOGGER.warn("Starting to sync advancement " + object.getAdvancement().getId().toString());
        if (!TogetherForeverConfig.advancementSync) return playerInformations;
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
            if (playerMP == null) {
                TogetherForever.LOGGER.warn(information.getName() + " is not online, adding it to the offline recovery!");
                playerInformations.add(information);
            }
            else {
                TogetherForever.LOGGER.warn("Trying to grant all the parent advancements to the player " + information.getName());
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

package com.buuz135.togetherforever.action;

import com.buuz135.togetherforever.action.recovery.AdvancementOfflineRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.action.EventSyncAction;
import com.buuz135.togetherforever.api.annotation.SyncAction;
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
        for (String string : player.getAdvancements().getProgress(advancement).getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(advancement, string);
        }
    }

    @Override
    public List<IPlayerInformation> triggerSync(AdvancementEvent object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
            if (playerMP == null) playerInformations.add(information);
            else {
                grantAllParentAchievements(playerMP, object.getAdvancement());
            }
        }
        return playerInformations;
    }

    @Override
    public NBTTagCompound transformEventToNBT(AdvancementEvent event) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("AdvancementId", event.getAdvancement().getId().toString());
        return compound;
    }
}

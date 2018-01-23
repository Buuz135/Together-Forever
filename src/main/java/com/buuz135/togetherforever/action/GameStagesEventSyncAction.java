package com.buuz135.togetherforever.action;

import com.buuz135.togetherforever.action.recovery.GameStageOfflineRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.action.EventSyncAction;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SyncAction(id = "gamestage_event_sync", dependencies = {"gamestages"})
public class GameStagesEventSyncAction extends EventSyncAction<GameStageEvent.Add, GameStageOfflineRecovery> {

    private ListMultimap<EntityPlayerMP, String> stageUnlocks;

    public GameStagesEventSyncAction() {
        super(GameStageEvent.Add.class, GameStageOfflineRecovery.class);
        this.stageUnlocks = ArrayListMultimap.create();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public NBTTagCompound transformEventToNBT(GameStageEvent.Add event) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Stage", event.getStageName());
        return compound;
    }

    @Override
    public List<IPlayerInformation> triggerSync(GameStageEvent.Add object, ITogetherTeam togetherTeam) {
        List<IPlayerInformation> playerInformations = new ArrayList<>();
        for (IPlayerInformation information : togetherTeam.getPlayers()) {
            EntityPlayerMP playerMP = information.getPlayer();
            if (playerMP == null) playerInformations.add(information);
            else {
                if (!PlayerDataHandler.getStageData(playerMP).hasUnlockedStage(object.getStageName()) && !stageUnlocks.containsEntry(playerMP, object.getStageName()))
                    stageUnlocks.put(playerMP, object.getStageName());
            }
        }
        return playerInformations;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Map.Entry<EntityPlayerMP, String> entityPlayerMPStringEntry : stageUnlocks.entries()) {
            if (!PlayerDataHandler.getStageData(entityPlayerMPStringEntry.getKey()).hasUnlockedStage(entityPlayerMPStringEntry.getValue())) {
                PlayerDataHandler.getStageData(entityPlayerMPStringEntry.getKey()).unlockStage(entityPlayerMPStringEntry.getValue());
                entityPlayerMPStringEntry.getKey().sendMessage(new TextComponentString("You unlocked stage " + entityPlayerMPStringEntry.getValue() + "!"));
            }
        }
        stageUnlocks.clear();
    }
}

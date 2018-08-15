package com.buuz135.togetherforever.action.recovery;

import com.buuz135.togetherforever.action.GameStagesEventSyncAction;
import com.buuz135.togetherforever.api.IPlayerInformation;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStageOfflineRecovery extends AbstractOfflineRecovery {
    public GameStageOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(IPlayerInformation playerInformation) {
        List<Map.Entry<IPlayerInformation, NBTTagCompound>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                String stage = entry.getValue().getString("Stage");
                if (playerInformation.getPlayer() != null && !GameStageHelper.hasStage(playerInformation.getPlayer(), stage)) {
                    GameStagesEventSyncAction.unlockPlayerStage(playerInformation.getPlayer(), stage);
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
    }
}

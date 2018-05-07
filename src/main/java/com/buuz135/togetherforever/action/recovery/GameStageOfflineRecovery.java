package com.buuz135.togetherforever.action.recovery;

import com.buuz135.togetherforever.action.GameStagesEventSyncAction;
import com.buuz135.togetherforever.api.IOfflineSyncRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.data.TogetherRegistries;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStageOfflineRecovery implements IOfflineSyncRecovery {

    private ListMultimap<IPlayerInformation, NBTTagCompound> offlineRecoveries;

    public GameStageOfflineRecovery() {
        this.offlineRecoveries = ArrayListMultimap.create();
    }

    @Override
    public void storeMissingPlayers(List<IPlayerInformation> playerInformations, NBTTagCompound store) {
        for (IPlayerInformation playerInformation : playerInformations) {
            storeMissingPlayer(playerInformation, store);
        }
    }

    @Override
    public void storeMissingPlayer(IPlayerInformation playerInformation, NBTTagCompound store) {
        offlineRecoveries.put(playerInformation, store);
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

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        for (IPlayerInformation playerInformation : offlineRecoveries.keySet()) {
            String uuid = playerInformation.getUUID().toString();
            NBTTagCompound recovery = new NBTTagCompound();
            recovery.setTag("ID", playerInformation.getNBTTag());
            recovery.setString("PlayerID", TogetherRegistries.getPlayerInformationID(playerInformation.getClass()));
            int id = 0;
            for (NBTTagCompound compound : offlineRecoveries.get(playerInformation)) {
                recovery.setTag(id + "", compound);
                ++id;
            }
            tagCompound.setTag(uuid, recovery);
        }
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        offlineRecoveries.clear();
        for (String uuid : compound.getKeySet()) {
            NBTTagCompound recovery = compound.getCompoundTag(uuid);
            Class plClass = TogetherRegistries.getPlayerInformationClass(recovery.getString("PlayerID"));
            if (plClass != null) {
                try {
                    IPlayerInformation info = (IPlayerInformation) plClass.newInstance();
                    info.readFromNBT(recovery.getCompoundTag("ID"));
                    for (String id : recovery.getKeySet()) {
                        if (!id.equalsIgnoreCase("ID") && !id.equalsIgnoreCase("PlayerID")) {
                            offlineRecoveries.put(info, recovery.getCompoundTag(id));
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

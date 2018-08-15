package com.buuz135.togetherforever.action.recovery;

import com.buuz135.togetherforever.api.IOfflineSyncRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.data.TogetherRegistries;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public abstract class AbstractOfflineRecovery implements IOfflineSyncRecovery {
    protected ListMultimap<IPlayerInformation, NBTTagCompound> offlineRecoveries;

    public AbstractOfflineRecovery() {
        this.offlineRecoveries = ArrayListMultimap.create();
    }

    @Override
    public void storeMissingPlayers(List<IPlayerInformation> playersInformation, NBTTagCompound store) {
        for (IPlayerInformation playerInformation : playersInformation) {
            storeMissingPlayer(playerInformation, store);
        }
    }

    @Override
    public void storeMissingPlayer(IPlayerInformation playerInformation, NBTTagCompound store) {
        offlineRecoveries.put(playerInformation, store);
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
                recovery.setTag(Integer.toString(id), compound);
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
            Class<? extends IPlayerInformation> plClass = TogetherRegistries.getPlayerInformationClass(recovery.getString("PlayerID"));
            if (plClass != null) {
                try {
                    IPlayerInformation info = plClass.newInstance();
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
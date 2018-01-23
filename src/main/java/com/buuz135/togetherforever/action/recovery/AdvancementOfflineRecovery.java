package com.buuz135.togetherforever.action.recovery;

import com.buuz135.togetherforever.action.AdvancementEventSyncAction;
import com.buuz135.togetherforever.api.IOfflineSyncRecovery;
import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.data.TogetherRegistries;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancementOfflineRecovery implements IOfflineSyncRecovery {

    private ListMultimap<IPlayerInformation, NBTTagCompound> offlineRecoveries;

    public AdvancementOfflineRecovery() {
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
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : offlineRecoveries.entries()) {
            if (entry.getKey().equals(playerInformation)) {
                ResourceLocation location = new ResourceLocation(entry.getValue().getString("AdvancementId"));
                Advancement advancement = FMLServerHandler.instance().getServer().getAdvancementManager().getAdvancement(location);
                if (advancement != null) {
                    AdvancementEventSyncAction.grantAllParentAchievements(playerInformation.getPlayer(), advancement);
                    removeList.add(entry);
                }
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

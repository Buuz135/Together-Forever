package com.buuz135.togetherforever.action.recovery;

import com.buuz135.togetherforever.action.AdvancementEventSyncAction;
import com.buuz135.togetherforever.api.IPlayerInformation;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancementOfflineRecovery extends AbstractOfflineRecovery {
    public AdvancementOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(IPlayerInformation playerInformation) {
        List<Map.Entry<IPlayerInformation, NBTTagCompound>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                ResourceLocation location = new ResourceLocation(entry.getValue().getString("AdvancementId"));
                Advancement advancement = FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancement(location);
                if (advancement != null) {
                    AdvancementEventSyncAction.grantAllParentAchievements(playerInformation.getPlayer(), advancement);
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, NBTTagCompound> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
    }
}
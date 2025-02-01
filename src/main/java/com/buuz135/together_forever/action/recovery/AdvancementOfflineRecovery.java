/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.together_forever.action.recovery;


import com.buuz135.together_forever.action.AdvancementEventSyncAction;
import com.buuz135.together_forever.api.IPlayerInformation;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.ServerLifecycleHooks;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancementOfflineRecovery extends AbstractOfflineRecovery {
    public AdvancementOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(IPlayerInformation playerInformation) {
        List<Map.Entry<IPlayerInformation, CompoundTag>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, CompoundTag> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                ResourceLocation location = new ResourceLocation(entry.getValue().getString("AdvancementId"));
                Advancement advancement = ServerLifecycleHooks.getCurrentServer().getAdvancements().getAdvancement(location);
                if (advancement != null) {
                    AdvancementEventSyncAction.grantAllParentAchievements(playerInformation.getPlayer(), advancement);
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, CompoundTag> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
    }
}
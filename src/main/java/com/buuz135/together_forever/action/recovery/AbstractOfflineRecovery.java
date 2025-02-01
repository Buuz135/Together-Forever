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


import com.buuz135.together_forever.api.IOfflineSyncRecovery;
import com.buuz135.together_forever.api.IPlayerInformation;
import com.buuz135.together_forever.api.data.TogetherRegistries;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.nbt.CompoundTag;


import java.util.List;

public abstract class AbstractOfflineRecovery implements IOfflineSyncRecovery {
    protected ListMultimap<IPlayerInformation, CompoundTag> offlineRecoveries;

    public AbstractOfflineRecovery() {
        this.offlineRecoveries = ArrayListMultimap.create();
    }

    @Override
    public void storeMissingPlayers(List<IPlayerInformation> playersInformation, CompoundTag store) {
        for (IPlayerInformation playerInformation : playersInformation) {
            storeMissingPlayer(playerInformation, store);
        }
    }

    @Override
    public void storeMissingPlayer(IPlayerInformation playerInformation, CompoundTag store) {
        offlineRecoveries.put(playerInformation, store);
    }

    @Override
    public CompoundTag writeToNBT() {
        CompoundTag tagCompound = new CompoundTag();
        for (IPlayerInformation playerInformation : offlineRecoveries.keySet()) {
            String uuid = playerInformation.getUUID().toString();
            CompoundTag recovery = new CompoundTag();
            recovery.put("ID", playerInformation.getNBTTag());
            recovery.putString("PlayerID", TogetherRegistries.getPlayerInformationID(playerInformation.getClass()));
            int id = 0;
            for (CompoundTag compound : offlineRecoveries.get(playerInformation)) {
                recovery.put(Integer.toString(id), compound);
                ++id;
            }
            tagCompound.put(uuid, recovery);
        }
        return tagCompound;
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        offlineRecoveries.clear();
        for (String uuid : compound.getAllKeys()) {
            CompoundTag recovery = compound.getCompound(uuid);
            Class<? extends IPlayerInformation> plClass = TogetherRegistries.getPlayerInformationClass(recovery.getString("PlayerID"));
            if (plClass != null) {
                try {
                    IPlayerInformation info = plClass.newInstance();
                    info.readFromNBT(recovery.getCompound("ID"));
                    for (String id : recovery.getAllKeys()) {
                        if (!id.equalsIgnoreCase("ID") && !id.equalsIgnoreCase("PlayerID")) {
                            offlineRecoveries.put(info, recovery.getCompound(id));
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
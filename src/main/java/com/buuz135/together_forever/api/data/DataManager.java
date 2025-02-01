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
package com.buuz135.together_forever.api.data;

import com.buuz135.together_forever.api.IOfflineSyncRecovery;
import com.buuz135.together_forever.api.ISyncAction;
import com.buuz135.together_forever.api.ITogetherTeam;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManager extends SavedData {

    public static final String NAME = "TogetherForever";
    public static final String TEAM = "Teams";
    public static final String RECOVERY = "Recovery";

    private List<ITogetherTeam> teams;
    private List<IOfflineSyncRecovery> recoveries;

    public DataManager() {
        super();
        teams = new ArrayList<>();
        recoveries = new ArrayList<>();
    }

    public void readFromNBT(@Nonnull CompoundTag nbt) {
        teams = new ArrayList<>();
        //
        CompoundTag raw = nbt.getCompound(NAME);
        //TEAM READING
        CompoundTag teamCompound = raw.getCompound(TEAM);
        for (String teamNames : teamCompound.getAllKeys()) {
            CompoundTag team = teamCompound.getCompound(teamNames);
            String teamID = team.getString("TeamID");
            Class<? extends ITogetherTeam> aClass = TogetherRegistries.getTogetherTeamClass(teamID);
            if (aClass != null) {
                try {
                    ITogetherTeam togetherTeam = aClass.newInstance();
                    togetherTeam.readFromNBT(team.getCompound("Value"));
                    teams.add(togetherTeam);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //OFFLINE RECOVERY READING
        CompoundTag offlineRecovery = raw.getCompound(RECOVERY);
        for (String key : offlineRecovery.getAllKeys()) {
            ISyncAction<?, ? extends IOfflineSyncRecovery> action = TogetherRegistries.getSyncActionFromID(key.split("-")[0]);
            if (action != null) {
                try {
                    IOfflineSyncRecovery recovery = action.getOfflineRecovery().newInstance();
                    recovery.readFromNBT(offlineRecovery.getCompound(key));
                    recoveries.add(recovery);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        CompoundTag custom = new CompoundTag();
        //TEAM SAVING
        CompoundTag teamCompound = new CompoundTag();
        for (ITogetherTeam togetherTeam : teams) {
            CompoundTag team = new CompoundTag();
            team.putString("TeamID", TogetherRegistries.getTogetherTeamID(togetherTeam.getClass()));
            team.put("Value", togetherTeam.getNBTTag());
            teamCompound.put(togetherTeam.getTeamName(), team);
        }
        custom.put(TEAM, teamCompound);
        //OFFLINE RECOVERY SAVING
        CompoundTag offlineRecovery = new CompoundTag();
        for (IOfflineSyncRecovery recovery : recoveries) {
            offlineRecovery.put(TogetherRegistries.getSyncActionIdFromOfflineRecovery(recovery) +"-"+ UUID.randomUUID(), recovery.writeToNBT());
        }
        custom.put(RECOVERY, offlineRecovery);

        compound.put(NAME, custom);
        return compound;
    }

    public List<ITogetherTeam> getTeams() {
        return teams;
    }

    public List<IOfflineSyncRecovery> getRecoveries() {
        return recoveries;
    }
}

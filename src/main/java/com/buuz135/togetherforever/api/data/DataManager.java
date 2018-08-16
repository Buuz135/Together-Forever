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
package com.buuz135.togetherforever.api.data;

import com.buuz135.togetherforever.api.IOfflineSyncRecovery;
import com.buuz135.togetherforever.api.ISyncAction;
import com.buuz135.togetherforever.api.ITogetherTeam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DataManager extends WorldSavedData {

    public static final String NAME = "TogetherForever";
    public static final String TEAM = "Teams";
    public static final String RECOVERY = "Recovery";

    private List<ITogetherTeam> teams;
    private List<IOfflineSyncRecovery> recoveries;

    public DataManager(String string) {
        super(string);
        teams = new ArrayList<>();
        recoveries = new ArrayList<>();
    }

    public DataManager() {
        this(NAME);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        teams = new ArrayList<>();
        //
        NBTTagCompound raw = nbt.getCompoundTag(NAME);
        //TEAM READING
        NBTTagCompound teamCompound = raw.getCompoundTag(TEAM);
        for (String teamNames : teamCompound.getKeySet()) {
            NBTTagCompound team = teamCompound.getCompoundTag(teamNames);
            String teamID = team.getString("TeamID");
            Class<? extends ITogetherTeam> aClass = TogetherRegistries.getTogetherTeamClass(teamID);
            if (aClass != null) {
                try {
                    ITogetherTeam togetherTeam = aClass.newInstance();
                    togetherTeam.readFromNBT(team.getCompoundTag("Value"));
                    teams.add(togetherTeam);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //OFFLINE RECOVERY READING
        NBTTagCompound offlineRecovery = nbt.getCompoundTag(RECOVERY);
        for (String key : offlineRecovery.getKeySet()) {
            ISyncAction<?, ? extends IOfflineSyncRecovery> action = TogetherRegistries.getSyncActionFromID(key);
            if (action != null) {
                try {
                    IOfflineSyncRecovery recovery = action.getOfflineRecovery().newInstance();
                    recovery.readFromNBT(offlineRecovery.getCompoundTag(key));
                    recoveries.add(recovery);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound custom = new NBTTagCompound();
        //TEAM SAVING
        NBTTagCompound teamCompound = new NBTTagCompound();
        for (ITogetherTeam togetherTeam : teams) {
            NBTTagCompound team = new NBTTagCompound();
            team.setString("TeamID", TogetherRegistries.getTogetherTeamID(togetherTeam.getClass()));
            team.setTag("Value", togetherTeam.getNBTTag());
            teamCompound.setTag(togetherTeam.getTeamName(), team);
        }
        custom.setTag(TEAM, teamCompound);
        //OFFLINE RECOVERY SAVING
        NBTTagCompound offlineRecovery = new NBTTagCompound();
        for (IOfflineSyncRecovery recovery : recoveries) {
            offlineRecovery.setTag(TogetherRegistries.getSyncActionIdFromOfflineRecovery(recovery), recovery.writeToNBT());
        }
        custom.setTag(RECOVERY, offlineRecovery);

        compound.setTag(NAME, custom);
        return compound;
    }

    public List<ITogetherTeam> getTeams() {
        return teams;
    }

    public List<IOfflineSyncRecovery> getRecoveries() {
        return recoveries;
    }
}

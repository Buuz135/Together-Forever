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

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.annotation.TogetherTeam;
import com.buuz135.togetherforever.utils.TeamHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@TogetherTeam(id = "default_together_team")
public class DefaultTogetherTeam implements ITogetherTeam {

    private String teamName;
    private UUID owner;
    private List<IPlayerInformation> playersInformation;

    public DefaultTogetherTeam() {
        playersInformation = new ArrayList<>();
    }

    @Override
    public void addPlayer(IPlayerInformation playerInformation) {
        if (teamName == null) {
            teamName = playerInformation.getName();
            owner = playerInformation.getUUID();
        }
        if (!playersInformation.contains(playerInformation)) {
            playersInformation.add(playerInformation);
        }
    }

    @Override
    public void removePlayer(IPlayerInformation playerInformation) {
        playersInformation.remove(playerInformation);
    }

    @Override
    public void removePlayer(UUID playerUUID) {
        IPlayerInformation information = TeamHelper.findPlayerInfo(playersInformation, playerUUID);
        if (information != null) playersInformation.remove(information);
    }

    @Override
    public Collection<IPlayerInformation> getPlayers() {
        return playersInformation;
    }

    @Override
    public NBTTagCompound getNBTTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Name", teamName);
        compound.setString("Owner", owner.toString());
        for (IPlayerInformation information : playersInformation) {
            NBTTagCompound informationCompound = new NBTTagCompound();
            informationCompound.setString("PlayerID", TogetherRegistries.getPlayerInformationID(information.getClass()));
            informationCompound.setTag("Value", information.getNBTTag());
            compound.setTag(information.getUUID().toString(), informationCompound);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        teamName = compound.getString("Name");
        owner = UUID.fromString(compound.getString("Owner"));
        for (String key : compound.getKeySet()) {
            if (key.equalsIgnoreCase("Name")) continue;
            NBTTagCompound informationCompound = compound.getCompoundTag(key);
            Class<? extends IPlayerInformation> plClass = TogetherRegistries.getPlayerInformationClass(informationCompound.getString("PlayerID"));
            if (plClass != null) {
                try {
                    IPlayerInformation info = plClass.newInstance();
                    info.readFromNBT(informationCompound.getCompoundTag("Value"));
                    playersInformation.add(info);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }
}

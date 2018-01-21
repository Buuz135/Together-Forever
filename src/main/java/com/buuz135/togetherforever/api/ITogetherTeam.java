package com.buuz135.togetherforever.api;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.UUID;

public interface ITogetherTeam {

    void addPlayer(IPlayerInformation playerInformation);

    void removePlayer(IPlayerInformation playerInformation);

    void removePlayer(UUID playerUUID);

    Collection<IPlayerInformation> getPlayers();

    NBTTagCompound getNBTTag();

    void readFromNBT(NBTTagCompound compound);

    String getTeamName();

    UUID getOwner();
}

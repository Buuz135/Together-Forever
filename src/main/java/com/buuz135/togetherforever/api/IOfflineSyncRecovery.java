package com.buuz135.togetherforever.api;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface IOfflineSyncRecovery {

    void storeMissingPlayers(List<IPlayerInformation> playerInformations, NBTTagCompound store);

    void storeMissingPlayer(IPlayerInformation playerInformation, NBTTagCompound store);

    void recoverMissingPlayer(IPlayerInformation playerInformation);

    NBTTagCompound writeToNBT();

    void readFromNBT(NBTTagCompound compound);
}

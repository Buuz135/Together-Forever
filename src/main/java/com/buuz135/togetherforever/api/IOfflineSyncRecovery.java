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
package com.buuz135.togetherforever.api;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Represent a recovery system for the players that were offline in a team when the SyncAction happened
 */
public interface IOfflineSyncRecovery {

    /**
     * Stores a list of players into the system
     *
     * @param playersInformation The List of players that will need recovery
     * @param store              The NBT necessary to do a recovery when possible
     */
    void storeMissingPlayers(List<IPlayerInformation> playersInformation, NBTTagCompound store);

    /**
     * Stores a player into the system
     *
     * @param playerInformation The Player that will need recovery
     * @param store             The NBT necessary to do a recovery when possible
     */
    void storeMissingPlayer(IPlayerInformation playerInformation, NBTTagCompound store);

    /**
     * Syncs all the actions that a player missed when he was offline
     *
     * @param playerInformation The Player information that needs a recovery
     */
    void recoverMissingPlayer(IPlayerInformation playerInformation);

    /**
     * Transforms the OfflineSyncRecovery into NBT to be stored in the world so it can be saved properly
     *
     * @return The OfflineSyncRecovery transformed into NBT
     */
    NBTTagCompound writeToNBT();

    /**
     * Reads all the information stored as NBT into the OfflineSyncRecovery
     *
     * @param compound The OfflineSyncRecovery previously stored as NBT
     */
    void readFromNBT(NBTTagCompound compound);
}

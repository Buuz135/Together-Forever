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

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a team. THIS CLASS NEEDS A CONSTRUCTOR WITHOUT PARAMETERS!
 * {@code getTeamName} and {@code getOwner} form an unique identifier of the team
 */
public interface ITogetherTeam {

    /**
     * Adds a player to the team
     *
     * @param playerInformation The PlayerInformation of the player
     */
    void addPlayer(IPlayerInformation playerInformation);

    /**
     * Removes a player from the team
     *
     * @param playerInformation The PlayerInformation of the player
     */
    void removePlayer(IPlayerInformation playerInformation);

    /**
     * Removes a player from the team
     *
     * @param playerUUID The UUID of the player
     */
    void removePlayer(UUID playerUUID);

    /**
     * Gets a collection of the players in the team
     *
     * @return A Collection of player of the team
     */
    Collection<IPlayerInformation> getPlayers();

    /**
     * Transforms a a team into NBT so it can be stored in the world
     *
     * @return The team transformed into nbt
     */
    NBTTagCompound getNBTTag();

    /**
     * Reads NBT the were previously stored into the team
     *
     * @param compound The NBT previously stored
     */
    void readFromNBT(NBTTagCompound compound);

    /**
     * Gets the team name
     *
     * @return The team name
     */
    String getTeamName();

    /**
     * Gets the UUID of the owner
     *
     * @return The UUID of the owner of the team
     */
    UUID getOwner();
}

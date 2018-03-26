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

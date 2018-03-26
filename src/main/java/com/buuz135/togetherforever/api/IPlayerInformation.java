package com.buuz135.togetherforever.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents basic information of a Player. THIS CLASS NEEDS A CONSTRUCTOR WITHOUT PARAMETERS!
 */
public interface IPlayerInformation {

    /**
     * Gets the UUID of a player
     *
     * @return The UUID of a player
     */
    UUID getUUID();

    /**
     * Sets the UUID of information
     *
     * @param uuid The UUID of the information
     */
    void setUUID(UUID uuid);

    /**
     * Gets the Name of the player
     *
     * @return The Name og the player
     */
    String getName();

    /**
     * Sets the name of information
     *
     * @param name The name of the information
     */
    void setName(String name);

    /**
     * Transforms the information into NBT
     *
     * @return The Information as NBT
     */
    NBTTagCompound getNBTTag();

    /**
     * Reads the information from NBT stored previously
     *
     * @param compound The information as NBT
     */
    void readFromNBT(NBTTagCompound compound);

    /**
     * Gets the entity player of this information
     *
     * @return The EntityPlayerMP of this information, null if it is offline
     */
    @Nullable
    EntityPlayerMP getPlayer();

}

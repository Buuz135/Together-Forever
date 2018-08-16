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

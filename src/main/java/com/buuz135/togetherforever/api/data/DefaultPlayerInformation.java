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
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.annotation.PlayerInformation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.UUID;

@PlayerInformation(id = "default_player_information")
public class DefaultPlayerInformation implements IPlayerInformation {

    private String name;
    private UUID uuid;

    public static DefaultPlayerInformation createInformation(EntityPlayerMP playerMP) {
        DefaultPlayerInformation info = new DefaultPlayerInformation();
        info.setName(playerMP.getName());
        info.setUUID(playerMP.getUniqueID());
        return info;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public NBTTagCompound getNBTTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Name", name);
        compound.setString("Id", uuid.toString());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        name = compound.getString("Name");
        uuid = UUID.fromString(compound.getString("Id"));
    }


    @Nullable
    @Override
    public EntityPlayerMP getPlayer() {
        return TogetherForeverAPI.getInstance().getPlayer(uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPlayerInformation) return this.uuid.equals(((IPlayerInformation) obj).getUUID());
        return false;
    }
}

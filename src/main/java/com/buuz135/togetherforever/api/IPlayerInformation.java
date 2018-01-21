package com.buuz135.togetherforever.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IPlayerInformation {

    UUID getUUID();

    void setUUID(UUID uuid);

    String getName();

    void setName(String name);

    NBTTagCompound getNBTTag();

    void readFromNBT(NBTTagCompound compound);

    @Nullable
    EntityPlayerMP getPlayer();

}

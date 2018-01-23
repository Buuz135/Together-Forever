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

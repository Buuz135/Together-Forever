package com.buuz135.togetherforever.data;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.annotation.TogetherTeam;
import com.buuz135.togetherforever.utils.TeamHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@TogetherTeam(id = "default_together_team")
public class DefaultTogetherTeam implements ITogetherTeam {

    private String teamName;
    private UUID owner;
    private List<IPlayerInformation> playersInformation;

    public DefaultTogetherTeam() {
        playersInformation = new ArrayList<>();
    }

    @Override
    public void addPlayer(IPlayerInformation playerInformation) {
        if (teamName == null) {
            teamName = playerInformation.getName();
            owner = playerInformation.getUUID();
        }
        if (!playersInformation.contains(playerInformation)) {
            playersInformation.add(playerInformation);
        }
    }

    @Override
    public void removePlayer(IPlayerInformation playerInformation) {
        if (playersInformation.contains(playerInformation)) {
            playersInformation.remove(playerInformation);
        }
    }

    @Override
    public void removePlayer(UUID playerUUID) {
        IPlayerInformation information = TeamHelper.findPlayerInfo(playersInformation, playerUUID);
        if (information != null) playersInformation.remove(information);
    }

    @Override
    public Collection<IPlayerInformation> getPlayers() {
        return playersInformation;
    }

    @Override
    public NBTTagCompound getNBTTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Name", teamName);
        compound.setString("Owner", owner.toString());
        for (IPlayerInformation information : playersInformation) {
            NBTTagCompound informationCompund = new NBTTagCompound();
            informationCompund.setString("PlayerID", TogetherRegistries.getPlayerInformationID(information.getClass()));
            informationCompund.setTag("Value", information.getNBTTag());
            compound.setTag(information.getUUID().toString(), informationCompund);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        teamName = compound.getString("Name");
        owner = UUID.fromString(compound.getString("Owner"));
        for (String key : compound.getKeySet()) {
            if (key.equalsIgnoreCase("Name")) continue;
            NBTTagCompound informationCompound = compound.getCompoundTag(key);
            Class plClass = TogetherRegistries.getPlayerInformationClass(informationCompound.getString("PlayerID"));
            if (plClass != null) {
                try {
                    IPlayerInformation info = (IPlayerInformation) plClass.newInstance();
                    info.readFromNBT(informationCompound.getCompoundTag("Value"));
                    playersInformation.add(info);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }
}

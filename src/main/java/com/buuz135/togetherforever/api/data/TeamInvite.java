package com.buuz135.togetherforever.api.data;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import net.minecraft.util.text.TextComponentString;

public class TeamInvite {

    private final IPlayerInformation sender;
    private final IPlayerInformation reciever;
    private long createdTime;

    public TeamInvite(IPlayerInformation sender, IPlayerInformation reciever) {
        this.sender = sender;
        this.reciever = reciever;
        this.createdTime = System.currentTimeMillis();
    }

    public IPlayerInformation getSender() {
        return sender;
    }

    public IPlayerInformation getReciever() {
        return reciever;
    }

    public void acceptInvite(boolean announce) {
        ITogetherTeam team = TogetherForeverAPI.getInstance().getPlayerTeam(sender.getUUID());
        if (team == null) {
            team = new DefaultTogetherTeam();
            team.addPlayer(sender);
            TogetherForeverAPI.getInstance().addTeam(team);
        }
        if (announce) {
            for (IPlayerInformation info : team.getPlayers()) {
                if (info.getPlayer() != null)
                    info.getPlayer().sendMessage(new TextComponentString(reciever.getName() + " has joined your team."));
            }
            if (reciever.getPlayer() != null)
                reciever.getPlayer().sendMessage(new TextComponentString("You have joined " + sender.getName() + "'s team."));
        }
        TogetherForeverAPI.getInstance().addPlayerToTeam(team, reciever);
    }
}

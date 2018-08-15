package com.buuz135.togetherforever.api.data;

import com.buuz135.togetherforever.api.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TeamInvite {

    private final IPlayerInformation sender;
    private final IPlayerInformation receiver;
    private long createdTime;

    public TeamInvite(IPlayerInformation sender, IPlayerInformation receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.createdTime = System.currentTimeMillis();
    }

    public IPlayerInformation getSender() {
        return sender;
    }

    public IPlayerInformation getReciever() {
        return receiver;
    }

    public void acceptInvite(boolean announce, boolean syncActions) {
        ITogetherTeam team = TogetherForeverAPI.getInstance().getPlayerTeam(sender.getUUID());
        if (team == null) {
            team = new DefaultTogetherTeam();
            team.addPlayer(sender);
            TogetherForeverAPI.getInstance().addTeam(team);
        }
        if (announce) {
            for (IPlayerInformation info : team.getPlayers()) {
                if (info.getPlayer() != null)
                    info.getPlayer().sendMessage(new TextComponentString(TextFormatting.GREEN + receiver.getName() + " has joined your team."));
            }
            if (receiver.getPlayer() != null)
                receiver.getPlayer().sendMessage(new TextComponentString(TextFormatting.GREEN + "You have joined " + sender.getName() + "'s team."));
        }
        if (syncActions) {
            for (ISyncAction<?, ? extends IOfflineSyncRecovery> action : TogetherRegistries.getSyncActions()) {
                action.syncJoinPlayer(receiver, sender);
            }
        }
        TogetherForeverAPI.getInstance().addPlayerToTeam(team, receiver);
    }
}

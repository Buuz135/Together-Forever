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

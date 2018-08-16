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
package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.api.data.DefaultPlayerInformation;
import com.buuz135.togetherforever.api.data.TeamInvite;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class DeclineInviteCommand extends SubCommandAction {

    public DeclineInviteCommand() {
        super("decline");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length > 1) {
            try {
                EntityPlayerMP inviteReceiver = CommandBase.getCommandSenderAsPlayer(sender);
                EntityPlayerMP inviteSender = TogetherForeverAPI.getInstance().getPlayer(args[1]);
                if (inviteSender != null && inviteReceiver != inviteSender) {
                    IPlayerInformation infoReceiver = DefaultPlayerInformation.createInformation(inviteReceiver);
                    IPlayerInformation infoSender = DefaultPlayerInformation.createInformation(inviteSender);
                    for (TeamInvite invite : TogetherForeverAPI.getInstance().getTeamInvites()) {
                        if (invite.getSender().equals(infoSender) && invite.getReciever().equals(infoReceiver)) {
                            inviteReceiver.sendMessage(new TextComponentString(TextFormatting.RED + "You have declined the invite."));
                            inviteSender.sendMessage(new TextComponentString(TextFormatting.RED + inviteReceiver.getName() + " has declined the invite!"));
                            TogetherForeverAPI.getInstance().getTeamInvites().remove(invite);
                            return true;
                        }
                    }
                }
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't find a team invite for that player!"));
            } catch (PlayerNotFoundException e) {
                sender.sendMessage(new TextComponentTranslation(e.getLocalizedMessage(), e.getErrorObjects()));
            }
        }
        return false;
    }

    @Override
    public String getUsage() {
        return "<player_name>";
    }

    @Override
    public String getInfo() {
        return "Declines an invite from a player";
    }
}

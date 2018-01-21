package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.TeamInvite;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.data.DefaultPlayerInformation;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class DeclineInviteCommand extends SubCommandAction {

    public DeclineInviteCommand() {
        super("decline");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            try {
                EntityPlayerMP inviteReciever = CommandBase.getCommandSenderAsPlayer(sender);
                EntityPlayerMP inviteSender = TogetherForeverAPI.getInstance().getPlayer(args[1]);
                if (inviteSender != null && inviteReciever != inviteSender) {
                    IPlayerInformation infoReciever = DefaultPlayerInformation.createInformation(inviteReciever);
                    IPlayerInformation infoSender = DefaultPlayerInformation.createInformation(inviteSender);
                    for (TeamInvite invite : TogetherForeverAPI.getInstance().getTeamInvites()) {
                        if (invite.getSender().equals(infoSender) && invite.getReciever().equals(infoReciever)) {
                            inviteReciever.sendMessage(new TextComponentString("You have declined the invite."));
                            inviteSender.sendMessage(new TextComponentString(inviteReciever.getName() + " has declined the invite!"));
                            TogetherForeverAPI.getInstance().getTeamInvites().remove(invite);
                            break;
                        }
                    }
                }
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

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

public class AcceptInviteCommand extends SubCommandAction {

    public AcceptInviteCommand() {
        super("accept");
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
                            invite.acceptInvite(true);
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

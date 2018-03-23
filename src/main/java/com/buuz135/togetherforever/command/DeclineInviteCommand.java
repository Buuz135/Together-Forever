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
                EntityPlayerMP inviteReciever = CommandBase.getCommandSenderAsPlayer(sender);
                EntityPlayerMP inviteSender = TogetherForeverAPI.getInstance().getPlayer(args[1]);
                if (inviteSender != null && inviteReciever != inviteSender) {
                    IPlayerInformation infoReciever = DefaultPlayerInformation.createInformation(inviteReciever);
                    IPlayerInformation infoSender = DefaultPlayerInformation.createInformation(inviteSender);
                    for (TeamInvite invite : TogetherForeverAPI.getInstance().getTeamInvites()) {
                        if (invite.getSender().equals(infoSender) && invite.getReciever().equals(infoReciever)) {
                            inviteReciever.sendMessage(new TextComponentString(TextFormatting.RED + "You have declined the invite."));
                            inviteSender.sendMessage(new TextComponentString(TextFormatting.RED + inviteReciever.getName() + " has declined the invite!"));
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

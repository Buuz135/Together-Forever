package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.api.data.DefaultPlayerInformation;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class InviteCommand extends SubCommandAction {

    public InviteCommand() {
        super("invite");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length > 1) {
            try {
                EntityPlayerMP senderMP = CommandBase.getCommandSenderAsPlayer(sender);
                EntityPlayerMP invited = CommandBase.getPlayer(server, sender, args[1]);
                if (TogetherForeverAPI.getInstance().getPlayerTeam(invited.getUniqueID()) != null) {
                    senderMP.sendMessage(new TextComponentString(TextFormatting.RED + "The invited player already has a team!"));
                    return false;
                }
                if (senderMP != invited) {
                    TogetherForeverAPI.getInstance().createTeamInvite(DefaultPlayerInformation.createInformation(senderMP), DefaultPlayerInformation.createInformation(invited), true);
                    return true;
                }
            } catch (CommandException e) {
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
        return "Invites a player to your team, if you don't have a team it will be created";
    }
}

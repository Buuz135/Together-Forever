package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TeamKickCommand extends SubCommandAction {

    public TeamKickCommand() {
        super("kick");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length > 1) {
            try {
                EntityPlayerMP commandSender = CommandBase.getCommandSenderAsPlayer(sender);
                ITogetherTeam teamSender = TogetherForeverAPI.getInstance().getPlayerTeam(commandSender.getUniqueID());
                if (teamSender == null) {
                    commandSender.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have a team!"));
                    return false;
                }
                if (!teamSender.getOwner().equals(commandSender.getUniqueID())) {
                    commandSender.sendMessage(new TextComponentString(TextFormatting.RED + "You can't do that! You are not the owner of the team!"));
                    return false;
                }
                for (IPlayerInformation playerInformation : teamSender.getPlayers()) {
                    if (playerInformation.getName().equalsIgnoreCase(args[1])) {
                        TogetherForeverAPI.getInstance().removePlayerFromTeam(teamSender, playerInformation);
                        if (playerInformation.getPlayer() != null) {
                            playerInformation.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You have been removed from " + teamSender.getTeamName() + "'s team."));
                        }
                        commandSender.sendMessage(new TextComponentString(TextFormatting.RED + "You successfully removed " + args[1] + " from your team"));
                        return true;
                    }
                }
                commandSender.sendMessage(new TextComponentString(TextFormatting.RED + "That player is not in your team!"));
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
}

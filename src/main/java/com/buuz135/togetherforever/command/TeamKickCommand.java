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

public class TeamKickCommand extends SubCommandAction {

    public TeamKickCommand() {
        super("kick");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            try {
                EntityPlayerMP commandSender = CommandBase.getCommandSenderAsPlayer(sender);
                ITogetherTeam teamSender = TogetherForeverAPI.getInstance().getPlayerTeam(commandSender.getUniqueID());
                if (teamSender != null) {
                    if (!teamSender.getOwner().equals(commandSender.getUniqueID()))
                        commandSender.sendMessage(new TextComponentString("You can't do that! You are not the owner of the team!"));
                    for (IPlayerInformation playerInformation : teamSender.getPlayers()) {
                        if (playerInformation.getName().equalsIgnoreCase(args[1])) {
                            TogetherForeverAPI.getInstance().removePlayerFromTeam(teamSender, playerInformation);
                            if (playerInformation.getPlayer() != null) {
                                playerInformation.getPlayer().sendMessage(new TextComponentString("You have been removed from " + teamSender.getTeamName() + "'s team."));
                            }
                            commandSender.sendMessage(new TextComponentString("You successfully removed " + args[1] + " from your team"));
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

package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.api.data.DefaultPlayerInformation;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TeamLeaveCommand extends SubCommandAction {

    public TeamLeaveCommand() {
        super("leave");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            ITogetherTeam togetherTeam = TogetherForeverAPI.getInstance().getPlayerTeam(CommandBase.getCommandSenderAsPlayer(sender).getUniqueID());
            if (togetherTeam != null) {
                TogetherForeverAPI.getInstance().removePlayerFromTeam(togetherTeam, DefaultPlayerInformation.createInformation(CommandBase.getCommandSenderAsPlayer(sender)));
                CommandBase.getCommandSenderAsPlayer(sender).sendMessage(new TextComponentString(TextFormatting.GREEN + "You successfully left your team."));
                for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                    EntityPlayerMP playerMP = playerInformation.getPlayer();
                    if (playerMP != null) {
                        playerMP.sendMessage(new TextComponentString(TextFormatting.RED + "Player " + sender.getName() + " left your team."));
                    }
                }
                return true;
            }
        } catch (PlayerNotFoundException e) {
            sender.sendMessage(new TextComponentTranslation(e.getLocalizedMessage(), e.getErrorObjects()));
        }
        return false;
    }

    @Override
    public String getUsage() {
        return "<player_name>";
    }
}

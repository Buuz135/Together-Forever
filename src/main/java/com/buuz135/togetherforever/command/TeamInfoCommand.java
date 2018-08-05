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

public class TeamInfoCommand extends SubCommandAction {

    public TeamInfoCommand() {
        super("info");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            EntityPlayerMP senderMP = CommandBase.getCommandSenderAsPlayer(sender);
            ITogetherTeam togetherTeam = TogetherForeverAPI.getInstance().getPlayerTeam(senderMP.getUniqueID());
            if (togetherTeam == null) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have a team!"));
                return false;
            }
            sender.sendMessage(new TextComponentString(togetherTeam.getTeamName() + " team information:"));
            for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                sender.sendMessage(new TextComponentString(getFormatedName(playerInformation, playerInformation.getPlayer() != null, playerInformation.getUUID().equals(togetherTeam.getOwner()), senderMP.getUniqueID().equals(playerInformation.getUUID()))));
            }
            return true;
        } catch (PlayerNotFoundException e) {
            sender.sendMessage(new TextComponentTranslation(e.getLocalizedMessage(), e.getErrorObjects()));
        }
        return false;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getInfo() {
        return "Gets the players of your team";
    }

    private String getFormatedName(IPlayerInformation playerInformation, boolean online, boolean owner, boolean yourself) {
        StringBuilder builder = new StringBuilder(" - ");
        if (yourself) builder.append(TextFormatting.GOLD).append('[');
        builder.append(online ? TextFormatting.GREEN : TextFormatting.RED).append(playerInformation.getName());
        if (yourself) builder.append(TextFormatting.GOLD).append(']');
        if (owner) builder.append(TextFormatting.DARK_RED).append('*');
        return builder.toString();
    }
}

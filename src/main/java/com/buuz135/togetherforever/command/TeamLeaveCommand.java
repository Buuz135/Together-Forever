package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.data.DefaultPlayerInformation;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class TeamLeaveCommand extends SubCommandAction {

    public TeamLeaveCommand() {
        super("leave");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            ITogetherTeam togetherTeam = TogetherForeverAPI.getInstance().getPlayerTeam(CommandBase.getCommandSenderAsPlayer(sender).getUniqueID());
            if (togetherTeam != null) {
                TogetherForeverAPI.getInstance().removePlayerFromTeam(togetherTeam, DefaultPlayerInformation.createInformation(CommandBase.getCommandSenderAsPlayer(sender)));
                CommandBase.getCommandSenderAsPlayer(sender).sendMessage(new TextComponentString("You successfully left your team."));
            }
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
    }
}

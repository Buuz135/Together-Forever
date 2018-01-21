package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.data.DefaultPlayerInformation;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class InviteCommand extends SubCommandAction {

    public InviteCommand() {
        super("invite");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            try {
                EntityPlayerMP senderMP = CommandBase.getCommandSenderAsPlayer(sender);
                EntityPlayerMP invited = CommandBase.getPlayer(server, sender, args[1]);
                if (senderMP != invited && TogetherForeverAPI.getInstance().getPlayerTeam(invited.getUniqueID()) == null) { //TODO test if invited has team
                    TogetherForeverAPI.getInstance().createTeamInvite(DefaultPlayerInformation.createInformation(senderMP), DefaultPlayerInformation.createInformation(invited), true);
                }
            } catch (CommandException e) {
                e.printStackTrace();
            }
        }
    }
}

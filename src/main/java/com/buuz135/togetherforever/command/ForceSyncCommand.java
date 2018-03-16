package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ISyncAction;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.api.data.DefaultPlayerInformation;
import com.buuz135.togetherforever.api.data.TogetherRegistries;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class ForceSyncCommand extends SubCommandAction {

    public ForceSyncCommand() {
        super("forcesync");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            EntityPlayerMP senderPlayer = CommandBase.getCommandSenderAsPlayer(sender);
            ITogetherTeam togetherTeam = TogetherForeverAPI.getInstance().getPlayerTeam(senderPlayer.getUniqueID());
            if (togetherTeam != null) {
                for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                    EntityPlayerMP playerMP = playerInformation.getPlayer();
                    if (playerMP != null) {
                        for (ISyncAction action : TogetherRegistries.getSyncActions()) {
                            action.syncJoinPlayer(DefaultPlayerInformation.createInformation(senderPlayer), playerInformation);
                        }
                    }
                }
            }
        } catch (PlayerNotFoundException e) {
            sender.sendMessage(new TextComponentTranslation(e.getLocalizedMessage(), e.getErrorObjects()));
        }
        return false;
    }

    @Override
    public String getUsage() {
        return "";
    }
}

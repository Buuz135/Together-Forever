package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.TogetherForever;
import com.buuz135.togetherforever.api.TogetherForeverAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class TogetherForeverDebug extends CommandBase {
    @Override
    public String getName() {
        return "tfdebug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "tfdebug";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TogetherForever.LOGGER.info(TogetherForeverAPI.getInstance().getDataManager(server.getWorld(0)).writeToNBT(new NBTTagCompound()));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        try {
            return server.getPlayerList().getOppedPlayers().getPermissionLevel(CommandBase.getCommandSenderAsPlayer(sender).getGameProfile()) >= 4;
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}

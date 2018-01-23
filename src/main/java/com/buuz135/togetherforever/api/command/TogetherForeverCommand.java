package com.buuz135.togetherforever.api.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TogetherForeverCommand extends CommandBase {

    private final List<SubCommandAction> subCommandActions;

    public TogetherForeverCommand(List<SubCommandAction> subCommandActions) {
        this.subCommandActions = subCommandActions;
    }


    @Override
    public String getName() {
        return "togetherforever";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("tf", "together");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            for (SubCommandAction action : subCommandActions) {
                if (action.getSubCommandName().equalsIgnoreCase(args[0])) {
                    action.execute(server, sender, args);
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return subCommandActions.stream().map(SubCommandAction::getSubCommandName).collect(Collectors.toList());
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("decline") || args[0].equalsIgnoreCase("kick")) {
                return FMLServerHandler.instance().getServer().getPlayerList().getPlayers().stream().map(EntityPlayer::getName).collect(Collectors.toList());
            }
        }
        return Arrays.asList();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}

package com.buuz135.togetherforever.api.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TogetherForeverCommand extends CommandBase {

    public static TogetherForeverCommand command;

    private final List<SubCommandAction> subCommandActions;

    public TogetherForeverCommand(List<SubCommandAction> subCommandActions) {
        this.subCommandActions = subCommandActions;
        command = this;
    }

    @Nonnull
    @Override
    public String getName() {
        return "togetherforever";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        StringBuilder builder = new StringBuilder("Usage: /tf <");
        for (SubCommandAction action : subCommandActions) {
            builder.append(action.getSubCommandName()).append('|');
        }
        return builder.deleteCharAt(builder.length() - 1).append('>').toString();
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return Arrays.asList("tf", "together");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        if (args.length >= 1) {
            for (SubCommandAction action : subCommandActions) {
                if (action.getSubCommandName().equalsIgnoreCase(args[0])) {
                    if (!action.execute(server, sender, args)) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /tf " + action.getSubCommandName() + ' ' + action.getUsage() + " - " + action.getInfo()));
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Use '/tf help' for more information!"));
                    }
                    return;
                }
            }
        }
        sender.sendMessage(new TextComponentString(TextFormatting.RED + getUsage(sender)));
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return subCommandActions.stream().map(SubCommandAction::getSubCommandName).collect(Collectors.toList());
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("decline") || args[0].equalsIgnoreCase("kick")) {
                return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().stream().map(EntityPlayer::getName).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public List<SubCommandAction> getSubCommandActions() {
        return subCommandActions;
    }
}

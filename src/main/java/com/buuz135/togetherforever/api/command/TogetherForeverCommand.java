/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
        StringBuilder builder = new StringBuilder("Usage: /tofe <");
        for (SubCommandAction action : subCommandActions) {
            builder.append(action.getSubCommandName()).append('|');
        }
        return builder.deleteCharAt(builder.length() - 1).append('>').toString();
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return Arrays.asList("tofe", "together");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        if (args.length >= 1) {
            for (SubCommandAction action : subCommandActions) {
                if (action.getSubCommandName().equalsIgnoreCase(args[0])) {
                    if (!action.execute(server, sender, args)) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /tofe " + action.getSubCommandName() + ' ' + action.getUsage() + " - " + action.getInfo()));
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Use '/tofe help' for more information!"));
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

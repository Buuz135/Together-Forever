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
package com.buuz135.together_forever.command;


import com.buuz135.together_forever.api.IPlayerInformation;
import com.buuz135.together_forever.api.ITogetherTeam;
import com.buuz135.together_forever.api.TogetherForeverAPI;
import com.buuz135.together_forever.api.command.SubCommandAction;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class TeamInfoCommand extends SubCommandAction {

    public TeamInfoCommand() {

    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getSubCommand() {
        return Commands.literal("info").executes(commandContext -> execute(commandContext.getSource().getServer(), commandContext.getSource(), commandContext));
    }

    public int execute(MinecraftServer server, CommandSourceStack sender, CommandContext<CommandSourceStack> args) {
        ServerPlayer senderMP = sender.getPlayer();
        ITogetherTeam togetherTeam = TogetherForeverAPI.getInstance().getPlayerTeam(senderMP.getUUID());
        if (togetherTeam == null) {
            sender.sendSystemMessage(Component.literal(ChatFormatting.RED + "You don't have a team!"));
            return 0;
        }
        sender.sendSystemMessage(Component.literal(togetherTeam.getTeamName() + " team information:"));
        for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
            sender.sendSystemMessage(Component.literal(getFormattedName(playerInformation, playerInformation.getPlayer() != null, playerInformation.getUUID().equals(togetherTeam.getOwner()), senderMP.getUUID().equals(playerInformation.getUUID()))));
        }
        return 1;

    }


    private String getFormattedName(IPlayerInformation playerInformation, boolean online, boolean owner, boolean yourself) {
        StringBuilder builder = new StringBuilder(" - ");
        if (yourself) builder.append(ChatFormatting.GOLD).append('[');
        builder.append(online ? ChatFormatting.GREEN : ChatFormatting.RED).append(playerInformation.getName());
        if (yourself) builder.append(ChatFormatting.GOLD).append(']');
        if (owner) builder.append(ChatFormatting.DARK_RED).append('*');
        return builder.toString();
    }
}

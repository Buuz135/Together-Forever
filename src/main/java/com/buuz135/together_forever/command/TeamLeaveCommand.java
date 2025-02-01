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
import com.buuz135.together_forever.api.data.DefaultPlayerInformation;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class TeamLeaveCommand extends SubCommandAction {

    public TeamLeaveCommand() {

    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getSubCommand() {
        return Commands.literal("leave").executes(commandContext -> execute(commandContext.getSource().getServer(), commandContext.getSource(), commandContext));
    }

    @Override
    public int execute(MinecraftServer server, CommandSourceStack sender, CommandContext<CommandSourceStack> args) {

            ITogetherTeam togetherTeam = TogetherForeverAPI.getInstance().getPlayerTeam(sender.getPlayer().getUUID());
            if (togetherTeam != null) {
                TogetherForeverAPI.getInstance().removePlayerFromTeam(togetherTeam, DefaultPlayerInformation.createInformation(sender.getPlayer()));
                sender.sendSystemMessage(Component.literal(ChatFormatting.GREEN + "You successfully left your team."));
                for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                    ServerPlayer playerMP = playerInformation.getPlayer();
                    if (playerMP != null) {
                        playerMP.sendSystemMessage(Component.literal(ChatFormatting.RED + "Player " + sender.getPlayer().getName().getString() + " left your team."));
                    }
                }
                return 1;
            }

        return 0;
    }

}

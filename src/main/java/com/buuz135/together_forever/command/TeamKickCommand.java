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
import net.minecraft.world.entity.Entity;

public class TeamKickCommand extends SubCommandAction {

    public TeamKickCommand() {

    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getSubCommand() {
        return Commands.literal("kick").then(Commands.argument("Player", EntityArgument.player()).executes(commandContext -> execute(commandContext.getSource().getServer(), commandContext.getSource(), commandContext)));
    }

    public int execute(MinecraftServer server, CommandSourceStack sender, CommandContext<CommandSourceStack> args) {

                ServerPlayer commandSender = sender.getPlayer();
                ITogetherTeam teamSender = TogetherForeverAPI.getInstance().getPlayerTeam(commandSender.getUUID());
                if (teamSender == null) {
                    commandSender.sendSystemMessage(Component.literal(ChatFormatting.RED + "You don't have a team!"));
                    return 0;
                }
                if (!teamSender.getOwner().equals(commandSender.getUUID())) {
                    commandSender.sendSystemMessage(Component.literal(ChatFormatting.RED + "You can't do that! You are not the owner of the team!"));
                    return 0;
                }
                for (IPlayerInformation playerInformation : teamSender.getPlayers()) {
                    Entity entityReciever = args.getArgument("Player", Entity.class);
                    if (playerInformation.getUUID().equals(entityReciever.getUUID())) {
                        TogetherForeverAPI.getInstance().removePlayerFromTeam(teamSender, playerInformation);
                        if (playerInformation.getPlayer() != null) {
                            playerInformation.getPlayer().sendSystemMessage(Component.literal(ChatFormatting.RED + "You have been removed from " + teamSender.getTeamName() + "'s team."));
                        }
                        commandSender.sendSystemMessage(Component.literal(ChatFormatting.RED + "You successfully removed " + entityReciever.getName().getString() + " from your team"));
                        return 1;
                    }
                }
                commandSender.sendSystemMessage(Component.literal(ChatFormatting.RED + "That player is not in your team!"));
      
        return 0;
    }

}

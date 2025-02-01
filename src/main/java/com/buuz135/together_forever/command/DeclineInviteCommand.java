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
import com.buuz135.together_forever.api.TogetherForeverAPI;
import com.buuz135.together_forever.api.command.SubCommandAction;
import com.buuz135.together_forever.api.data.DefaultPlayerInformation;
import com.buuz135.together_forever.api.data.TeamInvite;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class DeclineInviteCommand extends SubCommandAction {

    public DeclineInviteCommand() {

    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getSubCommand() {
        return Commands.literal("decline").then(Commands.argument("Player", EntityArgument.player()).executes(commandContext -> execute(commandContext.getSource().getServer(), commandContext.getSource(), commandContext)));
    }

    @Override
    public int execute(MinecraftServer server, CommandSourceStack sender, CommandContext<CommandSourceStack> args) {
        ServerPlayer inviteReceiver = sender.getPlayer();
        Entity entitySender = null;
        try {
            entitySender = EntityArgument.getPlayer(args, "Player");
        if (entitySender instanceof ServerPlayer inviteSender && inviteReceiver != inviteSender) {
            IPlayerInformation infoReceiver = DefaultPlayerInformation.createInformation(inviteReceiver);
            IPlayerInformation infoSender = DefaultPlayerInformation.createInformation(inviteSender);
            for (TeamInvite invite : TogetherForeverAPI.getInstance().getTeamInvites()) {
                if (invite.getSender().equals(infoSender) && invite.getReciever().equals(infoReceiver)) {
                    inviteReceiver.sendSystemMessage(Component.literal(ChatFormatting.RED + "You have declined the invite."));
                    inviteSender.sendSystemMessage(Component.literal(ChatFormatting.RED + inviteReceiver.getName().getString() + " has declined the invite!"));
                    TogetherForeverAPI.getInstance().getTeamInvites().remove(invite);
                    return 1;
                }
            }
        }
    } catch (
    CommandSyntaxException e) {
        throw new RuntimeException(e);
    }

        return 1;
    }

}

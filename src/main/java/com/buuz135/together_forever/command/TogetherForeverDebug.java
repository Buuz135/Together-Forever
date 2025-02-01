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



import com.buuz135.together_forever.TogetherForever;
import com.buuz135.together_forever.api.TogetherForeverAPI;
import com.buuz135.together_forever.api.command.SubCommandAction;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

public class TogetherForeverDebug extends SubCommandAction {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getSubCommand() {
        return Commands.literal("tfdebug").requires(commandSourceStack -> commandSourceStack.hasPermission(4)).executes(commandContext -> execute(commandContext.getSource().getServer(), commandContext.getSource(), commandContext));
    }

    @Override
    public int execute(MinecraftServer server, CommandSourceStack sender, CommandContext<CommandSourceStack> args) {
        TogetherForever.LOGGER.info(TogetherForeverAPI.getInstance().getDataManager(ServerLifecycleHooks.getCurrentServer().overworld()).save(new CompoundTag()).toString());
        return 1;
    }

}

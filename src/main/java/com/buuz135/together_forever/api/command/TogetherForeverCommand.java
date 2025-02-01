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
package com.buuz135.together_forever.api.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class TogetherForeverCommand {

    public static TogetherForeverCommand command;

    private final List<SubCommandAction> subCommandActions;

    public TogetherForeverCommand(List<SubCommandAction> subCommandActions) {
        this.subCommandActions = subCommandActions;
        command = this;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (SubCommandAction subCommandAction : subCommandActions) {
            var subCommandLiteral = subCommandAction.getSubCommand();
            dispatcher.register(Commands.literal("togetherforever").then(subCommandLiteral));
            dispatcher.register(Commands.literal("tofe").then(subCommandLiteral));
        }

    }


    public List<SubCommandAction> getSubCommandActions() {
        return subCommandActions;
    }
}

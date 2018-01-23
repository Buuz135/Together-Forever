package com.buuz135.togetherforever.api.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class SubCommandAction {

    private final String subCommandName;

    public SubCommandAction(String subCommandName) {
        this.subCommandName = subCommandName;
    }

    public abstract boolean execute(MinecraftServer server, ICommandSender sender, String[] args);

    public abstract String getUsage();

    public String getSubCommandName() {
        return subCommandName;
    }
}

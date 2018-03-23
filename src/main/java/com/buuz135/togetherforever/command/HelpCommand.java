package com.buuz135.togetherforever.command;

import com.buuz135.togetherforever.api.command.SubCommandAction;
import com.buuz135.togetherforever.api.command.TogetherForeverCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class HelpCommand extends SubCommandAction {

    public HelpCommand() {
        super("help");
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (TogetherForeverCommand.command != null) {
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Together Forever Commands: "));
            for (SubCommandAction action : TogetherForeverCommand.command.getSubCommandActions()) {
                if (!action.getSubCommandName().equals(this.getSubCommandName()))
                    sender.sendMessage(new TextComponentString(TextFormatting.BLUE + " /tf " + action.getSubCommandName() + " " + getUsage() + TextFormatting.GRAY + "- " + TextFormatting.AQUA + action.getInfo()));
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getInfo() {
        return "";
    }
}

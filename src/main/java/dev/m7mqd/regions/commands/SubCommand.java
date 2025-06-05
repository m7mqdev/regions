package dev.m7mqd.regions.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    private final String name;
    private final String permission;

    public SubCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public abstract void run(CommandSender sender, String[] args);
}
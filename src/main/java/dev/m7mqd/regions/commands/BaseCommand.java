package dev.m7mqd.regions.commands;

import dev.m7mqd.regions.utils.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private SubCommand defaultCommand;

    protected void register(SubCommand command) {
        if (command.getName().equalsIgnoreCase("default")) {
            this.defaultCommand = command;
        } else {
            subCommands.put(command.getName().toLowerCase(), command);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        SubCommand sub;

        if (args.length == 0) {
            sub = defaultCommand;
        } else {
            sub = subCommands.get(args[0].toLowerCase());
            if (sub != null) {
                args = Arrays.copyOfRange(args, 1, args.length);
            } else {
                Messenger.send(sender, "<red>Unknown sub-command.");
                return true;
            }
        }

        if (sub == null) {
            Messenger.send(sender, "<red>No default command is set.");
            return true;
        }

        if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
            Messenger.send(sender, "<red>You don't have enough permission.");
            return true;
        }
        sub.run(sender, args);
        return true;
    }
}

package dev.m7mqd.regions.commands;

import dev.m7mqd.regions.menus.MenuService;
import dev.m7mqd.regions.menus.RegionsMenu;
import dev.m7mqd.regions.utils.Messenger;
import io.github.mqzen.menus.Lotus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegionsCommand implements CommandExecutor {
    private final MenuService menuService;

    public RegionsCommand(MenuService menuService) {
        this.menuService = menuService;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("regions.menu")){
            Messenger.send(sender, "<red>You don't have enough permission.");
            return true;
        }
        if(!(sender instanceof Player player)){
            Messenger.send(sender, "<red>Players only are allowed.");
            return true;
        }
        menuService.open(player);
        return true;
    }
}

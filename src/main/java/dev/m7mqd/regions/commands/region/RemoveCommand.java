package dev.m7mqd.regions.commands.region;

import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.commands.SubCommand;
import dev.m7mqd.regions.utils.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class RemoveCommand extends SubCommand {
    private final RegionService regionService;
    private final Plugin plugin;
    public RemoveCommand(RegionService regionService, Plugin plugin) {
        super("remove", "regions.remove");
        this.regionService = regionService;
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.send(sender, "<red>Usage: /region remove <name> <username>");
            return;
        }
        Region region = regionService.getRegion(args[0]);
        if (region == null) {
            Messenger.send(sender, "<red>Region <name> not found.", Placeholder.unparsed("name", args[0]));
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            region.getWhitelisted().remove(target.getUniqueId());
            Messenger.send(sender, "<green>Removed <player> from region <region>.",
                    Placeholder.unparsed("player", args[1]),
                    Placeholder.unparsed("region", args[0]));
        });
    }
}

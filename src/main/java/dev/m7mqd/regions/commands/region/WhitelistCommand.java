package dev.m7mqd.regions.commands.region;

import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.commands.SubCommand;
import dev.m7mqd.regions.utils.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class WhitelistCommand extends SubCommand {
    private final RegionService regionService;
    private final Plugin plugin;
    public WhitelistCommand(RegionService regionService, Plugin plugin) {
        super("whitelist", "regions.whitelist");
        this.regionService = regionService;
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            Messenger.send(sender, "<red>Usage: /region whitelist <name>");
            return;
        }
        Region region = regionService.getRegion(args[0]);
        if (region == null) {
            Messenger.send(sender, "<red>Region <name> not found.", Placeholder.unparsed("name", args[0]));
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Component list = Component.join(
                    JoinConfiguration.separator(Component.text(", ")),
                    region.getWhitelisted().stream()
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .map(Component::text)
                            .toList()
            );
            Messenger.send(sender, "<green>Whitelisted players: <list>", Placeholder.component("list", list));
        });
    }
}
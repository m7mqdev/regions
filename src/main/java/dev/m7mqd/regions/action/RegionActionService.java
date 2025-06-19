package dev.m7mqd.regions.action;

import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.utils.Messenger;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegionActionService implements Listener {
    private final Plugin plugin;
    private final RegionService regionService;
    private final Map<UUID, ActionEntry> actionMap = new ConcurrentHashMap<>();

    public RegionActionService(Plugin plugin, RegionService regionService){
        this.regionService = regionService;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void add(Player player, RegionAction action, Region region) {
        final ActionEntry actionEntry = new ActionEntry(action, region);
        final UUID id = player.getUniqueId();
        actionMap.put(id, actionEntry);
        Messenger.send(player, "<yellow>Type the value in chat to complete the action.");
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ActionEntry current = this.actionMap.get(id);
            if(current == actionEntry){
                this.actionMap.remove(id);
                if(!player.isOnline()) return;
                Messenger.send(player, "<red>Region action expired.");

            }

        }, 20*60); //action expires after 1 minute

    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ActionEntry entry = actionMap.remove(uuid);
        if (entry == null) return;

        event.setCancelled(true);

        String input = ((TextComponent)event.originalMessage()).content().trim();
        Region region = entry.region;

        switch (entry.action) {
            case RENAME -> {
                regionService.updateName(region, input);
                Messenger.send(player, "<green>Region renamed to <white>" + input + "</white>.");
            }
            case WHITELIST_ADD -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(input);
                region.addWhitelisted(target.getUniqueId());
                Messenger.send(player, "<green>Added <white>" + target.getName() + "</white> to whitelist.");
            }
            case WHITELIST_REMOVE -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(input);
                region.removeWhitelisted(target.getUniqueId());
                Messenger.send(player, "<green>Removed <white>" + target.getName() + "</white> from whitelist.");
            }
        }
    }

    private record ActionEntry(RegionAction action, Region region) {
    }
}
package dev.m7mqd.regions.selection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;

public class SelectionService implements Listener {

    private final Map<Player, Selection> selections = new WeakHashMap<>();

    public SelectionService(Plugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event){
        this.selections.remove(event.getPlayer());
    }

    public Selection getOrCreateSelection(Player player) {
        return selections.computeIfAbsent(player, p -> new Selection());
    }

    public void setPosition(Player player, Location location, boolean first) {
        Selection selection = getOrCreateSelection(player);
        if (first) {
            selection.setPos1(location);
        } else {
            selection.setPos2(location);
        }
    }

    public Selection getSelection(Player player) {
        return selections.get(player);
    }

    public void clearSelection(Player player) {
        selections.remove(player);
    }

    public boolean hasCompleteSelection(Player player) {
        Selection sel = selections.get(player);
        return sel != null && sel.isComplete();
    }
}
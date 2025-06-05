package dev.m7mqd.regions;


import dev.m7mqd.regions.selection.SelectionService;
import dev.m7mqd.regions.utils.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class WandService implements Listener {

    private final SelectionService selectionService;

    public WandService(Plugin plugin, SelectionService selectionService) {
        this.selectionService = selectionService;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ItemStack getWand() {
        return new ItemStack(Material.WOODEN_AXE);
    }

    public boolean isWand(Player player, ItemStack item) {
        if (item == null || item.getType() != Material.WOODEN_AXE) return false;
        return selectionService.getSelection(player) != null;
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!isWand(player, item)) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        event.setCancelled(true);

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            selectionService.setPosition(player, clicked.getLocation(), true);
            Messenger.send(player, "<green>Set <yellow>position 1 <green>to <gray><loc>",
                    Placeholder.component("loc", format(clicked.getLocation())));
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            selectionService.setPosition(player, clicked.getLocation(), false);
            Messenger.send(player, "<green>Set <yellow>position 2 <green>to <gray><loc>",
                    Placeholder.component("loc", format(clicked.getLocation())));
        }
    }

    private Component format(org.bukkit.Location loc) {
        return Component.text(loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
    }
}
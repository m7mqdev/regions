package dev.m7mqd.regions.flag.impl;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.flag.FlagState;
import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DefaultFlagService implements Listener {
    private static final Set<Material> CLICKABLE_BLOCKS = EnumSet.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.ACACIA_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.POLISHED_BLACKSTONE_BUTTON,
            Material.CRAFTING_TABLE,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.BREWING_STAND,
            Material.ENCHANTING_TABLE,
            Material.ANVIL,
            Material.CHIPPED_ANVIL,
            Material.DAMAGED_ANVIL,
            Material.BEACON,
            Material.BELL,
            Material.SHULKER_BOX,
            Material.BARREL,
            Material.HOPPER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.JUKEBOX,
            Material.COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.NOTE_BLOCK,
            Material.LECTERN,
            Material.GRINDSTONE,
            Material.LOOM,
            Material.STONECUTTER,
            Material.CARTOGRAPHY_TABLE,
            Material.SMITHING_TABLE,
            Material.FLETCHING_TABLE,
            Material.RESPAWN_ANCHOR,
            Material.LODESTONE,
            Material.CAKE
    );
    private final RegionService regionService;

    public DefaultFlagService(RegionService regionService, FlagService flagService, Plugin plugin) {
        this.regionService = regionService;
        for (DefaultFlag value : DefaultFlag.values()) {
            flagService.addResolver(value.toKey(), () -> value);
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public FlagState getFlagState(Region region, DefaultFlag flag) {
        FlagState state = region.getFlagState(flag);
        return state != null ? state : FlagState.NONE;
    }

    public List<DefaultFlag> flags() {
        return List.of(DefaultFlag.values());
    }

    private boolean isWhitelisted(Region region, Player player) {
        return region.getWhitelisted().contains(player.getUniqueId());
    }

    private boolean isAllowed(Region region, Player player, DefaultFlag flag) {
        if(player.hasPermission("regions.bypass")) return true;
        FlagState state = getFlagState(region, flag);
        return switch (state) {
            case EVERYONE -> true;
            case WHITELISTED -> isWhitelisted(region, player);
            case NONE -> false;
        };
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        handle(event.getBlock().getLocation(), event.getPlayer(), event, DefaultFlag.BLOCK_BREAK);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        handle(event.getBlock().getLocation(), event.getPlayer(), event, DefaultFlag.BLOCK_PLACE);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && CLICKABLE_BLOCKS.contains(clickedBlock.getType())) {
            handle(clickedBlock.getLocation(), event.getPlayer(), event, DefaultFlag.INTERACT);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            handle(victim.getLocation(), attacker, event, DefaultFlag.ENTITY_DAMAGE);
        }
    }

    private void handle(Location location, Player player, Cancellable event, DefaultFlag flag) {
        Region region = regionService.getRegion(location);
        if (region == null) return;

        if (!isAllowed(region, player, flag)) {
            event.setCancelled(true);
        }
    }
}
package dev.m7mqd.regions.flag.impl;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.flag.FlagState;
import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DefaultFlagService implements Listener {

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

    public void setFlagState(Region region, DefaultFlag flag, FlagState state) {
        region.setFlagState(flag, state);
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
        if(state == null) return false;
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
        if (clickedBlock != null) {
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
package dev.m7mqd.regions.menus;

import dev.m7mqd.regions.action.RegionActionService;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import io.github.mqzen.menus.Lotus;
import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.button.Button;
import io.github.mqzen.menus.misc.button.actions.ButtonClickAction;
import io.github.mqzen.menus.misc.itembuilder.ItemBuilder;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionsMenu implements Menu {

    private final RegionService regionService;
    private final RegionActionService actionService;
    private final FlagService flagService;
    private final Lotus lotus;

    public RegionsMenu(RegionService regionService, RegionActionService actionService, FlagService flagService, Lotus lotus) {
        this.regionService = regionService;
        this.actionService = actionService;
        this.flagService = flagService;
        this.lotus = lotus;
    }

    @Override
    public String getName() {
        return "regions";
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern("<green>All Regions");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(6);
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        Content.Builder builder = Content.builder(capacity);

        Collection<Region> regions = regionService.getRegions().values();
        AtomicInteger slot = new AtomicInteger(0);

        regions.forEach(region -> {
            builder.setButton(slot.getAndIncrement(), Button.clickable(
                    ItemBuilder.modern(Material.GRASS_BLOCK)
                            .setDisplay(Component.text(region.getName()))
                            .build(), ButtonClickAction.plain((view, event) -> {
                            lotus.openMenu(player, new RegionMenu(actionService, flagService, region, lotus));
                    })
            ));
        });
        return builder.build();
    }
}

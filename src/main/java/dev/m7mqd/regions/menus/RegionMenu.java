package dev.m7mqd.regions.menus;

import dev.m7mqd.regions.action.RegionAction;
import dev.m7mqd.regions.action.RegionActionService;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.utils.Messenger;
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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegionMenu implements Menu {
    private final RegionActionService actionService;
    private final FlagService flagService;
    private final Region region;
    private final Lotus lotus;

    public RegionMenu(RegionActionService actionService, FlagService flagService, Region region, Lotus lotus) {
        this.actionService = actionService;
        this.flagService = flagService;
        this.region = region;
        this.lotus = lotus;
    }

    @Override
    public String getName() {
        return "region";
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern("<green>Region: " + region.getName());
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(3);
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        return Content.builder(capacity)
                .setButton(10, actionButton("Rename", Material.NAME_TAG, player, region, RegionAction.RENAME))
                .setButton(11, actionButton("Whitelist Add", Material.PLAYER_HEAD, player, region, RegionAction.WHITELIST_ADD))
                .setButton(12, actionButton("Whitelist Remove", Material.BARRIER, player, region, RegionAction.WHITELIST_REMOVE))
                .setButton(13, Button.clickable(
                        ItemBuilder.modern(Material.WOODEN_AXE)
                                .setDisplay(Component.text("Redefine Location"))
                                .setLore(MiniMessage.miniMessage().deserialize("<gray>Left-click: Set Min"),
                                        MiniMessage.miniMessage().deserialize("<gray>Right-click: Set Max"))
                                .build(),
                        ButtonClickAction.plain((view, event) -> {
                            Location loc = player.getLocation();

                            if (event.isLeftClick()) {
                                region.setMin(loc);
                                player.sendMessage(Component.text("Min location set to your position."));
                            } else if (event.isRightClick()) {
                                region.setMax(loc);
                                player.sendMessage(Component.text("Max location set to your position."));
                            } else {
                                player.sendMessage(Component.text("Use left or right click only."));
                            }

                            player.closeInventory();
                        })
                ))
                .setButton(14, Button.clickable(
                        ItemBuilder.modern(Material.REDSTONE_TORCH)
                                .setDisplay(Component.text("Edit Flags"))
                                .build(), ButtonClickAction.plain((view, event) -> {
                                lotus.openMenu(player, new FlagsMenu(region, flagService));
                        })
                ))
                .build();
    }

    private Button actionButton(String label, Material material, Player player, Region region, RegionAction action) {
        return Button.clickable(
                ItemBuilder.modern(material)
                        .setDisplay(Component.text(label))
                        .build(), ButtonClickAction.plain((view, event) -> {
                    actionService.add(player, action, region);
                    Messenger.send(player, "<green>Send input in chat to complete the action.");
                    player.closeInventory();
                })
        );
    }
}

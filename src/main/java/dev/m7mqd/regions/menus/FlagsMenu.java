package dev.m7mqd.regions.menus;

import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.flag.Flag;
import dev.m7mqd.regions.flag.FlagState;
import dev.m7mqd.regions.model.Region;
import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.button.Button;
import io.github.mqzen.menus.misc.itembuilder.ItemBuilder;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

import static dev.m7mqd.regions.flag.FlagState.*;

public class FlagsMenu implements Menu {

    private final Region region;
    private final FlagService flagService;

    public FlagsMenu(Region region, FlagService flagService) {
        this.region = region;
        this.flagService = flagService;
    }

    @Override
    public String getName() {
        return "flags";
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern("<yellow>Edit Flags");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        int flags = flagService.flags().size();
        int rows = (int) Math.ceil(flags / 9.0);
        return Capacity.ofRows(Math.max(1, Math.min(6, rows)));
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        Collection<Flag> flags = flagService.flags();

        Content.Builder builder = Content.builder(capacity);
        Iterator<Flag> iterator = flags.iterator();
        int i = 0;
        while(iterator.hasNext()){
            Flag flag = iterator.next();
            builder.setButton(i++, createFlagButton(region, flag));
        }
        return builder.build();
    }

    private Button createFlagButton(Region region, Flag flag) {
        FlagState currentState = region.getFlagState(flag);
        if(currentState == null) currentState = NONE;
        Material displayMaterial = switch (currentState) {
            case EVERYONE -> Material.LIME_DYE;
            case NONE -> Material.GRAY_DYE;
            case WHITELISTED -> Material.YELLOW_DYE;
        };

        String displayName = "<white>" + flag.toKey().toString() + ": <green>" + currentState.name();

        return Button.transformerButton(
                ItemBuilder.modern(displayMaterial)
                        .setDisplay(MiniMessage.miniMessage().deserialize(displayName))
                        .setLore(MiniMessage.miniMessage().deserialize("<gray>Click to cycle state"))
                        .build(),
                (view, click) -> {
                    FlagState current = region.getFlagState(flag);
                    if(current == null) current = NONE;
                    FlagState next = switch (current) {
                        case EVERYONE -> WHITELISTED;
                        case WHITELISTED -> NONE;
                        case NONE -> EVERYONE;
                    };
                    region.setFlagState(flag, next);
                    return createFlagButton(region, flag); // reuses same logic
                }
        );
    }
}

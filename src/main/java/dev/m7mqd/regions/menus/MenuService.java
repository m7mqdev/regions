package dev.m7mqd.regions.menus;

import dev.m7mqd.regions.action.RegionActionService;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.model.RegionService;
import io.github.mqzen.menus.Lotus;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MenuService {
    private final Lotus lotus;
    private final RegionsMenu regionsMenu;
    public MenuService(Plugin plugin, RegionService regionService, RegionActionService actionService, FlagService flagService) {
        this.lotus = Lotus.load(plugin);
        this.regionsMenu = new RegionsMenu(regionService, actionService, flagService, lotus);
    }

    public void open(Player player){
        this.lotus.openMenu(player, regionsMenu);
    }
}

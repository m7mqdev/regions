package dev.m7mqd.regions;

import dev.m7mqd.regions.action.RegionActionService;
import dev.m7mqd.regions.commands.RegionCommand;
import dev.m7mqd.regions.commands.RegionsCommand;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.flag.impl.DefaultFlagService;
import dev.m7mqd.regions.menus.MenuService;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.model.SQLRegionService;
import dev.m7mqd.regions.selection.SelectionService;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionsPlugin extends JavaPlugin {
    private RegionService regionService;
    private SelectionService selectionService;
    private RegionActionService regionActionService;
    private FlagService flagService;
    private DefaultFlagService defaultFlagService;
    private MenuService menuService;
    private WandService wandService;


    @Override
    public void onEnable() {
        registerServices();
        registerCommands();
        this.getServer().getScheduler().runTask(this, () -> {
            regionService.open();
            regionService.load();
            regionService.close();
        });

    }

    private void registerServices(){
        this.flagService = new FlagService();
        this.regionService = new SQLRegionService(this, flagService);
        this.selectionService = new SelectionService(this);
        this.wandService = new WandService(this, selectionService);
        this.regionActionService = new RegionActionService(this);
        this.defaultFlagService = new DefaultFlagService(regionService, flagService, this);
        this.menuService = new MenuService(this, regionService, regionActionService, flagService);
    }
    private void registerCommands(){
        this.getCommand("regions").setExecutor(new RegionsCommand(menuService));
        this.getCommand("region").setExecutor(new RegionCommand(this, regionService, flagService, selectionService, wandService));
    }
    @Override
    public void onDisable() {
        regionService.open();
        regionService.load();
        regionService.close();
    }
}

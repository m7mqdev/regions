package dev.m7mqd.regions.commands;

import dev.m7mqd.regions.WandService;
import dev.m7mqd.regions.commands.region.*;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.selection.SelectionService;
import org.bukkit.plugin.Plugin;

public class RegionCommand extends BaseCommand{

    public RegionCommand(Plugin plugin, RegionService regionService, FlagService flagService, SelectionService selectionService, WandService wandService){
        this.register(new AddCommand(regionService, plugin));
        this.register(new RemoveCommand(regionService, plugin));
        this.register(new CreateCommand(selectionService, regionService));
        this.register(new WandCommand(selectionService, wandService));
        this.register(new WhitelistCommand(regionService, plugin));
        this.register(new FlagCommand(regionService, flagService));
    }
}

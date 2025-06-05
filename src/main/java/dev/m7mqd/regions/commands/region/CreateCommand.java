package dev.m7mqd.regions.commands.region;

import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.selection.Selection;
import dev.m7mqd.regions.selection.SelectionService;
import dev.m7mqd.regions.commands.SubCommand;
import dev.m7mqd.regions.utils.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CreateCommand extends SubCommand {
    private final SelectionService selectionService;
    private final RegionService regionService;
    public CreateCommand(SelectionService selectionService, RegionService regionService) {
        super("create", "regions.create");
        this.selectionService = selectionService;
        this.regionService = regionService;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messenger.send(sender, "<red>Only players can create regions.");
            return;
        }
        if (args.length < 1) {
            Messenger.send(sender, "<red>Usage: /region create <name>");
            return;
        }
        String name = args[0];
        Selection selection = selectionService.getSelection(player);

        if(selection == null || !selection.isComplete()) {
            Messenger.send(sender, "<red>You must select both points with the wand.");
            return;
        }
        if(regionService.getRegion(name) != null){
            Messenger.send(sender, "<red>Region with name <name> already exists.", Placeholder.unparsed("name", name));
            return;
        }
        Region region = new Region(name, Collections.emptySet(), Collections.emptyMap(), selection.getPos1(), selection.getPos2());
        regionService.addRegion(region);
        selectionService.clearSelection(player);
        Messenger.send(sender, "<green>Region <name> created.", Placeholder.unparsed("name", name));
    }

}

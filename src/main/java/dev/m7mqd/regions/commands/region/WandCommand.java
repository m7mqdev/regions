package dev.m7mqd.regions.commands.region;

import dev.m7mqd.regions.WandService;
import dev.m7mqd.regions.commands.SubCommand;
import dev.m7mqd.regions.selection.SelectionService;
import dev.m7mqd.regions.utils.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WandCommand extends SubCommand {
    private final WandService wandService;
    private final SelectionService selectionService;
    public WandCommand(SelectionService selectionService, WandService wandService) {
        super("wand", "regions.wand");
        this.wandService = wandService;
        this.selectionService = selectionService;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messenger.send(sender, "<red>Only players can receive the wand.");
            return;
        }
        if(this.selectionService.getSelection(player) != null){
            this.selectionService.clearSelection(player);
            Messenger.send(sender, "<green>Selection mode disabled.");
            return;
        }
        this.selectionService.getOrCreateSelection(player);
        player.getInventory().addItem(wandService.getWand());
        Messenger.send(sender, "<green>You have been given the region wand and selection mode enabled.");
    }
}
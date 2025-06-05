package dev.m7mqd.regions.commands.region;

import dev.m7mqd.regions.commands.SubCommand;
import dev.m7mqd.regions.flag.Flag;
import dev.m7mqd.regions.flag.FlagService;
import dev.m7mqd.regions.flag.FlagState;
import dev.m7mqd.regions.model.Region;
import dev.m7mqd.regions.model.RegionService;
import dev.m7mqd.regions.utils.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class FlagCommand extends SubCommand {
    private final RegionService regionService;
    private final FlagService flagService;
    public FlagCommand(RegionService regionService, FlagService flagService) {
        super("flag", "regions.flag");
        this.regionService = regionService;
        this.flagService = flagService;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Messenger.send(sender, "<red>Usage: /region flag <name> <flag> <state>");
            return;
        }
        String regionName = args[0];
        Region region = regionService.getRegion(regionName);
        if (region == null) {
            Messenger.send(sender, "<red>Region <name> not found.", Placeholder.unparsed("name", regionName));
            return;
        }
        Flag flag;
        try {
            flag = Objects.requireNonNull(flagService.resolve(Flag.Key.fromString(args[1].toLowerCase())));
        } catch (IllegalArgumentException | NullPointerException e) {
            Messenger.send(sender, "<red>Unknown flag <flag>.", Placeholder.unparsed("flag", args[1]));
            return;
        }
        FlagState state;
        try {
            state = FlagState.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            Messenger.send(sender, "<red>Unknown state <state>.", Placeholder.unparsed("state", args[2]));
            return;
        }
        region.setFlagState(flag, state);
        Messenger.send(sender, "<green>Set flag <flag> to <state> for region <region>.",
                Placeholder.unparsed("flag", args[1]),
                Placeholder.unparsed("state", state.name()),
                Placeholder.unparsed("region", regionName));
    }
}

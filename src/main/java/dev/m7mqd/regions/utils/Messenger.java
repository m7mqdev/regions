package dev.m7mqd.regions.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

public class Messenger {
    public static void send(CommandSender sender, String msg, TagResolver... likes) {
        Component component = MiniMessage.miniMessage().deserialize(msg, likes);
        sender.sendMessage(component);
    }
}

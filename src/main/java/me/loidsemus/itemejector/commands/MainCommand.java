package me.loidsemus.itemejector.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.loidsemus.itemejector.ItemEjector;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("itemejector|ie")
@CommandPermission("itemejector.use")
public class MainCommand extends BaseCommand {

    private ItemEjector plugin;

    public MainCommand(ItemEjector plugin) {
        this.plugin = plugin;
    }

    @HelpCommand
    public void onHelp(CommandSender sender) {
        sender.sendMessage("Subcommands: <add|remove|list>");
    }

    @Subcommand("add|a")
    public void onAdd(Player player, Material item, @Description("Max amount of items allowed, 0 for none") int max) {
        plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).addOrUpdateBlacklistedItem(item, max);
        player.sendMessage(plugin.getMessages().getMessage("added_item", true)
                .replaceAll("\\{item}", item.toString())
                .replaceAll("\\{maxAmount}", max + ""));
    }

    @Subcommand("remove|r")
    public void onRemove(Player player, Material item) {
        if (!plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).hasBlacklistedItem(item)) {
            player.sendMessage(plugin.getMessages().getMessage("item_not_blacklisted", true).replaceAll("\\{item}", item.toString()));
            return;
        }

        plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).removeBlacklistedItem(item);
        player.sendMessage(plugin.getMessages().getMessage("removed_item", true).replaceAll("\\{item}", item.toString()));
    }

    @Subcommand("list|l")
    public void onList(Player player) {
        StringBuilder message = new StringBuilder(plugin.getMessages().getMessage("list_header", true) + "\n");
        for (Map.Entry<Material, Integer> entry : plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).getBlacklistedItems().entrySet()) {
            message.append(plugin.getMessages().getMessage("list_item", false)
            .replaceAll("\\{item}", entry.getKey().toString())
            .replaceAll("\\{maxAmount}", entry.getValue().toString())).append("\n");
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("reload|rel")
    @CommandPermission("itemejector.admin")
    public void onReload(CommandSender sender) {
        plugin.loadConfigAndMessages();
        sender.sendMessage("Config and messages reloaded");
    }
}

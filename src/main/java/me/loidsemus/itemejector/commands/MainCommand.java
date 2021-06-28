package me.loidsemus.itemejector.commands;


import com.mojang.brigadier.tree.LiteralCommandNode;
import me.loidsemus.itemejector.ItemEjector;
import me.loidsemus.itemejector.database.DataPlayer;
import me.loidsemus.itemejector.menu.ManagementMenu;
import me.loidsemus.itemejector.messages.Messages;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.io.InputStream;

public final class MainCommand implements CommandExecutor {

    private final ItemEjector plugin;

    public MainCommand(ItemEjector plugin) {
        this.plugin = plugin;
    }

    // TODO: permission checking

    public static void registerCommodore(ItemEjector plugin, Commodore commodore) throws Exception {
        PluginCommand command = plugin.getCommand("itemejector");

        try (InputStream is = plugin.getResource("itemejector.commodore")) {
            if (is == null) {
                throw new Exception("Command completion file missing from jar");
            }

            LiteralCommandNode<?> commandNode = CommodoreFileFormat.parse(is);
            commodore.register(command, commandNode);
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
            if (!sender.hasPermission("itemejector.admin")) {
                plugin.getMessageProvider().sendPrefixed(sender, Messages.INSUFFICIENT_PERMISSION);
                return true;
            }
            plugin.loadConfigAndMessages();
            sender.sendMessage("Config and messages reloaded");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can't be used by console!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("itemejector.use")) {
            plugin.getMessageProvider().sendPrefixed(player, Messages.INSUFFICIENT_PERMISSION);
            return true;
        }
        DataPlayer dataPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId().toString());

        if (args.length == 1) {
            // /add
            if (args[0].equalsIgnoreCase("add")) {
                showUsage(player, "add <item> [max]");
            }
            // remove
            else if (args[0].equalsIgnoreCase("remove")) {
                showUsage(player, "remove <item>");
            }
            // list
            else if (args[0].equalsIgnoreCase("list")) {
                new ManagementMenu(plugin, dataPlayer).show(player);
            }
            else {
                showUsage(player);
            }
        }
        else if (args.length == 2) {
            // add <item>
            if (args[0].equalsIgnoreCase("add")) {
                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    plugin.getMessageProvider().sendPrefixed(player, Messages.INVALID_ITEM, "arg", args[1]);
                    return true;
                }
                dataPlayer.addOrUpdateBlacklistedItem(material, 0);
                plugin.getMessageProvider().sendPrefixed(player, Messages.ADDED_ITEM, "item", material.toString(), "maxAmount", "0");
            }
            // remove <item>
            else if (args[0].equalsIgnoreCase("remove")) {
                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    plugin.getMessageProvider().sendPrefixed(player, Messages.INVALID_ITEM, "arg", args[1]);
                    return true;
                }

                if (!dataPlayer.hasBlacklistedItem(material)) {
                    plugin.getMessageProvider().sendPrefixed(player, Messages.ITEM_NOT_BLACKLISTED, "item", material.toString());
                    return true;
                }

                dataPlayer.removeBlacklistedItem(material);
                plugin.getMessageProvider().sendPrefixed(player, Messages.REMOVED_ITEM, "item", material.toString());
            }
            else {
                showUsage(player);
            }
        }
        else if (args.length == 3) {
            // add <item> [max]
            if (args[0].equalsIgnoreCase("add")) {
                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    plugin.getMessageProvider().sendPrefixed(player, Messages.INVALID_ITEM, "arg", args[1]);
                    return true;
                }

                try {
                    int max = Integer.parseInt(args[2]);

                    plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).addOrUpdateBlacklistedItem(material, max);
                    plugin.getMessageProvider().sendPrefixed(player, Messages.ADDED_ITEM, "item", material.toString(), "maxAmount", max + "");
                } catch (NumberFormatException e) {
                    plugin.getMessageProvider().sendPrefixed(player, Messages.NOT_A_NUMBER, "arg", args[2]);
                    return true;
                }
            }
            else {
                showUsage(player);
            }
        }
        else {
            new ManagementMenu(plugin, dataPlayer).show(player);
        }

        return true;
    }

    private void showUsage(Player p, String usage) {
        plugin.getMessageProvider().sendPrefixed(p, Messages.CORRECT_USAGE, "usage", usage);
    }

    private void showUsage(Player p) {
        showUsage(p, "(add <item> [max] | remove <item> | list)");
    }
}

package me.loidsemus.itemejector.commands;


import me.loidsemus.itemejector.ItemEjector;
import me.loidsemus.itemejector.database.DataPlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class MainCommand implements CommandExecutor {

    private final ItemEjector plugin;

    public MainCommand(ItemEjector plugin) {
        this.plugin = plugin;
    }

    // TODO: permission checking

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
            plugin.loadConfigAndMessages();
            sender.sendMessage("Config and messages reloaded");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can't be used by console!");
            return true;
        }
        Player player = (Player) sender;
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
                StringBuilder message = new StringBuilder(plugin.getMessages().get("list_header", true) + "\n");
                for (Map.Entry<Material, Integer> entry : dataPlayer.getBlacklistedItems().entrySet()) {
                    message.append(plugin.getMessages().get("list_item", false)
                            .replaceAll("\\{item}", entry.getKey().toString())
                            .replaceAll("\\{maxAmount}", entry.getValue().toString())).append("\n");
                }
                player.sendMessage(message.toString());
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
                    // TODO: Configurable message
                    player.sendMessage(args[1] + " is not a valid item name");
                    return true;
                }
                dataPlayer.addOrUpdateBlacklistedItem(material, 0);
                player.sendMessage(plugin.getMessages().get("added_item", true)
                        .replaceAll("\\{item}", material.toString())
                        .replaceAll("\\{maxAmount}", "0"));
            }
            // remove <item>
            else if (args[0].equalsIgnoreCase("remove")) {
                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    // TODO: Configurable message
                    player.sendMessage(args[1] + " is not a valid item name");
                    return true;
                }

                if (!dataPlayer.hasBlacklistedItem(material)) {
                    player.sendMessage(plugin.getMessages().get("item_not_blacklisted", true).replaceAll("\\{item}", material.toString()));
                    return true;
                }

                dataPlayer.removeBlacklistedItem(material);
                player.sendMessage(plugin.getMessages().get("removed_item", true).replaceAll("\\{item}", material.toString()));
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
                    // TODO: Configurable message
                    player.sendMessage(args[1] + " is not a valid item name");
                    return true;
                }

                try {
                    int max = Integer.parseInt(args[2]);

                    plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).addOrUpdateBlacklistedItem(material, max);
                    player.sendMessage(plugin.getMessages().get("added_item", true)
                            .replaceAll("\\{item}", material.toString())
                            .replaceAll("\\{maxAmount}", max + ""));
                } catch (NumberFormatException e) {
                    // TODO: Configurable message
                    player.sendMessage(args[2] + " is not a number");
                    return true;
                }
            }
            else {
                showUsage(player);
            }
        }
        else {
            showUsage(player);
        }

        return true;
    }

    private void showUsage(Player p, String usage) {
        // TODO: Configurable message
        p.sendMessage("Usage: /ie " + usage);
    }

    private void showUsage(Player p) {
        showUsage(p, "(add <item> [max] | remove <item> | list)");
    }
}

package me.loidsemus.itemejector.commands;


import com.mojang.brigadier.tree.LiteralCommandNode;
import me.loidsemus.itemejector.ItemEjector;
import me.loidsemus.itemejector.config.GUISettings;
import me.loidsemus.itemejector.config.Settings;
import me.loidsemus.itemejector.database.DataPlayer;
import me.loidsemus.itemejector.menu.ManagementMenu;
import me.loidsemus.itemejector.messages.LangKey;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.io.InputStream;
import java.util.Map;

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
                sender.sendMessage(plugin.getMessages().get(LangKey.INSUFFICIENT_PERMISSION, true));
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
            player.sendMessage(plugin.getMessages().get(LangKey.INSUFFICIENT_PERMISSION, true));
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
                StringBuilder message = new StringBuilder(plugin.getMessages().get(LangKey.LIST_HEADER, true) + "\n");
                for (Map.Entry<Material, Integer> entry : dataPlayer.getBlacklistedItems().entrySet()) {
                    message.append(plugin.getMessages().get(LangKey.LIST_ITEM, false, plugin.getMessages().getPrefixOffset(), entry.getKey().toString(), entry.getValue().toString()))
                            .append("\n");
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
                    player.sendMessage(plugin.getMessages().get(LangKey.INVALID_ITEM, true, args[1]));
                    return true;
                }
                dataPlayer.addOrUpdateBlacklistedItem(material, 0);
                player.sendMessage(plugin.getMessages().get(LangKey.ADDED_ITEM, true, material.toString(), 0 + ""));
            }
            // remove <item>
            else if (args[0].equalsIgnoreCase("remove")) {
                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    player.sendMessage(plugin.getMessages().get(LangKey.INVALID_ITEM, true, args[1]));
                    return true;
                }

                if (!dataPlayer.hasBlacklistedItem(material)) {
                    player.sendMessage(plugin.getMessages().get(LangKey.ITEM_NOT_BLACKLISTED, true, material.toString()));
                    return true;
                }

                dataPlayer.removeBlacklistedItem(material);
                player.sendMessage(plugin.getMessages().get(LangKey.REMOVED_ITEM, true, material.toString()));
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
                    player.sendMessage(plugin.getMessages().get(LangKey.INVALID_ITEM, true, args[1]));
                    return true;
                }

                try {
                    int max = Integer.parseInt(args[2]);

                    plugin.getPlayerManager().getPlayer(player.getUniqueId().toString()).addOrUpdateBlacklistedItem(material, max);
                    player.sendMessage(plugin.getMessages().get(LangKey.ADDED_ITEM, true, material.toString(), max + ""));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessages().get(LangKey.NOT_A_NUMBER, true, args[2]));
                    return true;
                }
            }
            else {
                showUsage(player);
            }
        }
        else {
            if (plugin.getSettingsManager().getProperty(GUISettings.ENABLED)) {
                new ManagementMenu(plugin, dataPlayer).show(player);
            }
            else {
                showUsage(player);
            }
        }

        return true;
    }

    private void showUsage(Player p, String usage) {
        p.sendMessage(plugin.getMessages().get(LangKey.CORRECT_USAGE, true, usage));
    }

    private void showUsage(Player p) {
        showUsage(p, "(add <item> [max] | remove <item> | list)");
    }
}
